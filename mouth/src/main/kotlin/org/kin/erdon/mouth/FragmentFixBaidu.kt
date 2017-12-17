package org.kin.erdon.mouth

import org.kin.erdon.mouth.db.VoiceDao
import org.kin.erdon.mouth.models.Fragment
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

fun main(args: Array<String>){

    for(i in 0..VoiceDao.totalFragment() step 10){
        VoiceDao.fragments(i, 10).forEach {
            println("converting fragment id: ${it.id}")
            val newData = convertTo16K(it)
            VoiceDao.updateFragmentData(newData, it.id!!)
        }
    }

}

fun convertTo16K(fragment: Fragment): ByteArray {
    val clip = VoiceDao.clip(fragment.clipId)!!
    val origin = AudioInputStream(ByteArrayInputStream(fragment.audio), toAudioFormat(clip.format), fragment.audio.size.toLong())
    val newFormatInputStream = AudioSystem.getAudioInputStream(baiduFormat, origin)
    val buffer = ByteArray((origin.frameLength.toInt() * newFormatInputStream.format.frameRate / origin.format.frameRate * newFormatInputStream.format.frameSize).toInt())
    newFormatInputStream.read(buffer)
    return buffer
}

val baiduFormat = AudioFormat(
        16000.0F,
        16,
        1,
        true,
        false
)

fun toAudioFormat(formatStr: String): AudioFormat? {
//    PCM_SIGNED 24000.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian
    val formats = formatStr.split(",").map { it.trim() }
    return AudioFormat(
            formats[0].split(" ")[1].toFloat(),
            formats[1].split(" ")[0].toInt(),
            if (formats[2] == "mono") 1 else 2,
            !formats[0].contains("UNSIGNED"),
            formats[4] != "little-endian"
            )
}