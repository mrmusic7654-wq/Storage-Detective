package com.storagedetective.core.utils

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class UtilsTest {

    @Test
    fun testFormatSize() {
        assertEquals("0 B", FileSizeFormatter.format(0))
        assertEquals("1 B", FileSizeFormatter.format(1))
        assertEquals("1.0 KB", FileSizeFormatter.format(1024))
        assertEquals("1.0 MB", FileSizeFormatter.format(1024 * 1024))
        assertEquals("1.0 GB", FileSizeFormatter.format(1024 * 1024 * 1024))
        assertEquals("1.0 TB", FileSizeFormatter.format(1024L * 1024 * 1024 * 1024))
    }

    @Test
    fun testParseSize() {
        assertEquals(1024, FileSizeFormatter.parse("1 KB"))
        assertEquals(1024 * 1024, FileSizeFormatter.parse("1 MB"))
        assertEquals(1024 * 1024 * 1024, FileSizeFormatter.parse("1 GB"))
        assertEquals(1024L * 1024 * 1024 * 1024, FileSizeFormatter.parse("1 TB"))
        assertNull(FileSizeFormatter.parse("invalid"))
    }

    @Test
    fun testFormatSpeed() {
        assertEquals("1.0 B/s", FileSizeFormatter.formatSpeed(1.0))
        assertEquals("1.0 KB/s", FileSizeFormatter.formatSpeed(1024.0))
        assertEquals("1.0 MB/s", FileSizeFormatter.formatSpeed(1024.0 * 1024.0))
        assertEquals("1.0 GB/s", FileSizeFormatter.formatSpeed(1024.0 * 1024.0 * 1024.0))
    }

    @Test
    fun testTimeAgo() {
        val now = System.currentTimeMillis()
        assertEquals("just now", DateUtils.getTimeAgo(now))
        assertEquals("5 minutes ago", DateUtils.getTimeAgo(now - 5 * 60 * 1000))
        assertEquals("2 hours ago", DateUtils.getTimeAgo(now - 2 * 60 * 60 * 1000))
        assertEquals("3 days ago", DateUtils.getTimeAgo(now - 3 * 24 * 60 * 60 * 1000))
    }

    @Test
    fun testIsToday() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.isToday(now))
        assertFalse(DateUtils.isToday(now - 24 * 60 * 60 * 1000))
    }

    @Test
    fun testIsSameDay() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.isSameDay(now, now))
        assertFalse(DateUtils.isSameDay(now, now - 24 * 60 * 60 * 1000))
    }

    @Test
    fun testHashString() {
        val input = "test"
        val md5 = HashUtils.hashString(input, HashUtils.HashAlgorithm.MD5)
        val sha256 = HashUtils.hashString(input, HashUtils.HashAlgorithm.SHA256)
        
        assertNotNull(md5)
        assertNotNull(sha256)
        assertNotEquals(md5, sha256)
    }

    @Test
    fun testVerifyHash() {
        val input = "password"
        val salt = HashUtils.generateSalt()
        val hash = HashUtils.hashWithSalt(input, salt)
        
        assertTrue(HashUtils.verifyHash(input, hash, HashUtils.HashAlgorithm.SHA256, salt))
        assertFalse(HashUtils.verifyHash("wrong", hash, HashUtils.HashAlgorithm.SHA256, salt))
    }

    @Test
    fun testGenerateSalt() {
        val salt1 = HashUtils.generateSalt()
        val salt2 = HashUtils.generateSalt()
        
        assertNotNull(salt1)
        assertNotNull(salt2)
        assertNotEquals(salt1, salt2)
    }

    @Test
    fun testGetFileExtension() {
        assertEquals("txt", FilePathUtils.getFileExtension("/path/file.txt"))
        assertEquals("jpg", FilePathUtils.getFileExtension("image.jpg"))
        assertEquals("", FilePathUtils.getFileExtension("file"))
    }

    @Test
    fun testIsHiddenPath() {
        assertTrue(FilePathUtils.isHiddenPath("/path/.hidden"))
        assertFalse(FilePathUtils.isHiddenPath("/path/file.txt"))
    }

    @Test
    fun testJoinPaths() {
        assertEquals("/path/to/file", FilePathUtils.joinPaths("/path", "to", "file"))
        assertEquals("/path/to/file", FilePathUtils.joinPaths("/path/", "/to/", "/file"))
    }
}
