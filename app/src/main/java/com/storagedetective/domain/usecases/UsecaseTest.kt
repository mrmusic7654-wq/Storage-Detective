package com.storagedetective.domain.usecases

import com.storagedetective.domain.models.StorageStats
import com.storagedetective.domain.repository.StorageRepository
import com.storagedetective.domain.usecases.storage.GetStorageStatsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

class UseCaseTest {

    private lateinit var repository: StorageRepository
    private lateinit var getStorageStatsUseCase: GetStorageStatsUseCase

    @Before
    fun setup() {
        repository = mock()
        getStorageStatsUseCase = GetStorageStatsUseCase(repository)
    }

    @Test
    fun `get storage stats success`() = runBlocking {
        val expectedStats = StorageStats.createSample()
        `when`(repository.getStorageStats()).thenReturn(expectedStats)

        val result = getStorageStatsUseCase()

        assertTrue(result is GetStorageStatsUseCase.Result.Success)
        assertEquals(expectedStats, (result as GetStorageStatsUseCase.Result.Success).data)
    }

    @Test
    fun `get storage stats error`() = runBlocking {
        `when`(repository.getStorageStats()).thenThrow(RuntimeException("Test error"))

        val result = getStorageStatsUseCase()

        assertTrue(result is GetStorageStatsUseCase.Result.Error)
        assertTrue((result as GetStorageStatsUseCase.Result.Error).message?.contains("error") == true)
    }

    @Test
    fun `observe storage stats`() = runBlocking {
        val expectedStats = StorageStats.createSample()
        `when`(repository.getStorageStatsFlow()).thenReturn(flowOf(expectedStats))

        val result = getStorageStatsUseCase.observe().first()

        assertTrue(result is GetStorageStatsUseCase.Result.Success)
        assertEquals(expectedStats, (result as GetStorageStatsUseCase.Result.Success).data)
    }

    @Test
    fun `get with history`() = runBlocking {
        val currentStats = StorageStats.createSample()
        val history = listOf(currentStats)
        
        `when`(repository.getStorageStats()).thenReturn(currentStats)
        `when`(repository.getStorageHistory(7)).thenReturn(history)

        val result = getStorageStatsUseCase.getWithHistory(7)

        assertTrue(result is GetStorageStatsUseCase.Result.Success)
        val (current, hist) = (result as GetStorageStatsUseCase.Result.Success).data
        assertEquals(currentStats, current)
        assertEquals(history, hist)
    }

    @Test
    fun `get comparison`() = runBlocking {
        val currentStats = StorageStats.createSample()
        val yesterdayStats = StorageStats.createSample().copy(usedSpace = 40L * 1024 * 1024 * 1024)
        
        `when`(repository.getStorageStats()).thenReturn(currentStats)
        `when`(repository.getStorageHistory(1)).thenReturn(listOf(yesterdayStats))

        val result = getStorageStatsUseCase.getComparison()

        assertTrue(result is GetStorageStatsUseCase.Result.Success)
        val comparison = (result as GetStorageStatsUseCase.Result.Success).data
        assertEquals(currentStats, comparison.current)
        assertEquals(yesterdayStats, comparison.previous)
        assertTrue(comparison.usedDifference > 0)
    }

    @Test
    fun `validate storage access`() = runBlocking {
        `when`(repository.getStorageStats()).thenReturn(StorageStats.createSample())

        val result = getStorageStatsUseCase.validateStorageAccess()

        assertTrue(result is GetStorageStatsUseCase.Result.Success)
        assertTrue((result as GetStorageStatsUseCase.Result.Success).data)
    }
}
