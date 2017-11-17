package org.kin.erdon.mouth

import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * A simple property config wrapper that initially loads a set of properties from
 * a configured config_file parameter or defaults to local setup.
 *
 * For individual property lookups we look first to System property, fallback to
 * Environment property, and if not overridden at either of those levels, we look
 * in the properties array that came from the original config file.
 *
 * For Consul/bootstrap we have 2 use cases.
 *
 * 1. Terraform/lambda where the consul lookup props are introduced by ENV settings
 * 2. Library form (from raken-api/insights-query) where the consul lookup props are brought in by file or -D
 */
object Config {

    const val INSIGHTS_PROPERTY_CONFIG_FILE = "config_file"

    val file = configLocation()

    var props: Properties

    init {
        log("Config file location: ${file}")
        props = if (file != null) readProperties(file) else Properties()
    }

    private fun configLocation():String? {
        getProperty(INSIGHTS_PROPERTY_CONFIG_FILE)?.let { value ->
            println("Located config from JVM property pointer $value")
            return value
        }
        if (Files.exists(Paths.get("src/main/resources/erdon.conf"))) {
            println("Located config at src/main/resources")
            return "src/main/resources/erdon.conf"
        }
        return null
    }

    fun getPropertyAsInt(key:String):Int {
        val value = getProperty(key)
        value?.let {
            return value.toInt()
        }
        throw RuntimeException("Property ${key} not found!")
    }

    fun getPropertyAsBoolean(key:String):Boolean {
        val value = getProperty(key)
        value?.let {
            return value.toBoolean()
        }
        throw RuntimeException("Property ${key} not found!")
    }

    fun getProperty(key:String, default:String):String {
        return getProperty(key) ?: default
    }

    fun getProperty(key:String):String? {
        return getPropertyInCascade(key)
    }

    fun getRequiredProperty(key:String):String {
        return getPropertyInCascade(key) ?: throw RuntimeException("Property ${key} marked as required but not present!")
    }

    fun getPropertyInCascade(key:String):String? {

        System.getProperty(key)?.let {
            return System.getProperty(key)
        }
        System.getenv(key)?.let {
            return System.getenv(key)
        }

        //These two check for variations on the property name based on lambda env prop naming limitations
        System.getProperty(getLambdaIfiedPropertyName(key))?.let {
            return System.getProperty(getLambdaIfiedPropertyName(key))
        }
        System.getenv(getLambdaIfiedPropertyName(key))?.let {
            return System.getenv(getLambdaIfiedPropertyName(key))
        }

        props?.let { propSet ->
            propSet[key]?.let { prop ->
                return prop as String
            }
        }
        return null
    }

    fun getLambdaIfiedPropertyName(propName:String):String {
        return propName.replace(".", "_")
    }

    private fun readProperties(file:String): Properties = Properties().apply {
        FileInputStream(file).use { fis ->
            load(fis)
        }
    }

}