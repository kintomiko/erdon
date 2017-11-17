package org.kin.erdon.mouth

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

object WordCut {

    val apiKey = Config.getProperty("cloud.voice.apikey")

    fun cut(sen: String): List<String> {

        val (request, response, result) =
        "http://api.ltp-cloud.com/analysis/?api_key=$apiKey&text=$sen&pattern=ws&format=plain"
                .httpGet().responseString()
        return result.get().split(" ")
    }

}
