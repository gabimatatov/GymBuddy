import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class GeminiRequest(val contents: List<Content>)
data class Content(val role: String = "user", val parts: List<Part>)
data class Part(val text: String)
data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: CandidateContent)  // Fix this to match the API response
data class CandidateContent(val parts: List<Part>)   // Add this class back

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun getGeminiResponse(
        @Body request: GeminiRequest
    ): GeminiResponse
}