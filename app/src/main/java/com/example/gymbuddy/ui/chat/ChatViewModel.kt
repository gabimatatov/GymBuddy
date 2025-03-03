import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.networking.Content
import com.example.gymbuddy.networking.GeminiRequest
import com.example.gymbuddy.networking.Part
import com.example.gymbuddy.networking.RetrofitInstance
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {
    fun sendMessage(userMessage: String, onResponse: (String) -> Unit) {
        val prompt = """
            You are GymBuddy, a friendly and knowledgeable assistant focused on sports and workouts. 
            
            Provide **concise yet informative** responses related to fitness, exercises, and workouts. 
            If the question is **not about sports or workouts**, say something fun like:  
            "Oops! Looks like you're asking about something outside the gym."  
            
            Avoid overly long responses. Use **numbers** where relevant.  
            At the end of your response, ask for more questions or fitness advice.  
            
            Question: $userMessage
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(prompt)))
            )
        )

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Sending request: $request")

                // No longer need to pass the API key here
                val response = RetrofitInstance.api.getGeminiResponse(request)
                Log.d("ChatViewModel", "API Response: $response")

                val reply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response"
                onResponse(reply)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error: ${e.message}")
                onResponse("Error: ${e.message}")
            }
        }
    }
}