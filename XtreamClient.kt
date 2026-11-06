package com.gatostv.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class AccountInfo(
    val username: String,
    val status: String,
    val expiration: String?
)

class XtreamClient(
    serverInput: String,
    private val username: String,
    private val password: String
) {
    private val server = serverInput.trim().trimEnd('/')
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun authenticate(): AccountInfo = withContext(Dispatchers.IO) {
        require(server.startsWith("http://") || server.startsWith("https://")) {
            "Informe o servidor incluindo http:// ou https://"
        }
        val url = "$server/player_api.php?username=${enc(username)}&password=${enc(password)}"
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Servidor respondeu HTTP ${response.code}")
            val body = response.body?.string() ?: error("Resposta vazia do servidor")
            val json = JSONObject(body)
            val userInfo = json.optJSONObject("user_info") ?: error("Resposta Xtream Codes inválida")
            val auth = userInfo.optInt("auth", 0)
            if (auth != 1) error("Usuário ou senha não autorizados")
            val exp = userInfo.optString("exp_date").takeIf { it.isNotBlank() && it != "null" && it != "0" }
            AccountInfo(
                username = userInfo.optString("username", username),
                status = userInfo.optString("status", "Ativo"),
                expiration = exp
            )
        }
    }

    private fun enc(value: String) = URLEncoder.encode(value, Charsets.UTF_8.name())
}
