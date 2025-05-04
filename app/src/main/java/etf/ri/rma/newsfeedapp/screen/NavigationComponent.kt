
package etf.ri.rma.newsfeedapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationComponent() {
    val navController = rememberNavController()

    var selectedCategory by rememberSaveable { mutableStateOf("Sve") }
    var dateRange by rememberSaveable { mutableStateOf<Pair<String?, String?>>(null to null) }
    var unwantedWords by rememberSaveable { mutableStateOf(listOf<String>()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val filteredNews = remember(selectedCategory, dateRange, unwantedWords) {
                NewsData.getAllNews().filter { item ->
                    val matchesCategory = selectedCategory == "Sve" || item.category == selectedCategory

                    val matchesDate = if (dateRange.first != null && dateRange.second != null) {
                        try {
                            val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            val itemDate = LocalDate.parse(item.publishedDate, format)
                            val from = LocalDate.parse(dateRange.first, format)
                            val to = LocalDate.parse(dateRange.second, format)
                            !itemDate.isBefore(from) && !itemDate.isAfter(to)
                        } catch (e: Exception) {
                            true
                        }
                    } else true

                    val matchesUnwanted = unwantedWords.none {
                        item.title.contains(it, ignoreCase = true) ||
                                item.snippet.contains(it, ignoreCase = true)
                    }

                    matchesCategory && matchesDate && matchesUnwanted
                }
            }

            NewsFeedScreen(
                onMoreFiltersClick = { navController.navigate("filters") },
                onNewsClick = { id -> navController.navigate("details/$id") },
                newsItems = filteredNews,
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
            val newsId = backStackEntry.arguments?.getString("id") ?: return@composable
            NewsDetailsScreen(newsId = newsId, navController = navController)
        }
    }
}
