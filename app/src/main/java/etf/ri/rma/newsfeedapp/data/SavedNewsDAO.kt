package etf.ri.rma.newsfeedapp.data

import androidx.room.*
import etf.ri.rma.newsfeedapp.model.News
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsTags
import etf.ri.rma.newsfeedapp.model.TagEntity

@Dao
interface SavedNewsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: News): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTag(crossRef: NewsTags)

    @Query("SELECT * FROM News")
    suspend fun getAllNewsRaw(): List<News>

    @Query("SELECT * FROM Tags WHERE id IN (SELECT tagsId FROM NewsTags WHERE newsId = :newsId)")
    suspend fun getTagsForNews(newsId: Int): List<TagEntity>

    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun getNewsByCategoryRaw(category: String): List<News>

    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsByUuid(uuid: String): News?

    @Transaction
    suspend fun saveNews(newsItem: NewsItem): Boolean {
        val existing=getNewsByUuid(newsItem.uuid)
        if(existing!=null) return false

        val category = newsItem.category ?: return false
        val source = newsItem.source ?: return false
        val title = newsItem.title ?: return false
        val publishedDate = newsItem.publishedDate ?: return false

        val news = News(
            uuid = newsItem.uuid,
            title = title,
            category = category,
            source = source,
            publishedDate = publishedDate
        )
        return insertNews(news) != -1L
    }

    @Transaction
    suspend fun allNews(): List<NewsItem> {
        return getAllNewsRaw().map { news ->
            val tags = getTagsForNews(news.id)
            NewsItem(
                uuid = news.uuid,
                title = news.title,
                snippet = "", // nije u bazi
                category = news.category,
                imageUrl = "", // nije u bazi
                isFeatured = false, // nije u bazi
                publishedDate = news.publishedDate,
                source = news.source,
                imageTags = ArrayList(tags)
            )
        }
    }

    @Transaction
    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        return getNewsByCategoryRaw(category).map { news ->
            val tags = getTagsForNews(news.id)
            NewsItem(
                uuid = news.uuid,
                title = news.title,
                snippet = "",
                category = news.category,
                imageUrl = "",
                isFeatured = false,
                publishedDate = news.publishedDate,
                source = news.source,
                imageTags = ArrayList(tags)


            )
        }
    }

    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTagsCount = 0
        for (tag in tags) {
            val tagInsertId = insertTag(TagEntity(value = tag))
            val tagId = if (tagInsertId == -1L) {
                getTagIdByValue(tag) // uzmi postojeći ID
            } else {
                newTagsCount++
                tagInsertId
            }
            if (tagId != -1L) {
                insertNewsTag(NewsTags(newsId = newsId, tagsId = tagId.toInt()))
            }
        }
        return newTagsCount
    }



    @Query("SELECT id FROM Tags WHERE value = :value LIMIT 1")
    suspend fun getTagIdByValue(value: String): Long

    suspend fun getTags(newsId: Int): List<String> {
        return getTagsForNews(newsId).map { it.value }
    }

    @Query("""
        SELECT * FROM News WHERE id IN (
            SELECT newsId FROM NewsTags
            WHERE tagsId IN (
                SELECT id FROM Tags WHERE value IN (:tags)
            )
        )
        ORDER BY publishedDate DESC
    """)
    suspend fun getSimilarNewsRaw(tags: List<String>): List<News>

    @Query("SELECT id FROM News WHERE uuid = :uuid")
    fun getIdByUUID(uuid: String): Int



    @Transaction
    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        return getSimilarNewsRaw(tags).map { news ->
            val tagEntities = getTagsForNews(news.id) // ovo vraća List<TagEntity>
            NewsItem(
                uuid = news.uuid,
                title = news.title,
                snippet = "",
                category = news.category,
                imageUrl = "",
                isFeatured = false,
                publishedDate = news.publishedDate,
                source = news.source,
                imageTags = ArrayList(tagEntities)
            )
        }
    }

}
