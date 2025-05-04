package etf.ri.rma.newsfeedapp.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FilterScreen(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDateRangeSelected: (Pair<String?, String?>) -> Unit,
    onUnwantedWordsChanged: (List<String>) -> Unit,
    onApplyFilters: () -> Unit,
    navController: NavController
) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija")
    var dateRange by remember { mutableStateOf<Pair<String?, String?>>(null to null) }
    var unwantedInput by remember { mutableStateOf("") }
    var unwantedWords by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Kategorije", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        FilterChips(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            onMoreFiltersClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()

        val datePickerStart = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendarStart.set(year, month, day)
                dateRange = dateRange.copy(first = formatter.format(calendarStart.time))
            },
            calendarStart.get(Calendar.YEAR),
            calendarStart.get(Calendar.MONTH),
            calendarStart.get(Calendar.DAY_OF_MONTH)
        )

        val datePickerEnd = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendarEnd.set(year, month, day)
                dateRange = dateRange.copy(second = formatter.format(calendarEnd.time))
            },
            calendarEnd.get(Calendar.YEAR),
            calendarEnd.get(Calendar.MONTH),
            calendarEnd.get(Calendar.DAY_OF_MONTH)
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                text = if (!dateRange.first.isNullOrEmpty() && !dateRange.second.isNullOrEmpty())
                    "${dateRange.first};${dateRange.second}"
                else "Nije odabran opseg datuma",
                modifier = Modifier.testTag("filter_daterange_display")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    datePickerStart.show()
                    datePickerEnd.show()
                },
                modifier = Modifier.testTag("filter_daterange_button")
            ) {
                Text("Odaberi")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Neželjene riječi", style = MaterialTheme.typography.titleMedium)

        Row {
            TextField(
                value = unwantedInput,
                onValueChange = { unwantedInput = it },
                label = { Text("Unos") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("filter_unwanted_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val word = unwantedInput.trim()
                    if (word.isNotEmpty() && !unwantedWords.any { it.equals(word, ignoreCase = true) }) {
                        unwantedWords = unwantedWords + word
                        unwantedInput = ""
                    }
                },
                modifier = Modifier.testTag("filter_unwanted_add_button")
            ) {
                Text("Dodaj")
            }
        }

        LazyColumn(modifier = Modifier.testTag("filter_unwanted_list")) {
            items(unwantedWords) { word ->
                Text(text = word)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                onDateRangeSelected(dateRange)
                onUnwantedWordsChanged(unwantedWords)
                onApplyFilters()
                navController.popBackStack()
            },
            modifier = Modifier.testTag("filter_apply_button")
        ) {
            Text("Primijeni filtere")
        }
    }
}
