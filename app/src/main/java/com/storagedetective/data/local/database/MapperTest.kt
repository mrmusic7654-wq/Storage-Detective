package com.storagedetective.data.local.database

import com.storagedetective.core.constants.AppConstants
import com.storagedetective.data.local.database.entities.FileInfoEntity
import com.storagedetective.data.local.database.entities.StorageSnapshotEntity
import com.storagedetective.domain.models.StorageStats
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

class MapperTest {

    private lateinit var storageAnalyzer: StorageAnalyzer

    @Before
    fun setup() {
        storageAnalyzer = StorageAnalyzer(mock(), mock(), mock())
    }

    @Test
    fun `map file info entity to domain`() {
        val entity = FileInfoEntity(
            id = 1,
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

        val domain = storageAnalyzer.convertToFileItem(entity)

        assertEquals(entity.path, domain.path)
        assertEquals(entity.fileName, domain.name)
        assertEquals(entity.size, domain.size)
        assertEquals(entity.isDirectory, domain.isDirectory)
        assertEquals(StorageStats.FileCategory.DOCUMENTS, domain.category)
    }

    @Test
    fun `map storage snapshot entity to domain`() {
        val now = System.currentTimeMillis()
        val entity = StorageSnapshotEntity(
            id = 1,
            timestamp = now,
            storageId = "internal",
            storageLabel = "Internal Storage",
            storageType = AppConstants.StorageType.INTERNAL,
            totalSpace = 64L * 1024 * 1024 * 1024,
            usedSpace = 42L * 1024 * 1024 * 1024,
            freeSpace = 22L * 1024 * 1024 * 1024,
            fileCount = 1000,
            folderCount = 100,
            appCount = 50,
            imagesCount = 200,
            videosCount = 50,
            audioCount = 100,
            documentsCount = 150,
            imagesSize = 10L * 1024 * 1024 * 1024,
            videosSize = 15L * 1024 * 1024 * 1024,
            audioSize = 5L * 1024 * 1024 * 1024,
            documentsSize = 8L * 1024 * 1024 * 1024,
            appsSize = 4L * 1024 * 1024 * 1024,
            cacheSize = 2L * 1024 * 1024 * 1024,
            tempSize = 1L * 1024 * 1024 * 1024,
            systemSize = 3L * 1024 * 1024 * 1024,
            otherSize = 2L * 1024 * 1024 * 1024,
            healthScore = 95,
            isPrimary = true,
            isHealthy = true
        )

        val domain = storageAnalyzer.convertToDomain(entity)

        assertEquals(entity.totalSpace, domain.totalSpace)
        assertEquals(entity.usedSpace, domain.usedSpace)
        assertEquals(entity.freeSpace, domain.freeSpace)
        assertEquals(Date(now), domain.lastUpdated)
        assertEquals(StorageStats.StorageType.INTERNAL, domain.storageType)
        assertEquals(entity.healthScore, domain.healthScore)
    }

    @Test
    fun `map empty snapshot`() {
        val entity = StorageSnapshotEntity.createEmpty()

        val domain = storageAnalyzer.convertToDomain(entity)

        assertEquals(0, domain.totalSpace)
        assertEquals(0, domain.usedSpace)
        assertEquals(0, domain.freeSpace)
        assertEquals(100, domain.healthScore)
    }

    @Test
    fun `map category breakdown`() {
        val entity = StorageSnapshotEntity(
            id = 1,
            timestamp = System.currentTimeMillis(),
            storageId = "internal",
            storageLabel = "Internal Storage",
            storageType = AppConstants.StorageType.INTERNAL,
            totalSpace = 64L * 1024 * 1024 * 1024,
            usedSpace = 42L * 1024 * 1024 * 1024,
            freeSpace = 22L * 1024 * 1024 * 1024,
            fileCount = 1000,
            folderCount = 100,
            appCount = 50,
            imagesCount = 200,
            videosCount = 50,
            audioCount = 100,
            documentsCount = 150,
            imagesSize = 10L * 1024 * 1024 * 1024,
            videosSize = 15L * 1024 * 1024 * 1024,
            audioSize = 5L * 1024 * 1024 * 1024,
            documentsSize = 8L * 1024 * 1024 * 1024,
            appsSize = 4L * 1024 * 1024 * 1024,
            cacheSize = 2L * 1024 * 1024 * 1024,
            tempSize = 1L * 1024 * 1024 * 1024,
            systemSize = 3L * 1024 * 1024 * 1024,
            otherSize = 2L * 1024 * 1024 * 1024,
            healthScore = 95,
            isPrimary = true,
            isHealthy = true
        )

        val breakdown = storageAnalyzer.getCategoryBreakdown(entity)

        assertEquals(11, breakdown.size)
        assertTrue(breakdown.containsKey(StorageStats.FileCategory.IMAGES))
        assertEquals(200, breakdown[StorageStats.FileCategory.IMAGES]?.fileCount)
        assertEquals(10L * 1024 * 1024 * 1024, breakdown[StorageStats.FileCategory.IMAGES]?.totalSize)
    }
}
