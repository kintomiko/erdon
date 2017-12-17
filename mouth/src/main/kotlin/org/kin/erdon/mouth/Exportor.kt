package org.kin.erdon.mouth

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kin.erdon.mouth.configs.Config
import org.kin.erdon.mouth.db.VoiceDao
import org.kin.erdon.mouth.db.Word
import org.kin.erdon.mouth.models.Fragment
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

val STEP = 10

fun main(args: Array<String>){

    Database.connect(
            Config.getRequiredProperty("db.erdon.url"),
            Config.getRequiredProperty("db.erdon.driver"),
            Config.getRequiredProperty("db.erdon.username"),
            Config.getRequiredProperty("db.erdon.password")
    )

    val fileEntries = mutableListOf<Array<String>>()

    transaction {
        (0..VoiceDao.totalFragment() step STEP).forEachIndexed { index, i ->

            VoiceDao.fragments(i, STEP).forEach {
                println("converting fragment id: ${it.id}")
                fileEntries.add(exportToFile(it, "output", "wav"))
            }

        }
    }

    val outCsv = File("output/out.csv")

    outCsv.bufferedWriter().use { out ->
        fileEntries.forEach {
            out.write(it.joinToString(","))
            out.newLine()
        }
    }

}

fun exportToFile(f: Fragment, exportFolder: String = "./", subFolder: String = "wav"): Array<String> {
    val clip = VoiceDao.clip(f.clipId)!!
    val origin = AudioInputStream(ByteArrayInputStream(f.audio), toAudioFormat(clip.format), f.audio.size.toLong())

    val word = Word.select{
        Word.id.eq(f.wordId)
    }.first()

    val pronunciation = word[Word.pronunciation].replace(",", "_")
    val path = "$subFolder/${pronunciation}_${f.id}.wav"
    val outFile = File("$exportFolder/$path")
    AudioSystem.write(origin, AudioFileFormat.Type.WAVE, outFile)

    return arrayOf(path, pronunciation, origin.frameLength.toString())
}
