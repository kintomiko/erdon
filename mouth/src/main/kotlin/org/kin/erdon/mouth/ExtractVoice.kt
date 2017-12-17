package org.kin.erdon.mouth

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.TypeRef
import org.kin.erdon.mouth.configs.JsonFactory
import org.kin.erdon.mouth.functions.VoiceExtractor
import org.kin.erdon.mouth.models.Cut
import org.kin.erdon.mouth.models.CutGroup
import org.kin.erdon.mouth.models.Person
import org.kin.erdon.mouth.models.Sex
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem


fun main(args: Array<String>){

    listOf(
            "/Users/kindai/workspace/erdon/mouth/speech/yuan-mmxlgd",
            "/Users/kindai/workspace/erdon/mouth/speech/yuan-qxljgy",
            "/Users/kindai/workspace/erdon/mouth/speech/yuan-xlhdd",
            "/Users/kindai/workspace/erdon/mouth/speech/cuts-yuan-ycdj",
                "/Users/kindai/workspace/erdon/mouth/speech/Rec_007"
            ).forEach {
        println("processing $it.wav")
        val wavReader = WavReader("$it.wav")
        println("processing $it.json")
//        val cuts = parseCutsFromFile("$it.json")
//        VoiceExtractor.extract(SingleExtractRequest(wavReader, cuts, Person(1, "Yuan Lin", Sex.MALE), it))

        val groups = parseSentancesFromFile("$it.json")
        VoiceExtractor.extractToFile(groups, wavReader, "output", it.substring(it.lastIndexOf("/")))
    }
}

data class SingleExtractRequest(
        val reader: WavReader,
        val cuts: List<Cut>,
        val person: Person,
        val clipName: String
)

class WavReader(path: String) {
    private val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(path))
    private val buffer: ByteArray

    fun loadWord(startInMillis: Long, endInMillis: Long): ByteArray {
        val start = (startInMillis * format().frameRate * format().frameSize/1000).toInt()
        val end = (endInMillis * format().frameRate * format().frameSize/1000).toInt()
        return buffer.copyOfRange(start, end)
    }

    fun buffer() = buffer

    fun format() = audioInputStream.format!!

    init {
        buffer = ByteArray(audioInputStream.frameLength.toInt()*audioInputStream.format.frameSize)
        audioInputStream.read(buffer)
    }
}

fun parseCutsFromFile(path: String): List<Cut> {
    val json = String(File(path).readBytes())
    val typeRef = object : TypeRef<List<CutGroup>>() {}
    val sentenceGroups = JsonPath.using(JsonFactory.conf).parse(json).read("\$.result", typeRef)
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

fun parseSentancesFromFile(path: String): List<CutGroup> {
    val json = String(File(path).readBytes())
    val typeRef = object : TypeRef<List<CutGroup>>() {}
    return JsonPath.using(JsonFactory.conf).parse(json).read("\$.result", typeRef)
}