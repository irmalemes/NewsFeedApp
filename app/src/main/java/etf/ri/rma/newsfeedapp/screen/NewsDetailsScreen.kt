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
import etf.ri.rma.newsfeedapp.data.NewsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailsScreen(newsId: String, navController: NavController) {
    val allNews = remember { NewsData.getAllNews() }
    val news = allNews.find { it.id == newsId } ?: return

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val currentDate = LocalDate.parse(news.publishedDate, formatter)

    val relatedNews = allNews
        .filter { it.category == news.category && it.id != news.id }
        .sortedWith(
            compareBy(
                { kotlin.math.abs(ChronoUnit.DAYS.between(LocalDate.parse(it.publishedDate, formatter), currentDate)) },
                { it.title }
            )
        )
        .take(2)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(news.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("details_title"))
        Text(news.snippet, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("details_snippet"))
        Text(news.category, style = MaterialTheme.typography.labelMedium, modifier = Modifier.testTag("details_category"))
        Text(news.source, style = MaterialTheme.typography.labelSmall, modifier = Modifier.testTag("details_source"))
        Text(news.publishedDate, style = MaterialTheme.typography.labelSmall, modifier = Modifier.testTag("details_date"))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Povezane vijesti iz iste kategorije:")

        relatedNews.forEachIndexed { index, item ->
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        navController.navigate("details/${item.id}") {
                            popUpTo("details/${newsId}") { inclusive = true }
                        }
                    }
                    .testTag("related_news_title_${index + 1}")
            )
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

