package org.kin.erdon.mouth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

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

data class Word(
    val id: Int?,
    val name: String,
    val pronunciation: String
)

data class Fragment(
        val id: Int?,
        val clipId: Int,
        val personId: Int,
        val wordId: Int,
        val audio: ByteArray,
        val wc: Float,
        val wp: String
)

data class Person(
        val id: Int?,
        val name: String,
        val sex: Sex
)

enum class Sex{
    MALE, FEMALE
}

data class Clip(
        val id: Int?,
        val format: String,
        val name: String,
        val audio: ByteArray?
)