import android.util.Base64
import org.json.JSONObject

object JwtDecoder {
    fun getUserId(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            // предполагаем, что в payload есть поле "userId" или "user_id"
            json.optLong("userId", json.optLong("user_id", 0))
        } catch (e: Exception) {
            null
        }
    }
}