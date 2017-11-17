package org.kin.erdon.mouth

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main(args: Array<String>){
    val sen = """
        朱莉娅不知道，她的肚子里有一个火车站！
肚子精灵们就住在这里，他们的工作是把食物弄成泥。
这会儿，小精灵们都懒洋洋地躺着，因为大家无事可做。
火车也停在那里，整个肚子火车站里静悄悄的。
        """
    val words = WordCut.cut(sen)
//    val words = listOf("你", "是", "个", "工作狂")

    val buffer = words.map {
        VoiceDao.readWord(pinyin(it), 1)
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

    Thread.sleep(30*1000)

}