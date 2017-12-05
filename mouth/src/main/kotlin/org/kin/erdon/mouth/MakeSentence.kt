package org.kin.erdon.mouth

import com.jayway.jsonpath.JsonPath
import org.kin.erdon.mouth.configs.JsonFactory
import org.kin.erdon.mouth.db.VoiceDao
import org.kin.erdon.mouth.functions.BaiduApi
import org.kin.erdon.mouth.functions.WordCut
import org.kin.erdon.mouth.functions.pinyin
import javax.sound.sampled.AudioFormat

fun main(args: Array<String>){
    val sen = """
        朱莉娅不知道
        """
    val words = WordCut.cut(sen)
//    val words = listOf("你", "是", "个", "工作狂")

    val buffer = words.mapNotNull {
        println("matching word: $it")

        VoiceDao.readWord(pinyin(it), 1)?.let {
            println("matched fragment: ${JsonPath.using(JsonFactory.conf).parse(it).jsonString()}")
            it.audio
        }

    }.fold(ByteArray(0), { buffera, bufferb ->
        val merged = ByteArray(buffera.size+bufferb.size)
        System.arraycopy(buffera, 0, merged, 0, buffera.size)
        System.arraycopy(bufferb, 0, merged, buffera.size, bufferb.size)
        merged
    })

    val format = AudioFormat(24000f, 16, 1, true, false)

    val result = listen(buffer, format)

//    val recResult = listen(buffer, format)

//    val clip = AudioSystem.getClip()
//    clip.open(format, buffer, 0, buffer.size)
//    clip.start()
//    clip.drain()

//    Thread.sleep(30*1000)

}

fun listen(buffer: ByteArray, format: AudioFormat): String = BaiduApi.listen(buffer, format)