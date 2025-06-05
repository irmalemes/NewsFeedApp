package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.ImageTagResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ImaggaApi {
    @GET("tags")
    suspend fun getImageTags(
        @Query("image_url") imageUrl: String,
        @Header("Authorization") authHeader: String
    ): Response<ImageTagResponse>
}
