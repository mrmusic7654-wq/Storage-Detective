package com.storagedetective.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storagedetective.core.utils.Logger
import com.storagedetective.domain.usecases.storage.GetStorageStatsUseCase
import com.storagedetective.presentation.screens.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var getStorageStatsUseCase: GetStorageStatsUseCase
    private lateinit var logger: Logger
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getStorageStatsUseCase = mock(GetStorageStatsUseCase::class.java)
        logger = mock(Logger::class.java)
        
        viewModel = DashboardViewModel(
            savedStateHandle = SavedStateHandle(),
            getStorageStatsUseCase = getStorageStatsUseCase,
            getStorageHistoryUseCase = mock(),
            getChangesTodayUseCase = mock(),
            getStorageForecastUseCase = mock(),
            getStoragePersonalityUseCase = mock(),
            getStorageHealthUseCase = mock(),
            getStorageTimeMachineUseCase = mock(),
            getFamilyStorageUseCase = mock(),
            getGameProgressUseCase = mock(),
            scanStorageUseCase = mock(),
            logger = logger
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertNotNull(state)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun testRefreshData() {
        viewModel.refreshData()
        // Verify loading state changes
    }

    @Test
    fun testStartScan() {
        testScope.launchTest {
            viewModel.startScan()
            // Verify scan progress
        }
    }

    @Test
    fun testPerformQuickAction() {
        viewModel.performQuickAction(QuickAction("scan", "Scan", Icons.Default.Search))
        // Verify action performed
    }
}
