package etf.ri.rma.newsfeedapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import etf.ri.rma.newsfeedapp.data.NewsApiService
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.SavedNewsDAO
import etf.ri.rma.newsfeedapp.navigation.AppNavGraph
import etf.ri.rma.newsfeedapp.ui.theme.NewsFeedAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var db: NewsDatabase
    private lateinit var savedNewsDAO: SavedNewsDAO



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicijalizacija Room baze
        db = Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java,
            "news-db"
        ).build()

        savedNewsDAO = db.savedNewsDAO()
        NewsDAO.setSavedNewsDAO(savedNewsDAO) // prosljeđivanje DAO-a u NewsDAO

        // ✅ Inicijalizacija Retrofit API-ja
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thenewsapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(NewsApiService::class.java)
        NewsDAO.setApiService(api)

        // ✅ Compose UI
        setContent {
            NewsFeedAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}
