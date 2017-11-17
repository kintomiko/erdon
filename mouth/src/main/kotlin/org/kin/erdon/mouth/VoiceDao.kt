package org.kin.erdon.mouth

import java.io.ByteArrayInputStream
import java.sql.*

object VoiceDao{
    fun create(fragments: Collection<Fragment>) {
        fragments.forEach {
            create(it)
        }
    }

    fun loadClip(name: String): Clip{
        var clip: Clip? = null
        runWithDbConnection(Database.erdon, true) { dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        select id, format, name
                         from clip
                         where name = ?
                        """
            )

            ps.setString(1, name)

            execSql(ps){
                while (it.next()){
                    clip = Clip(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3),
                            null
                    )
                }
            }
        }
        return clip!!
    }

    fun readWord(pronunciation: String, personId: Int): ByteArray{
        var audio = ByteArray(0)
        runWithDbConnection(Database.erdon, true){ dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        select data
                         from fragment f
                         join word w on w.id = f.word_id
                         where w.pronunciation = ? and f.person_id= ? and f.wc > 0.8
                         order by f.wc desc
                        """
            )

            ps.setString(1, pronunciation)
            ps.setInt(2, personId)

            execSql(ps){
                if (it.next()){
                    audio = it.getBytes(1)
                }
            }
        }
        return audio
    }

    private fun create(fragments: Fragment): Int {
        var newId = -1
        runWithDbConnection(Database.erdon, true){ dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        insert into fragment (clip_id, person_id, word_id, wc, wp, data) values (?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS
            )

            ps.setInt(1, fragments.clipId)
            ps.setInt(2, fragments.personId)
            ps.setInt(3, fragments.wordId)
            ps.setFloat(4, fragments.wc)
            ps.setString(5, fragments.wp)
            ps.setBinaryStream(6, ByteArrayInputStream(fragments.audio))

            newId = execUpdate(ps)
        }
        return newId
    }

    fun create(clip: Clip): Int {
        var newId = -1
        runWithDbConnection(Database.erdon, true) { dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        insert into clip (format, name) values (?, ?)
                        """, Statement.RETURN_GENERATED_KEYS
            )

            ps.setString(1, clip.format)
            ps.setString(2, clip.name)
            newId = execUpdate(ps)
        }
        return newId
    }

    fun create(person: Person): Int {
        var newId = -1
        runWithDbConnection(Database.erdon, true) { dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        insert into person (name, sex) values (?, ?)
                        """, Statement.RETURN_GENERATED_KEYS
            )

            ps.setString(1, person.name)
            ps.setString(2, person.sex.name)
            newId = execUpdate(ps)
        }
        return newId
    }

    fun create(word: Word): Int {
        var newId = -1
        runWithDbConnection(Database.erdon, true) { dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        insert into word (name, pronunciation) values (?, ?)
                        """, Statement.RETURN_GENERATED_KEYS
            )

            ps.setString(1, word.name)
            ps.setString(2, word.pronunciation)
            newId = execUpdate(ps)
        }
        return newId
    }

    fun findWord(wordsName: String): Word? {
        var word: Word? = null
        runWithDbConnection(Database.erdon, true){ dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        select id, name, pronunciation from word
                        where name = ?
                        """
            )

            ps.setString(1, wordsName)

            execSql(ps){
                if (it.next()){
                    word = Word(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
                    )
                }
            }
        }
        return word
    }
}

fun execUpdate(preparedStatement: PreparedStatement): Int{
    try {
        preparedStatement.executeUpdate()
        var rs = preparedStatement.generatedKeys
        if(rs.next()){
            return rs.getInt(1)
        }
        return -1
    } catch (e: SQLException) {
        logError(e)
        return -1
    } finally {
        preparedStatement.close()
    }
}

fun execSql(preparedStatement: PreparedStatement, work:(rs: ResultSet) -> Unit) {
    try {
        val resultSet = preparedStatement.executeQuery()
        work.invoke(resultSet)
    } catch (e: SQLException) {
        logError(e)
    } finally {
        preparedStatement.close()
    }
}

fun runWithDbConnection(dbName: Database, autoCommit: Boolean = true, work: (dbConnection: Connection) -> Unit) {
    val db = DatabaseConnection.getDBConnection(dbName)
    db.autoCommit = autoCommit
    try {
        work.invoke(db)
    } catch (e: SQLException) {
        logError(e)
    } finally {
        if (!db.isClosed) {
            db.close()
        }
    }
}