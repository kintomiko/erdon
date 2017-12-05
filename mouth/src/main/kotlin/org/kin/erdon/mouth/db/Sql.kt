package org.kin.erdon.mouth.db

object Sql{
    val selectFragment = """
                        select id, clip_id, person_id, word_id, wc, wp, data
                        from fragment
                        limit ?, ?
                        """

    val selectClip = """
                        select id, format, name
                        from clip
                        where id = ?
                        """
}