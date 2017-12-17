package org.kin.erdon.mouth.db

import org.jetbrains.exposed.sql.Table

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

object Word : Table("word"){
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50)
    val pronunciation = varchar("pronunciation", length = 50)
}