import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class GeminiRequest(val contents: List<Content>)
data class Content(val role: String = "user", val parts: List<Part>) // Add "role"
data class Part(val text: String)
data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun getGeminiResponse(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
