package ru.mishlachok.LMessageClient.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.lmessage.client.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import ru.mishlachok.LMessageClient.presentation.theme.AppThemeState
import ru.mishlachok.LMessageClient.presentation.theme.LMessageTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		AppThemeState.init(applicationContext)
		setContent {
			val savedDarkTheme by AppThemeState.darkTheme.collectAsStateWithLifecycle()
			val systemDarkTheme = isSystemInDarkTheme()
			LMessageTheme(darkTheme = savedDarkTheme ?: systemDarkTheme) {
				val navController = rememberNavController()
				AppNavGraph(navController = navController)
			}
		}
	}
}
