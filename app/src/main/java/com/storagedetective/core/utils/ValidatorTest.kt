package com.storagedetective.core.utils

import org.junit.Assert.*
import org.junit.Test

class ValidatorTest {

    @Test
    fun testEmailValidation() {
        assertTrue("test@example.com".isValidEmail())
        assertTrue("user.name+tag@example.co.uk".isValidEmail())
        assertFalse("invalid-email".isValidEmail())
        assertFalse("test@.com".isValidEmail())
        assertFalse("@example.com".isValidEmail())
        assertFalse("test@example".isValidEmail())
    }

    @Test
    fun testPhoneValidation() {
        assertTrue("+1234567890".isValidPhone())
        assertTrue("123-456-7890".isValidPhone())
        assertTrue("(123) 456-7890".isValidPhone())
        assertFalse("abc".isValidPhone())
        assertFalse("123".isValidPhone())
    }

    @Test
    fun testUrlValidation() {
        assertTrue("https://example.com".isValidUrl())
        assertTrue("http://sub.domain.co.uk/path?query=1".isValidUrl())
        assertTrue("example.com".isValidUrl())
        assertFalse("invalid-url".isValidUrl())
        assertFalse("http://".isValidUrl())
    }

    @Test
    fun testPathValidation() {
        assertTrue(FilePathUtils.isValidPath(System.getProperty("java.io.tmpdir")))
        assertFalse(FilePathUtils.isValidPath("/nonexistent/path/that/does/not/exist"))
    }

    @Test
    fun testFilePermissions() {
        val tempFile = java.io.File.createTempFile("test", ".txt")
        tempFile.setReadable(true)
        tempFile.setWritable(true)

        assertTrue(tempFile.canRead())
        assertTrue(tempFile.canWrite())
        assertFalse(tempFile.canExecute())

        tempFile.delete()
    }

    @Test
    fun testHashComparison() {
        val hash1 = HashUtils.hashString("test")
        val hash2 = HashUtils.hashString("test")
        val hash3 = HashUtils.hashString("different")

        assertEquals(hash1, hash2)
        assertNotEquals(hash1, hash3)

        assertTrue(HashUtils.verifyHash("test", hash1))
        assertFalse(HashUtils.verifyHash("wrong", hash1))
    }

    @Test
    fun testSizeValidation() {
        val formatter = FileSizeFormatter

        assertNotNull(formatter.parse("1 KB"))
        assertNotNull(formatter.parse("1.5 MB"))
        assertNotNull(formatter.parse("2.3 GB"))
        assertNull(formatter.parse("invalid"))

        val parsed = formatter.parse("1 MB")
        assertTrue(parsed != null && parsed > 0)
    }

    @Test
    fun testDateValidation() {
        val validDate = "2024-01-15"
        val invalidDate = "2024-13-45"

        val parsed = DateUtils.parseDate(validDate, "yyyy-MM-dd")
        assertNotNull(parsed)

        val invalid = DateUtils.parseDate(invalidDate, "yyyy-MM-dd")
        assertNull(invalid)
    }

    @Test
    fun testTimeAgoValidation() {
        val now = System.currentTimeMillis()
        val timeAgo = DateUtils.getTimeAgo(now)
        assertNotNull(timeAgo)
        assertTrue(timeAgo.isNotEmpty())

        val future = now + 1000
        assertEquals("in the future", DateUtils.getTimeUntil(future))
    }

    @Test
    fun testStorageUnitConversion() {
        assertEquals(1024L, StorageUnits.KILOBYTE)
        assertEquals(1024 * 1024, StorageUnits.MEGABYTE)
        assertEquals(1024 * 1024 * 1024, StorageUnits.GIGABYTE)

        assertEquals("1.0 KB", StorageUnits.formatBytes(1024))
        assertEquals("1.0 MB", StorageUnits.formatBytes(1024 * 1024))
        assertEquals("1.0 GB", StorageUnits.formatBytes(1024 * 1024 * 1024))

        val parsed = StorageUnits.parseBytes("1 MB")
        assertEquals(1024 * 1024, parsed)
    }

    @Test
    fun testCategoryClassification() {
        assertEquals(AppConstants.FileCategory.IMAGES, FileTypes.getCategory("jpg"))
        assertEquals(AppConstants.FileCategory.VIDEOS, FileTypes.getCategory("mp4"))
        assertEquals(AppConstants.FileCategory.AUDIO, FileTypes.getCategory("mp3"))
        assertEquals(AppConstants.FileCategory.DOCUMENTS, FileTypes.getCategory("pdf"))
        assertEquals(AppConstants.FileCategory.APPS, FileTypes.getCategory("apk"))
        assertEquals(AppConstants.FileCategory.ARCHIVES, FileTypes.getCategory("zip"))
        assertEquals(AppConstants.FileCategory.OTHER, FileTypes.getCategory("unknown"))

        assertTrue(FileTypes.isMediaFile("jpg"))
        assertTrue(FileTypes.isDocument("pdf"))
        assertTrue(FileTypes.isExecutable("apk"))
    }
}
