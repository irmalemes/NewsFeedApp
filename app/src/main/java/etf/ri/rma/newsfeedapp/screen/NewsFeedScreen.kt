package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch

@Composable
fun NewsFeedScreen(
    onMoreFiltersClick: () -> Unit,
    onNewsClick: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var newsItems by remember { mutableStateOf<List<NewsItem>>(emptyList()) }

    // Učitavanje vijesti pri promjeni kategorije
    LaunchedEffect(selectedCategory) {
        newsItems = NewsDAO.getTopStoriesByCategory(selectedCategory)
    }

    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Moda")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterChips(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            onMoreFiltersClick = onMoreFiltersClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize().testTag("news_list")) {
            if (newsItems.isEmpty()) {
                item {
                    MessageCard("Nema pronađenih vijesti u kategoriji \"$selectedCategory\"")
                }
            } else {
                items(newsItems, key = { it.uuid }) { item ->
                    if (item.isFeatured) {
                        FeaturedNewsCard(item = item, onClick = { onNewsClick(item.uuid) })
                    } else {
                        StandardNewsCard(newsItem = item, onClick = { onNewsClick(item.uuid) })
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onMoreFiltersClick: () -> Unit
) {
    Column {
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
                    label = { Text(text = category) },
                    modifier = Modifier.testTag(testTag)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilterChip(
            selected = false,
            onClick = onMoreFiltersClick,
            label = { Text("Više filtera ...") },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .testTag("filter_chip_more")
        )
    }
}

@Composable
fun MessageCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("empty_list_message"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun StandardNewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            newsItem.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(newsItem.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(newsItem.snippet, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun FeaturedNewsCard(item: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            item.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.news_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.snippet, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.source} • ${item.publishedDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
