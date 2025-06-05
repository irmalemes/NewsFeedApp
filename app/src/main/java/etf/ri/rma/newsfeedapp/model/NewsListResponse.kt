package etf.ri.rma.newsfeedapp.model

import com.google.gson.annotations.SerializedName

data class NewsListResponse(
    @SerializedName("data")
    val news: List<NewsItem>
)
