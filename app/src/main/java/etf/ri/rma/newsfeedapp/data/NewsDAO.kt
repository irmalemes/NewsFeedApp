package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.util.NewsApiConstants
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

object NewsDAO {

    private var apiService: NewsApiService? = null

    fun setApiService(service: NewsApiService) {
        apiService = service
        topStoriesCache.clear()
        similarStoriesCache.clear()
    }


    private val allNewsCache = mutableListOf<NewsItem>()
    private val topStoriesCache = mutableMapOf<String, CachedNews>()
    private val similarStoriesCache = mutableMapOf<String, List<NewsItem>>()
    private val mutex = Mutex()

    private const val CACHE_EXPIRY_MS = 30_000L

    private data class CachedNews(val data: List<NewsItem>, val timestamp: Long)

    init {
        allNewsCache.addAll(NewsData.getAllNews().map {
            it.copy(
                uuid = UUID.randomUUID().toString(),
                imageTags = arrayListOf()
            )
        })
    }

    fun reset() {
        apiService = null
        topStoriesCache.clear()
        similarStoriesCache.clear()
    }

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val currentTime = System.currentTimeMillis()

        val apiCategory = when (category.lowercase()) {
            "politika" -> "politics"
            "sport" -> "sports"
            "nauka/tehnologija" -> "science,technology"
            "moda" -> "fashion"
            "sve" -> ""
            else -> category.lowercase()
        }

        val cached = topStoriesCache[category]
        if (cached != null && (currentTime - cached.timestamp < CACHE_EXPIRY_MS)) {
            return cached.data
        }

        val response = try {
            apiService?.getTopStoriesByCategory(apiCategory)
        } catch (e: Exception) {
            null
        }

        return if (response?.isSuccessful == true) {
            val news = response.body()?.news ?: emptyList()
            topStoriesCache[category] = CachedNews(news, currentTime)
            news
        } else {
            emptyList()
        }
    }


    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!isValidUUID(uuid)) throw InvalidUUIDException("UUID $uuid nije validan")

        apiService?.let { service ->
            return try {
                val response = service.getSimilarStories(uuid)
                if (response.isSuccessful) {
                    response.body()?.news ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

        return localSimilarStories(uuid)
    }

    fun getAllStories(): List<NewsItem> {
        return allNewsCache.toList()
    }

    private fun isValidUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private suspend fun localSimilarStories(uuid: String): List<NewsItem> {
        mutex.withLock {
            val currentItem = allNewsCache.find { it.uuid == uuid }
                ?: throw InvalidUUIDException("UUID $uuid nije pronađen")

            similarStoriesCache[uuid]?.let { return it }

            val similar = allNewsCache.filter {
                it.uuid != uuid && it.category == currentItem.category
            }.shuffled().take(2)

            similarStoriesCache[uuid] = similar
            return similar
        }
    }

    class InvalidUUIDException(message: String) : Exception(message)
}
