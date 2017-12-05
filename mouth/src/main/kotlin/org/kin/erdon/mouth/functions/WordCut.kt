package org.kin.erdon.mouth.functions

import com.github.kittinunf.fuel.httpGet
import org.kin.erdon.mouth.configs.Config

object WordCut {

    private val apiKey = Config.getProperty("cloud.voice.apikey")

    fun cut(sen: String): List<String> {

        val (request, response, result) =
        "http://api.ltp-cloud.com/analysis/?api_key=${apiKey}&text=$sen&pattern=ws&format=plain"
                .httpGet().responseString()
        return result.get().split(" ")
    }

}
