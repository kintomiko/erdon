package org.kin.erdon.mouth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.TypeRef
import java.io.File
import javax.sound.sampled.AudioSystem
import java.util.EnumSet
import com.jayway.jsonpath.spi.mapper.MappingProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.Option
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream


fun main(args: Array<String>){

    Configuration.setDefaults(object : Configuration.Defaults {
        private val kotlinObjectMapper = jacksonObjectMapper()
        private val jsonProvider = JacksonJsonProvider(kotlinObjectMapper)
        private val mappingProvider = JacksonMappingProvider(kotlinObjectMapper)

        override fun jsonProvider(): JsonProvider {
            return jsonProvider
        }

        override fun mappingProvider(): MappingProvider {
            return mappingProvider
        }

        override fun options(): Set<Option> {
            return EnumSet.noneOf(Option::class.java)
        }
    })

    val wavReader = WavReader("/Users/kindai/workspace/erdon/mouth/speech/cuts-yuan-ycdj.wav")
    val cuts = parseCutsFromFile("/Users/kindai/workspace/erdon/mouth/speech/cuts-yuan-ycdj.json")
    val fragments = VoiceSplitter.splitVoice(wavReader, cuts)
    VoiceDao.save(fragments)
}

class WavReader {
    private val audioInputStream: AudioInputStream
    private val buffer: ByteArray
    val format: AudioFormat

    constructor(path: String) {
        audioInputStream = AudioSystem.getAudioInputStream(File(path))
        buffer = ByteArray(audioInputStream.frameLength.toInt()*audioInputStream.format.frameSize)
        audioInputStream.read(buffer)
        format = audioInputStream.format
    }

    fun loadWord(startInMillis: Long, endInMillis: Long): ByteArray {
        val start = (startInMillis * format.frameRate * format.frameSize/1000).toInt()
        val end = (endInMillis * format.frameRate * format.frameSize/1000).toInt()
        return buffer.copyOfRange(start, end)
    }
}

fun parseCutsFromFile(path: String): List<Cut> {
    val json = String(File(path).readBytes())
    val typeRef = object : TypeRef<List<CutGroup>>() {}
    val sentenceGroups = JsonPath.parse(json).read("\$.result", typeRef)
    val cuts = mutableListOf<Cut>()
    sentenceGroups.forEach {
        val start = it.bg
        it.wordsResultList.forEach {
            if(it.wordBg < it.wordEd) {
                cuts.add(Cut(
                        it.wordBg * 10 + start,
                        it.wordEd * 10 + start,
                        it.wordsName,
                        it.wc,
                        it.wp
                ))
            }
        }
    }
    return cuts
}
