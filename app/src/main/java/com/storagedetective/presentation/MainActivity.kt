package com.storagedetective.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.storagedetective.core.utils.Logger
import com.storagedetective.presentation.navigation.NavGraph
import com.storagedetective.presentation.theme.StorageDetectiveTheme
import com.storagedetective.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        logger.d("MainActivity created")

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {
            StorageDetectiveTheme(
                darkTheme = viewModel.isDarkTheme.value,
                dynamicColor = viewModel.useDynamicColor.value,
                amoledMode = viewModel.useAmoledMode.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavGraph(
                        navController = navController,
                        startDestination = viewModel.startDestination.value
                    )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.handleDeepLink(intent)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            viewModel.handleDeepLink(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        logger.d("MainActivity resumed")
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        logger.d("MainActivity paused")
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d("MainActivity destroyed")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.handlePermissionResult(requestCode, permissions, grantResults)
    }
}
