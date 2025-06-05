package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.InvalidImageURLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

object ImageDAO {
    private val cache = mutableMapOf<String, List<String>>()
    private lateinit var api: ImaggaApi

    fun setApiService(service: ImaggaApi) {
        api = service
    }

    suspend fun getTags(imageUrl: String): List<String> = withContext(Dispatchers.IO) {
        val normalizedUrl = imageUrl.trim() // osiguraj konzistentan unos

        if (!isValidUrl(normalizedUrl))
            throw InvalidImageURLException("Invalid image URL: $normalizedUrl")

        //  KEŠIRANO?
        cache[normalizedUrl]?.let {
            return@withContext it
        }

        val response = api?.getImageTags(normalizedUrl, ImaggaApiConstants.AUTH_HEADER)
        if (response?.isSuccessful == true) {
            val tags = response.body()?.result?.tags?.mapNotNull { it.tag?.en } ?: emptyList()
            cache[normalizedUrl] = tags // UPIS U CACHE
            tags
        } else {
            emptyList()
        }
    }


    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}
