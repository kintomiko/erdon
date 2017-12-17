package org.kin.erdon.mouth.functions

import org.kin.erdon.mouth.models.Clip
import org.kin.erdon.mouth.models.Fragment
import org.kin.erdon.mouth.SingleExtractRequest
import org.kin.erdon.mouth.WavReader
import org.kin.erdon.mouth.models.WordDto
import org.kin.erdon.mouth.db.VoiceDao
import org.kin.erdon.mouth.models.CutGroup
import org.kin.erdon.mouth.toAudioFormat
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

object VoiceExtractor {

    fun extractToFile(sentences: List<CutGroup>, reader: WavReader, exportFolder: String = "./", subFolder: String = "wav") {

        val fileEntries = mutableListOf<Array<String>>()

        File(exportFolder)?.let {
            if(!it.exists()){
                it.mkdir()
            }
        }

        File("$exportFolder/$subFolder/")?.let {
            if(!it.exists()){
                it.mkdir()
            }
        }

        sentences.forEach {
            val data = reader.loadWord(it.bg, it.ed)
            val pronunciation = pinyin(it.onebest).replace(",", "_")
            val origin = AudioInputStream(ByteArrayInputStream(data), reader.format(), data.size.toLong())
            val path = "$subFolder/$pronunciation.wav"
            val outFile = File("$exportFolder/$path")
            AudioSystem.write(origin, AudioFileFormat.Type.WAVE, outFile)

            fileEntries.add(arrayOf(path, pronunciation, origin.frameLength.toString()))
        }

        val outCsv = File("$exportFolder/$subFolder.csv")

        outCsv.bufferedWriter().use { out ->
            fileEntries.forEach {
                out.write(it.joinToString(","))
                out.newLine()
            }
        }

    }


    fun extract(request: SingleExtractRequest) {
        val clipId = VoiceDao.create(Clip(
                null,
                request.reader.format().toString(),
                request.clipName,
                request.reader.buffer()
        ))

        val personId = if (request.person.id != null){
            request.person.id
        }else{
            VoiceDao.create(request.person)
        }
        VoiceDao.create(request.cuts.map {
            val word = VoiceDao.findWord(it.wordsName)

            val wordId = word?.id ?: VoiceDao.create(
                    WordDto(
                            null,
                            it.wordsName,
                            pinyin(it.wordsName)
                    )
            )
            println("creating fragment ${it.wordsName}")
            Fragment(
                    null,
                    clipId,
                    personId,
                    wordId,
                    request.reader.loadWord(it.wordBg, it.wordEd),
                    it.wc,
                    it.wp
            )
        })
    }

}