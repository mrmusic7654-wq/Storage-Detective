package com.storagedetective.presentation.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setup() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavGraph(navController = navController, startDestination = Screen.Dashboard)
        }
    }

    @Test
    fun testNavigateToDashboard() {
        navController.navigate(Screen.Dashboard)
        assertEquals(Screen.Dashboard::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNavigateToApps() {
        navController.navigate(Screen.Apps)
        assertEquals(Screen.Apps::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNavigateToTimeline() {
        navController.navigate(Screen.Timeline)
        assertEquals(Screen.Timeline::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNavigateToCleaner() {
        navController.navigate(Screen.Cleaner)
        assertEquals(Screen.Cleaner::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNavigateToSettings() {
        navController.navigate(Screen.Settings)
        assertEquals(Screen.Settings::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNavigateToStorageDetails() {
        val path = "/test/path"
        navController.navigate(Screen.StorageDetails(path))
        val route = navController.currentBackStackEntry?.destination?.route
        assertTrue(route?.contains(path) == true)
    }

    @Test
    fun testNavigateToAppDetails() {
        val packageName = "com.test.app"
        navController.navigate(Screen.AppDetails(packageName))
        val route = navController.currentBackStackEntry?.destination?.route
        assertTrue(route?.contains(packageName) == true)
    }

    @Test
    fun testBackNavigation() {
        navController.navigate(Screen.Apps)
        navController.navigate(Screen.Timeline)
        navController.popBackStack()
        assertEquals(Screen.Apps::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testDeepLinkHandling() {
        val uri = android.net.Uri.parse("storagedetective://dashboard")
        val action = DeepLinkHandler.handleDeepLink(uri)
        assertTrue(action is NavigationAction.NavigateTo)
        assertTrue((action as NavigationAction.NavigateTo).screen is Screen.Dashboard)
    }
}
