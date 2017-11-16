package org.kin.erdon.mouth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

object VoiceSplitter{

    fun splitVoice(wavReader: WavReader, cuts: List<Cut>) : Collection<Fragment> {
        return cuts.map {
            Fragment(
                    it.wordsName,
                    wavReader.loadWord(it.wordBg, it.wordEd),
                    it.wc,
                    it.wp,
                    wavReader.format.toString())
        }
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CutGroup(
        val bg: Long,
        val ed: Long,
        val wordsResultList: List<Cut>
)

@JsonIgnoreProperties(ignoreUnknown = true)
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
        val wp: String,
        val format: String
)
