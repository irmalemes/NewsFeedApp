package etf.ri.rma.newsfeedapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    var selectedCategory by rememberSaveable { mutableStateOf("Sve") }
    var dateRange by rememberSaveable { mutableStateOf<Pair<String?, String?>>(null to null) }
    var unwantedWords by rememberSaveable { mutableStateOf(listOf<String>()) }

    val filteredNews = remember(selectedCategory, dateRange, unwantedWords) {
        NewsDAO.getAllStories().filter { item ->
            val categoryMatch = selectedCategory == "Sve" || item.category == selectedCategory
            val dateMatch = (dateRange.first == null || item.publishedDate >= (dateRange.first ?: "")) &&
                    (dateRange.second == null || item.publishedDate <= (dateRange.second ?: ""))
            val unwantedMatch = unwantedWords.none { word ->
                item.title.contains(word, ignoreCase = true) || item.snippet.contains(word, ignoreCase = true)
            }
            categoryMatch && dateMatch && unwantedMatch
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(
                onMoreFiltersClick = { navController.navigate("filters") },
                onNewsClick = { uuid -> navController.navigate("details/$uuid") },
                //newsItems = filteredNews,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        composable("filters") {
            FilterScreen(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onDateRangeSelected = { dateRange = it },
                onUnwantedWordsChanged = { unwantedWords = it },
                onApplyFilters = { navController.popBackStack() },
                navController = navController
            )
        }

        composable("details/{id}") { backStackEntry ->
            val uuid = backStackEntry.arguments?.getString("id") ?: return@composable
            NewsDetailsScreen(newsId = uuid, navController = navController)
        }
    }
}
