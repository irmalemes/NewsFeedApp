package etf.ri.rma.newsfeedapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.ImageDAO
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailsScreen(newsId: String, navController: NavController) {
    val scope = rememberCoroutineScope()
    var news by remember { mutableStateOf<NewsItem?>(null) }
    var relatedNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var imageTags by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(newsId) {
        val allNews = NewsDAO.getAllStories()
        news = allNews.find { it.uuid == newsId }

        news?.let { item ->
            relatedNews = NewsDAO.getSimilarStories(item.uuid)

            val dao = NewsDAO.getSavedNewsDAO()
            val localId = dao?.getIdByUUID(item.uuid)

            if (localId != null) {
                val localTags = dao.getTags(localId)
                if (localTags.isNotEmpty()) {
                    imageTags = localTags
                } else {
                    // Nema tagova u bazi, dohvatiti sa web-a
                    if (item.imageUrl != null) {
                        val downloadedTags = ImageDAO.getTags(item.imageUrl)
                        imageTags = downloadedTags
                        dao.addTags(downloadedTags, localId)
                    }
                }
            }
        }
    }

    news?.let { item ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("details_title"))
            Text(item.snippet, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("details_snippet"))
            Text(item.category, style = MaterialTheme.typography.labelMedium, modifier = Modifier.testTag("details_category"))
            Text(item.source, style = MaterialTheme.typography.labelSmall, modifier = Modifier.testTag("details_source"))
            Text(item.publishedDate, style = MaterialTheme.typography.labelSmall, modifier = Modifier.testTag("details_date"))

            Spacer(modifier = Modifier.height(16.dp))

            if (imageTags.isNotEmpty()) {
                Text("Tagovi slike:", style = MaterialTheme.typography.titleSmall)
                imageTags.forEach {
                    Text("- $it", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (relatedNews.isNotEmpty()) {
                Text("Povezane vijesti iz iste kategorije:")
                relatedNews.forEachIndexed { index, related ->
                    Text(
                        text = related.title,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable {
                                navController.navigate("details/${related.uuid}") {
                                    popUpTo("details/$newsId") { inclusive = true }
                                }
                            }
                            .testTag("related_news_title_${index + 1}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}
