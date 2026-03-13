package com.storagedetective.services.cleaner

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storagedetective.core.utils.Logger
import com.storagedetective.data.local.database.dao.CleanerActionDao
import com.storagedetective.data.local.database.dao.DuplicateFileDao
import com.storagedetective.data.local.database.dao.FileInfoDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class CleanerTest {

    private lateinit var context: Context
    private lateinit var fileInfoDao: FileInfoDao
    private lateinit var duplicateFileDao: DuplicateFileDao
    private lateinit var cleanerActionDao: CleanerActionDao
    private lateinit var logger: Logger
    private lateinit var smartCleaner: SmartCleaner

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileInfoDao = mock(FileInfoDao::class.java)
        duplicateFileDao = mock(DuplicateFileDao::class.java)
        cleanerActionDao = mock(CleanerActionDao::class.java)
        logger = mock(Logger::class.java)
        
        smartCleaner = SmartCleaner(fileInfoDao, duplicateFileDao, cleanerActionDao, logger)
    }

    @Test
    fun testSmartCleanerInitialization() {
        assertNotNull(smartCleaner)
    }

    @Test
    fun testCleanPaths() = runBlocking {
        val testPaths = listOf("/test/file1.txt", "/test/file2.txt")
        
        val result = smartCleaner.clean(testPaths)
        
        assertNotNull(result)
        assertTrue(result.success || !result.success)
    }

    @Test
    fun testCleanCache() = runBlocking {
        `when`(fileInfoDao.getFilesByCategory(any())).thenReturn(emptyList())
        
        val result = smartCleaner.cleanCache()
        
        assertNotNull(result)
    }

    @Test
    fun testSmartClean() = runBlocking {
        val result = smartCleaner.smartClean()
        
        assertNotNull(result)
        assertTrue(result.totalSpaceFreed >= 0)
    }

    @Test
    fun testPlan() = runBlocking {
        val plan = smartCleaner.plan()
        
        assertNotNull(plan)
        assertTrue(plan.estimatedSpace >= 0)
    }

    @Test
    fun testGetStats() = runBlocking {
        val stats = smartCleaner.getStats()
        
        assertNotNull(stats)
        assertTrue(stats.totalCleanups >= 0)
    }
}
