package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsFeedScreen(
    onMoreFiltersClick: () -> Unit,
    onNewsClick: (String) -> Unit,
    newsItems: List<NewsItem>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
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
                items(newsItems, key = { it.id }) { item ->
                    if (item.isFeatured) {
                        FeaturedNewsCard(item = item, onClick = { onNewsClick(item.id) })
                    } else {
                        StandardNewsCard(item = item, onClick = { onNewsClick(item.id) })
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
fun StandardNewsCard(item: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(text = item.title, style = MaterialTheme.typography.titleSmall)
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
            Image(
                painter = painterResource(id = R.drawable.news_placeholder),
                contentDescription = "image",
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
