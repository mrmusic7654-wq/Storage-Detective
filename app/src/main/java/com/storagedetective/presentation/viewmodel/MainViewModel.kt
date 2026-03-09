package com.storagedetective.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.storagedetective.core.utils.Logger
import com.storagedetective.core.utils.PermissionHelper
import com.storagedetective.data.local.preferences.UserPreferences
import com.storagedetective.presentation.navigation.DeepLinkHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val userPreferences: UserPreferences,
    private val permissionHelper: PermissionHelper,
    private val logger: Logger
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _useDynamicColor = MutableStateFlow(true)
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor

    private val _useAmoledMode = MutableStateFlow(false)
    val useAmoledMode: StateFlow<Boolean> = _useAmoledMode

    private val _startDestination = MutableStateFlow<Any>(Screen.Dashboard)
    val startDestination: StateFlow<Any> = _startDestination

    private val _permissionResults = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val permissionResults: StateFlow<Map<String, Boolean>> = _permissionResults

    init {
        loadPreferences()
        checkFirstLaunch()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                val theme = userPreferences.getThemeMode()
                _isDarkTheme.value = when (theme) {
                    "dark" -> true
                    "light" -> false
                    else -> isSystemDarkTheme()
                }
                
                _useDynamicColor.value = userPreferences.isDynamicColorsEnabled()
                _useAmoledMode.value = userPreferences.isAmoledModeEnabled()
                
                _isLoading.value = false
            } catch (e: Exception) {
                logger.e("Error loading preferences", e)
                _isLoading.value = false
            }
        }
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val isFirstLaunch = userPreferences.isFirstLaunch()
            if (isFirstLaunch) {
                _startDestination.value = Screen.Onboarding
                userPreferences.setFirstLaunch(false)
            } else {
                checkPermissions()
            }
        }
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            if (!permissionHelper.hasRequiredPermissions()) {
                // Show permission rationale
            }
        }
    }

    fun handleDeepLink(intent: Intent) {
        intent.data?.let { uri ->
            val action = DeepLinkHandler.handleDeepLink(uri)
            if (action != null) {
                // Handle navigation
                logger.d("Deep link handled: $uri")
            }
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val resultMap = permissions.zip(grantResults.map { it == PackageManager.PERMISSION_GRANTED }).toMap()
        _permissionResults.value = resultMap
        
        logger.d("Permission results: $resultMap")
    }

    fun onResume() {
        viewModelScope.launch {
            // Refresh theme if needed
            val theme = userPreferences.getThemeMode()
            _isDarkTheme.value = when (theme) {
                "dark" -> true
                "light" -> false
                else -> isSystemDarkTheme()
            }
        }
    }

    fun onPause() {
        // Save state if needed
    }

    private fun isSystemDarkTheme(): Boolean {
        val uiMode = getApplication<Application>().resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    fun requestPermission(permission: String) {
        // Delegate to activity
    }

    fun checkPermission(permission: String): Boolean {
        return permissionHelper.isPermissionGranted(permission)
    }
}

sealed class Screen {
    object Dashboard : Screen()
    object Onboarding : Screen()
    object Permissions : Screen()
}
