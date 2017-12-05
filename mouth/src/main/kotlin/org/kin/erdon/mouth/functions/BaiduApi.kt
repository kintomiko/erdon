package org.kin.erdon.mouth.functions

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import org.json.JSONObject
import org.kin.erdon.mouth.configs.Config
import org.kin.erdon.mouth.configs.JsonFactory
import javax.sound.sampled.AudioFormat
import javax.xml.bind.DatatypeConverter

object BaiduApi {

    private val apiKey = Config.getProperty("baidu.voice.apikey")
    private val secretKey = Config.getProperty("baidu.voice.secret")

    private var token: String? = null

    init {

        val (request, response, result) = "https://openapi.baidu.com/oauth/2.0/token?".httpGet(listOf(
                "grant_type" to "client_credentials",
                "client_id" to apiKey,
                "client_secret" to secretKey
        )).responseString()

        when (result) {
            is Result.Failure -> {
            }
            is Result.Success -> {
                token = JSONObject(result.getAs<String>()).getString("access_token")
            }
        }
    }

    fun listen(buffer: ByteArray, format: AudioFormat): String {
        var recResult: String? = null

        val (request, response, result) = "http://vop.baidu.com/server_api".httpPost()
                .body(JsonFactory.get().writeValueAsString(BaiduRecoRequest(
                        rate = format.sampleRate.toInt(), token = token!!, speech = DatatypeConverter.printBase64Binary(buffer), len = buffer.size
                )))
                .response()

        when (result) {
            is Result.Failure -> {
            }
            is Result.Success -> {
                recResult = result.getAs<String>()
            }
        }
        return recResult!!

    }
}

data class BaiduRecoRequest(
        val format: String = "wav",
        val rate: Int = 16000,
        val channel: Int = 1,
        val cuid: String = "YUANMAC",
        val token: String,
        val speech: String,
        val len: Int
)