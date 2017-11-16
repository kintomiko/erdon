package org.kin.erdon.mouth

import com.mchange.v2.c3p0.ComboPooledDataSource
import java.sql.Connection
import java.sql.SQLException

object DatabaseConnection {

    private val connections = mutableMapOf<Database, ComboPooledDataSource>()

    fun getDBConnection(db: Database): Connection {

        if (!connections.containsKey(db)) {

            try {

                connections.put(db, createPool(db))

            } catch (e: SQLException) {
                logError(e)
                throw e

            }

        }
        val pool = connections.get(db)!!
        if (pool.numBusyConnections >= pool.maxPoolSize) {
            log("All connections are busy right now, so the application may hang now.")
        }
        return pool.connection

    }

    private fun createPool(db: Database):ComboPooledDataSource {

        val cpds = ComboPooledDataSource(db.name)
        cpds.driverClass = Config.getRequiredProperty("db.${db.name}.driver") //loads the jdbc driver
        cpds.jdbcUrl = Config.getRequiredProperty("db.${db.name}.url")
        cpds.user = Config.getRequiredProperty("db.${db.name}.username")
        cpds.password = Config.getRequiredProperty("db.${db.name}.password")

// the settings below are optional -- c3p0 can work with defaults
        //TODO: Configurable!
        cpds.minPoolSize = 5
        cpds.acquireIncrement = 5
        cpds.maxPoolSize = Config.getRequiredProperty("db.${db.name}.pool.size")!!.toInt()
        cpds.isAutoCommitOnClose = true
        cpds.maxIdleTime = 120
        cpds.isTestConnectionOnCheckin = true
        cpds.isTestConnectionOnCheckout = true
//        cpds.maxConnectionAge = 300

        return cpds
    }
}

enum class Database {
    erdon
}
