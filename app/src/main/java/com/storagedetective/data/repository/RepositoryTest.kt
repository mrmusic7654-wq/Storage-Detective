package com.storagedetective.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storagedetective.core.utils.Logger
import com.storagedetective.data.local.database.StorageDatabase
import com.storagedetective.data.local.database.dao.FileInfoDao
import com.storagedetective.data.local.preferences.UserPreferences
import com.storagedetective.domain.repository.StorageRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var database: StorageDatabase
    private lateinit var fileInfoDao: FileInfoDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var logger: Logger
    private lateinit var repository: StorageRepository

    @Before
    fun setup() {
        database = mock(StorageDatabase::class.java)
        fileInfoDao = mock(FileInfoDao::class.java)
        userPreferences = mock(UserPreferences::class.java)
        logger = mock(Logger::class.java)
        
        `when`(database.fileInfoDao()).thenReturn(fileInfoDao)
        
        repository = StorageRepositoryImpl(
            fileInfoDao = fileInfoDao,
            storageSnapshotDao = mock(),
            changeEventDao = mock(),
            fileScanner = mock(),
            mediaScanner = mock(),
            storageAnalyzer = mock(),
            trendAnalyzer = mock(),
            storagePredictor = mock(),
            userPreferences = userPreferences,
            storageManager = mock(),
            permissionHelper = mock(),
            ioDispatcher = kotlinx.coroutines.Dispatchers.IO,
            logger = logger
        )
    }

    @After
    fun tearDown() {
        // Clean up
    }

    @Test
    fun testGetStorageStats() = runBlocking {
        val stats = repository.getStorageStats()
        assertNotNull(stats)
    }

    @Test
    fun testGetStorageHistory() = runBlocking {
        val history = repository.getStorageHistory(7)
        assertNotNull(history)
    }

    @Test
    fun testGetChangesToday() = runBlocking {
        val changes = repository.getChangesToday()
        assertNotNull(changes)
    }

    @Test
    fun testGetBiggestConsumers() = runBlocking {
        val consumers = repository.getBiggestConsumers(10)
        assertNotNull(consumers)
    }

    @Test
    fun testScanStorage() = runBlocking {
        val result = repository.scanStorage(false)
        assertNotNull(result)
    }

    @Test
    fun testGetStorageHealth() = runBlocking {
        val health = repository.getStorageHealth()
        assertNotNull(health)
    }

    @Test
    fun testGetStorageForecast() = runBlocking {
        val forecast = repository.getStorageForecast(30)
        assertNotNull(forecast)
    }

    @Test
    fun testGetStoragePersonality() = runBlocking {
        val personality = repository.getStoragePersonality()
        assertNotNull(personality)
    }
}
