package org.kin.erdon.mouth

import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper

fun pinyin(word: String): String{
    return PinyinHelper.convertToPinyinString(word, ",", PinyinFormat.WITH_TONE_NUMBER)
}