package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity

@Entity(tableName = "NewsTags", primaryKeys = ["newsId", "tagsId"])
data class NewsTags(
    val newsId: Int,
    val tagsId: Int
)

