package com.storagedetective.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.storagedetective.core.constants.AppConstants
import com.storagedetective.data.local.database.dao.FileInfoDao
import com.storagedetective.data.local.database.entities.FileInfoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class StorageDaoTest {

    private lateinit var database: StorageDatabase
    private lateinit var fileInfoDao: FileInfoDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StorageDatabase::class.java
        ).build()
        fileInfoDao = database.fileInfoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetFile() = runBlocking {
        val file = FileInfoEntity(
            path = "/test/file.txt",
            fileName = "file.txt",
            parentPath = "/test",
            extension = "txt",
            size = 1024,
            lastModified = System.currentTimeMillis(),
            isDirectory = false,
            isHidden = false,
            canRead = true,
            canWrite = true,
            canExecute = false,
            category = AppConstants.FileCategory.DOCUMENTS
        )

        val id = fileInfoDao.insert(file)
        assertTrue(id > 0)

        val retrieved = fileInfoDao.getFileByPath("/test/file.txt")
        assertNotNull(retrieved)
        assertEquals("file.txt", retrieved?.fileName)
        assertEquals(1024, retrieved?.size)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAllAndCount() = runBlocking {
        val files = listOf(
            FileInfoEntity(
                path = "/test/file1.txt",
                fileName = "file1.txt",
                parentPath = "/test",
                extension = "txt",
                size = 1024,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            ),
            FileInfoEntity(
                path = "/test/file2.txt",
                fileName = "file2.txt",
                parentPath = "/test",
                extension = "txt",
                size = 2048,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            )
        )

        fileInfoDao.insertAll(files)
        
        val count = fileInfoDao.getTotalFileCount()
        assertEquals(2, count)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteFile() = runBlocking {
        val file = FileInfoEntity(
            path = "/test/file.txt",
            fileName = "file.txt",
            parentPath = "/test",
            extension = "txt",
            size = 1024,
            lastModified = System.currentTimeMillis(),
            isDirectory = false,
            isHidden = false,
            canRead = true,
            canWrite = true,
            canExecute = false,
            category = AppConstants.FileCategory.DOCUMENTS
        )

        fileInfoDao.insert(file)
        fileInfoDao.deleteByPath("/test/file.txt")
        
        val retrieved = fileInfoDao.getFileByPath("/test/file.txt")
        assertNull(retrieved)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLargestFiles() = runBlocking {
        val files = listOf(
            FileInfoEntity(
                path = "/test/small.txt",
                fileName = "small.txt",
                parentPath = "/test",
                extension = "txt",
                size = 1024,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            ),
            FileInfoEntity(
                path = "/test/large.txt",
                fileName = "large.txt",
                parentPath = "/test",
                extension = "txt",
                size = 10240,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            )
        )

        fileInfoDao.insertAll(files)
        
        val largest = fileInfoDao.getLargestFiles(1)
        assertEquals(1, largest.size)
        assertEquals("large.txt", largest[0].fileName)
        assertEquals(10240, largest[0].size)
    }

    @Test
    @Throws(Exception::class)
    fun testSearchFiles() = runBlocking {
        val files = listOf(
            FileInfoEntity(
                path = "/test/document.pdf",
                fileName = "document.pdf",
                parentPath = "/test",
                extension = "pdf",
                size = 1024,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            ),
            FileInfoEntity(
                path = "/test/image.jpg",
                fileName = "image.jpg",
                parentPath = "/test",
                extension = "jpg",
                size = 2048,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.IMAGES
            )
        )

        fileInfoDao.insertAll(files)
        
        val results = fileInfoDao.searchFiles("document")
        assertEquals(1, results.size)
        assertEquals("document.pdf", results[0].fileName)
    }

    @Test
    @Throws(Exception::class)
    fun testGetTotalSize() = runBlocking {
        val files = listOf(
            FileInfoEntity(
                path = "/test/file1.txt",
                fileName = "file1.txt",
                parentPath = "/test",
                extension = "txt",
                size = 1024,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            ),
            FileInfoEntity(
                path = "/test/file2.txt",
                fileName = "file2.txt",
                parentPath = "/test",
                extension = "txt",
                size = 2048,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            )
        )

        fileInfoDao.insertAll(files)
        
        val totalSize = fileInfoDao.getTotalSize()
        assertEquals(3072, totalSize)
    }

    @Test
    @Throws(Exception::class)
    fun testGetFilesByCategory() = runBlocking {
        val files = listOf(
            FileInfoEntity(
                path = "/test/image.jpg",
                fileName = "image.jpg",
                parentPath = "/test",
                extension = "jpg",
                size = 1024,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.IMAGES
            ),
            FileInfoEntity(
                path = "/test/doc.pdf",
                fileName = "doc.pdf",
                parentPath = "/test",
                extension = "pdf",
                size = 2048,
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                isHidden = false,
                canRead = true,
                canWrite = true,
                canExecute = false,
                category = AppConstants.FileCategory.DOCUMENTS
            )
        )

        fileInfoDao.insertAll(files)
        
        val images = fileInfoDao.getFilesByCategory(AppConstants.FileCategory.IMAGES)
        assertEquals(1, images.size)
        assertEquals("image.jpg", images[0].fileName)
    }
}
