package com.storagedetective

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storagedetective.data.local.database.StorageDatabase
import com.storagedetective.data.local.preferences.UserPreferences
import com.storagedetective.data.repository.StorageRepositoryImpl
import com.storagedetective.services.scanner.FileScanner
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class IntegrationTest {

    private lateinit var database: StorageDatabase
    private lateinit var userPreferences: UserPreferences
    private lateinit var fileScanner: FileScanner
    private lateinit var repository: StorageRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        
        database = StorageDatabase.getDatabase(context)
        userPreferences = UserPreferences(context)
        fileScanner = FileScanner(context, database.fileInfoDao(), mock())
        
        repository = StorageRepositoryImpl(
            fileInfoDao = database.fileInfoDao(),
            storageSnapshotDao = database.storageSnapshotDao(),
            changeEventDao = database.changeEventDao(),
            fileScanner = fileScanner,
            mediaScanner = mock(),
            storageAnalyzer = mock(),
            trendAnalyzer = mock(),
            storagePredictor = mock(),
            userPreferences = userPreferences,
            storageManager = mock(),
            permissionHelper = mock(),
            ioDispatcher = kotlinx.coroutines.Dispatchers.IO,
            logger = mock()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testFullScanAndRetrieve() = runBlocking {
        // Perform scan
        val scanResult = repository.scanStorage(true)
        assertTrue(scanResult is StorageRepository.Result.Success)

        // Get stats
        val stats = repository.getStorageStats()
        assertNotNull(stats)
        assertTrue(stats.totalSpace > 0)

        // Get history
        val history = repository.getStorageHistory(7)
        assertNotNull(history)

        // Get changes
        val changes = repository.getChangesToday()
        assertNotNull(changes)

        // Get consumers
        val consumers = repository.getBiggestConsumers(10)
        assertNotNull(consumers)
    }

    @Test
    fun testFileOperations() = runBlocking {
        // Create test file
        val testFile = File(context.filesDir, "test.txt")
        testFile.writeText("test content")

        // Get file details
        val fileDetails = repository.getFileDetails(testFile.absolutePath)
        assertNotNull(fileDetails)
        assertEquals("test.txt", fileDetails?.name)

        // Search for file
        val searchResults = repository.searchFiles("test")
        assertTrue(searchResults.isNotEmpty())

        // Get files in directory
        val filesInDir = repository.getFilesInDirectory(context.filesDir.absolutePath)
        assertTrue(filesInDir.isNotEmpty())

        // Clean up
        testFile.delete()
    }

    @Test
    fun testStorageHealthAndForecast() = runBlocking {
        val health = repository.getStorageHealth()
        assertNotNull(health)

        val forecast = repository.getStorageForecast(30)
        assertNotNull(forecast)

        val fullDate = repository.getPredictedFullDate()
        // Can be null
    }

    @Test
    fun testPersonalityAndHabits() = runBlocking {
        val personality = repository.getStoragePersonality()
        assertNotNull(personality)

        val habits = repository.analyzeUserHabits(30)
        assertNotNull(habits)
    }

    @Test
    fun testTimeMachine() = runBlocking {
        val now = System.currentTimeMillis()
        val yesterday = now - 24 * 60 * 60 * 1000

        val snapshot = repository.getStorageAtTime(yesterday)
        // Can be null if no data

        val comparison = repository.compareTimePeriods(
            yesterday - 12 * 60 * 60 * 1000,
            yesterday + 12 * 60 * 60 * 1000,
            now - 12 * 60 * 60 * 1000,
            now + 12 * 60 * 60 * 1000
        )
        assertNotNull(comparison)
    }

    @Test
    fun testCategoryBreakdown() = runBlocking {
        val breakdown = repository.getCategoryBreakdown()
        assertNotNull(breakdown)

        val history = repository.getCategoryHistory(FileCategory.IMAGES, 30)
        assertNotNull(history)
    }

    @Test
    fun testScanProgressFlow() = runBlocking {
        val progressFlow = repository.getScanProgress()
        val progress = progressFlow.first()
        assertNotNull(progress)
    }

    @Test
    fun testGetStorageStatsFlow() = runBlocking {
        val statsFlow = repository.getStorageStatsFlow()
        val stats = statsFlow.first()
        assertNotNull(stats)
    }

    @Test
    fun testGetChangesFlow() = runBlocking {
        val changesFlow = repository.getChangesFlow()
        val changes = changesFlow.first()
        assertNotNull(changes)
    }

    @Test
    fun testGetTimelineEventsFlow() = runBlocking {
        val eventsFlow = repository.getTimelineEventsFlow()
        val events = eventsFlow.first()
        assertNotNull(events)
    }

    @Test
    fun testGetConsumersFlow() = runBlocking {
        val consumersFlow = repository.getConsumersFlow()
        val consumers = consumersFlow.first()
        assertNotNull(consumers)
    }
}
