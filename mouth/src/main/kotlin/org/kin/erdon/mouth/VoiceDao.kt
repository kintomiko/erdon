package org.kin.erdon.mouth

import java.io.ByteArrayInputStream
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object VoiceDao{
    fun save(fragments: Collection<Fragment>) {
        fragments.forEach {
            save(it)
        }
    }

    fun readWord(word: String): Fragment{
        var fragment: Fragment? = null
        runWithDbConnection(Database.erdon, true){ dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        select wordsName, wc, wp, data, format
                         from voice where wordsName = ?
                        """
            )

            ps.setString(1, word)

            execSql(ps){
                while (it.next()){
                    fragment = Fragment(
                            it.getString(1),
                            it.getBytes(4),
                            it.getFloat(2),
                            it.getString(3),
                            it.getString(5)
                    )
                }
            }
        }
        return fragment!!
    }

    private fun save(fragments: Fragment) {
        runWithDbConnection(Database.erdon, true){ dbConnection ->
            val ps = dbConnection.prepareStatement(
                    """
                        insert into voice (wordsName, wc, wp, data, format) values (?, ?, ?, ?, ?)
                        """
            )

            ps.setString(1, fragments.wordsName)
            ps.setFloat(2, fragments.wc)
            ps.setString(3, fragments.wp)
            ps.setBinaryStream(4, ByteArrayInputStream(fragments.audio))
            ps.setString(5, fragments.format)

            execUpdate(ps)
        }
    }
}

fun execUpdate(preparedStatement: PreparedStatement){
    try {
        preparedStatement.executeUpdate()
    } catch (e: SQLException) {
        logError(e)
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