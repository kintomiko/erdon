package org.kin.erdon.mouth.functions

import org.kin.erdon.mouth.models.Clip
import org.kin.erdon.mouth.models.Fragment
import org.kin.erdon.mouth.SingleExtractRequest
import org.kin.erdon.mouth.models.Word
import org.kin.erdon.mouth.db.VoiceDao

object VoiceExtractor {

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
                    Word(
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