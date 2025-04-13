package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import etf.ri.rma.newsfeedapp.ui.theme.LightPrimary
import etf.ri.rma.newsfeedapp.ui.theme.DarkPrimary
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsFeedScreen() {
    val allNews = remember { NewsData.getAllNews() }
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Moda")
    var selectedCategory by remember { mutableStateOf("Sve") }

    val filteredNews = allNews.filter {
        selectedCategory == "Sve" || it.category == selectedCategory
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterChips(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji \"$selectedCategory\"")
        } else {
            NewsList(newsItems = filteredNews)
        }
    }
}

@Composable
fun FilterChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val selectedColor = if (isDark) DarkPrimary else LightPrimary

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val testTag = when (category) {
                "Sve" -> "filter_chip_all"
                "Politika" -> "filter_chip_pol"
                "Sport" -> "filter_chip_spo"
                "Nauka/tehnologija" -> "filter_chip_sci"
                "Moda" -> "filter_chip_none"
                else -> "filter_chip_unknown"
            }

            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        color = if (selectedCategory == category)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag(testTag),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = selectedColor,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}



@Composable
fun NewsList(newsItems: List<NewsItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list")
    ) {
        items(newsItems, key = { it.id }) { item ->
            if (item.isFeatured) {
                FeaturedNewsCard(item)
            } else {
                StandardNewsCard(item)
            }
        }
    }
}


@Composable
fun StandardNewsCard(newsItem: NewsItem) {
    val sourceColor = if (isSystemInDarkTheme()) DarkPrimary else LightPrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.news_placeholder),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = newsItem.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = newsItem.snippet,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${newsItem.source} • ${newsItem.publishedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = sourceColor
                )
            }
        }
    }
}

@Composable
fun FeaturedNewsCard(newsItem: NewsItem) {
    val sourceColor = if (isSystemInDarkTheme()) DarkPrimary else LightPrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.news_placeholder),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = newsItem.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = newsItem.snippet,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${newsItem.source} • ${newsItem.publishedDate}",
                style = MaterialTheme.typography.labelSmall,
                color = sourceColor
            )
        }
    }
}


@Composable
fun MessageCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD32F2F)
        )
    }
}
