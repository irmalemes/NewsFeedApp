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

    private var savedNewsDAO: SavedNewsDAO? = null


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

            // Spremanje vijesti i tagova u lokalnu bazu
            savedNewsDAO?.let { dao ->
                news.forEach { item ->
                    val wasSaved = dao.saveNews(item)
                    if (wasSaved) {
                        val newsId = dao.getIdByUUID(item.uuid)
                        try {
                            val tags = ImageDAO.getTags(ImaggaApiConstants.IMAGE_BASE_URL + item.imageUrl)
                            dao.addTags(tags, newsId)
                        } catch (_: Exception) {
                            // Ako padne dohvat tagova, samo ignoriši
                        }
                    }
                }
            }

            news
        } else {
            emptyList()
        }
    }



    fun setSavedNewsDAO(dao: SavedNewsDAO) {
        savedNewsDAO = dao
    }

    fun getSavedNewsDAO(): SavedNewsDAO? {
        return savedNewsDAO
    }



    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!isValidUUID(uuid)) throw InvalidUUIDException("UUID $uuid nije validan")

        // 1. Pokušaj sa API-em
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

        // 2. Lokalna logika ako API ne radi
        return savedNewsDAO?.let { dao ->
            try {
                val newsId = dao.getIdByUUID(uuid)
                val tags = dao.getTags(newsId).take(2) // uzimamo max 2 taga

                val candidates = dao.getSimilarNews(tags) // vijesti koje dijele makar jedan tag
                    .filter { it.uuid != uuid } // izbacimo originalnu
                    .sortedByDescending { it.publishedDate } // najnovije prvo
                    .take(2) // maksimalno 2 vijesti

                candidates
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }


    suspend fun getTagsForNews(news: NewsItem): List<String> {
        val dao = savedNewsDAO ?: return emptyList()
        val newsId = dao.getIdByUUID(news.uuid)

        val existingTags = dao.getTags(newsId)
        if (existingTags.isNotEmpty()) {
            return existingTags
        }

        // Ako nema u bazi – pozivaj Imagga API
        val imageUrl = ImaggaApiConstants.IMAGE_BASE_URL + news.imageUrl
        val tagsFromApi = try {
            ImageDAO.getTags(imageUrl)
        } catch (e: Exception) {
            emptyList()
        }

        if (tagsFromApi.isNotEmpty()) {
            dao.addTags(tagsFromApi, newsId)
        }

        return tagsFromApi
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
