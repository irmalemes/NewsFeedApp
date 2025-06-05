package etf.ri.rma.newsfeedapp

import etf.ri.rma.newsfeedapp.data.ImageDAO
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.data.ImaggaApi
import etf.ri.rma.newsfeedapp.data.NewsApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestS3PripremljenRetrofit {

    fun getNewsDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): NewsDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val newsApiService = retrofit.create(NewsApiService::class.java)
        val newsDAO = NewsDAO
        newsDAO.setApiService(newsApiService)
        return newsDAO
    }

    fun getImaggaDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): ImageDAO {
        val correctedBaseUrl = if (!baseURL.endsWith("v2/")) baseURL + "v2/" else baseURL

        val retrofit = Retrofit.Builder()
            .baseUrl(correctedBaseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val imagaApiService = retrofit.create(ImaggaApi::class.java)
        val imagaDAO = ImageDAO
        imagaDAO.setApiService(imagaApiService)
        return imagaDAO
    }

}
