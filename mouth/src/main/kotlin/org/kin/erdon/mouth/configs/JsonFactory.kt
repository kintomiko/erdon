package org.kin.erdon.mouth.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider

object JsonFactory{

    val conf: Configuration
    private val kotlinObjectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        conf = Configuration.ConfigurationBuilder()
                .jsonProvider(JacksonJsonProvider(kotlinObjectMapper))
                .mappingProvider(JacksonMappingProvider(kotlinObjectMapper))
                .build()
    }

    fun get(): ObjectMapper = kotlinObjectMapper
}