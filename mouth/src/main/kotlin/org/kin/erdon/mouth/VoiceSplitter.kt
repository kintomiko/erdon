package org.kin.erdon.mouth

object VoiceSplitter{

    fun splitVoice(rawAudio: ByteArray, cuts: List<Cut>) : Collection<Fragment> {
        return listOf()
    }

}

data class Cut(
        val wordBg: Long,
        val wordEd: Long,
        val wordsName: String,
        val wc: Float,
        val wp: String
)

data class Fragment(
        val wordsName: String,
        val audio: ByteArray,
        val wc: Float,
        val wp: String
)
