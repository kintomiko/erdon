package org.kin.erdon.mouth

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.thread

fun main(args: Array<String>){
//    val sen = "新鲜的牙齿大街"
//    val words = WordCut.cut(sen)
    val words = listOf("你", "是", "个", "工作狂")
    val buffer = words.map {
        VoiceDao.readWord(it).audio
    }.fold(ByteArray(0), { buffera, bufferb ->
        val merged = ByteArray(buffera.size+bufferb.size)
        System.arraycopy(buffera, 0, merged, 0, buffera.size)
        System.arraycopy(bufferb, 0, merged, buffera.size, bufferb.size)
        merged
    })

    val format = AudioFormat(24000f, 16, 1, true, false)

    val clip = AudioSystem.getClip()
    clip.open(format, buffer, 0, buffer.size)
    clip.start()
    clip.drain()

}