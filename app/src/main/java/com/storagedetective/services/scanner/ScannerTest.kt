package com.storagedetective.services.scanner

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storagedetective.core.utils.Logger
import com.storagedetective.data.local.database.dao.FileInfoDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.io.File

@RunWith(AndroidJUnit4::class)
class ScannerTest {

    private lateinit var context: Context
    private lateinit var fileInfoDao: FileInfoDao
    private lateinit var logger: Logger
    private lateinit var fileScanner: FileScanner

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileInfoDao = mock(FileInfoDao::class.java)
        logger = mock(Logger::class.java)
        
        fileScanner = FileScanner(context, fileInfoDao, logger)
    }

    @Test
    fun testFileScannerInitialization() {
        assertNotNull(fileScanner)
    }

    @Test
    fun testScanIncremental() = runBlocking {
        // Mock behavior
        `when`(fileInfoDao.getAllFiles()).thenReturn(emptyList())
        
        // Perform scan
        fileScanner.scanIncremental()
        
        // Verify interactions
        verify(fileInfoDao, atLeastOnce()).getAllFiles()
    }

    @Test
    fun testScanCancel() {
        fileScanner.cancelScan()
        assertFalse(fileScanner.isScanning())
    }

    @Test
    fun testGetProgress() {
        val progress = fileScanner.getProgress()
        assertNotNull(progress)
    }

    @Test
    fun testScanPaths() = runBlocking {
        val testPath = context.cacheDir?.absolutePath ?: return@runBlocking
        
        fileScanner.scanPaths(listOf(testPath))
        
        verify(fileInfoDao, atLeastOnce()).insert(any())
    }

    @Test
    fun testGetScanStats() = runBlocking {
        val stats = fileScanner.getScanStats()
        assertNotNull(stats)
        assertTrue(stats.totalFiles >= 0)
    }
}
