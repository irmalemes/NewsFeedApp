package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsListResponse
import etf.ri.rma.newsfeedapp.util.NewsApiConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {

    @GET("news/top")
    suspend fun getTopStoriesByCategory(
        @Query("categories") category: String,
        @Query("api_token") token: String = NewsApiConstants.API_TOKEN
    ): Response<NewsListResponse>

    @GET("news/similar/{uuid}")
    suspend fun getSimilarStories(
        @Path("uuid") uuid: String,
        @Query("api_token") token: String = NewsApiConstants.API_TOKEN
    ): Response<NewsListResponse>
}
