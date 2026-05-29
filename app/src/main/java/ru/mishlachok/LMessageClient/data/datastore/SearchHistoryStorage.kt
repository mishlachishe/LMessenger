package ru.mishlachok.LMessageClient.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryStorage @Inject constructor(
	@ApplicationContext private val context: Context
) {
	private val Context.dataStore by preferencesDataStore(name = "search_history")
	private val historyKey = stringPreferencesKey("history")

	val historyFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
		val json = prefs[historyKey] ?: "[]"
		try { Json.decodeFromString<List<String>>(json) } catch (_: Exception) { emptyList() }
	}

	suspend fun addQuery(query: String) {
		context.dataStore.edit { prefs ->
			val current = getCurrentList(prefs)
			val newList = mutableListOf(query).apply { addAll(current.filter { it != query }) }
			if (newList.size > 10) newList.removeAt(newList.lastIndex)
			prefs[historyKey] = Json.encodeToString(newList)
		}
	}

	suspend fun clearHistory() {
		context.dataStore.edit { it.remove(historyKey) }
	}

	private fun getCurrentList(prefs: androidx.datastore.preferences.core.Preferences): List<String> {
		val json = prefs[historyKey] ?: "[]"
		return try { Json.decodeFromString(json) } catch (_: Exception) { emptyList() }
	}
}