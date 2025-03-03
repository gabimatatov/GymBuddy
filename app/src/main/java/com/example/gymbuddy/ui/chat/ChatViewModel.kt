import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.BuildConfig
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {
    private val apiKey = BuildConfig.GEMINI_API_KEY // Load API Key from BuildConfig

    fun sendMessage(userMessage: String, onResponse: (String) -> Unit) {
        val request = GeminiRequest(
            contents = listOf(
                Content(role = "user", parts = listOf(Part(userMessage))) // Ensure "role" is included
            )
        )

        viewModelScope.launch {
            try {
                // ✅ Debug Log for API Key
                Log.d("ChatViewModel", "API Key: $apiKey")

                // ✅ Debug Log for Request Data
                Log.d("ChatViewModel", "Sending request: $request")

                val response = RetrofitInstance.api.getGeminiResponse(apiKey, request)

                // ✅ Debug Log for Response Data
                Log.d("ChatViewModel", "API Response: $response")

                val reply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response"
                onResponse(reply)
            } catch (e: Exception) {
                // ✅ Debug Log for Errors
                Log.e("ChatViewModel", "Error: ${e.message}")
                onResponse("Error: ${e.message}")
            }
        }
    }
}