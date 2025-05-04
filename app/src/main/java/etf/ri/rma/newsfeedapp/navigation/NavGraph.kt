package etf.ri.rma.newsfeedapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.navigation.*
import androidx.navigation.compose.*
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.screen.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    var selectedCategory by rememberSaveable { mutableStateOf("Sve") }
    var dateRange by rememberSaveable { mutableStateOf<Pair<String?, String?>>(null to null) }
    var unwantedWords by rememberSaveable { mutableStateOf(listOf<String>()) }

    val allNews = NewsData.getAllNews()

    val filteredNews = allNews.filter { item ->
        val categoryMatch = selectedCategory == "Sve" || item.category == selectedCategory
        val dateMatch = (dateRange.first == null || item.publishedDate >= (dateRange.first ?: "")) &&
                (dateRange.second == null || item.publishedDate <= (dateRange.second ?: ""))
        val unwantedMatch = unwantedWords.none { word ->
            item.title.contains(word, ignoreCase = true) || item.snippet.contains(word, ignoreCase = true)
        }
        categoryMatch && dateMatch && unwantedMatch
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { backStackEntry ->
            saveableStateHolder.SaveableStateProvider(backStackEntry.id) {
                NewsFeedScreen(
                    onMoreFiltersClick = { navController.navigate("filters") },
                    onNewsClick = { id -> navController.navigate("details/$id") },
                    newsItems = filteredNews,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
        }
        composable("filters") {
            FilterScreen(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onDateRangeSelected = { dateRange = it },
                onUnwantedWordsChanged = { unwantedWords = it },
                onApplyFilters = {},
                navController = navController
            )
        }
        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            if (id != null) {
                NewsDetailsScreen(newsId = id, navController = navController)
            }
        }
    }
}
