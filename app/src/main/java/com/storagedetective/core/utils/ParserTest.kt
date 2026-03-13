package com.storagedetective.core.utils

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class ParserTest {

    @Test
    fun testParseFileSize() {
        val parser = FileSizeFormatter

        assertEquals(1024, parser.parse("1 KB"))
        assertEquals(1024, parser.parse("1kb"))
        assertEquals(1024, parser.parse("1 KB"))
        assertEquals(1024 * 1024, parser.parse("1 MB"))
        assertEquals(1024 * 1024 * 1024, parser.parse("1 GB"))
        assertEquals(1024L * 1024 * 1024 * 1024, parser.parse("1 TB"))

        assertNull(parser.parse(""))
        assertNull(parser.parse("invalid"))
        assertNull(parser.parse("1XB"))
    }

    @Test
    fun testParseDate() {
        val parser = DateUtils

        val date1 = parser.parseDate("2024-01-15", "yyyy-MM-dd")
        assertNotNull(date1)

        val date2 = parser.parseDate("15/01/2024", "dd/MM/yyyy")
        assertNotNull(date2)

        val date3 = parser.parseDate("2024-01-15T14:30:00Z", DateUtils.FORMAT_ISO)
        assertNotNull(date3)

        val invalid = parser.parseDate("invalid", "yyyy-MM-dd")
        assertNull(invalid)
    }

    @Test
    fun testParseJson() {
        val jsonString = """
            {
                "name": "test",
                "value": 123,
                "items": [1, 2, 3]
            }
        """

        val json = JSONObject(jsonString)
        assertEquals("test", json.getString("name"))
        assertEquals(123, json.getInt("value"))
        assertEquals(3, json.getJSONArray("items").length())
    }

    @Test
    fun testParsePath() {
        val path = "/storage/emulated/0/Download/file.txt"

        val fileName = FilePathUtils.getFileNameFromPath(path)
        assertEquals("file.txt", fileName)

        val extension = FilePathUtils.getFileExtension(path)
        assertEquals("txt", extension)

        val parent = FilePathUtils.getParentPath(path)
        assertEquals("/storage/emulated/0/Download", parent)

        val nameWithoutExt = FilePathUtils.getFileNameWithoutExtension(path)
        assertEquals("file", nameWithoutExt)
    }

    @Test
    fun testParseHash() {
        val input = "test string"
        val md5 = HashUtils.hashString(input, HashUtils.HashAlgorithm.MD5)
        val sha1 = HashUtils.hashString(input, HashUtils.HashAlgorithm.SHA1)
        val sha256 = HashUtils.hashString(input, HashUtils.HashAlgorithm.SHA256)

        assertNotNull(md5)
        assertNotNull(sha1)
        assertNotNull(sha256)

        assertNotEquals(md5, sha1)
        assertNotEquals(md5, sha256)
        assertNotEquals(sha1, sha256)

        assertEquals(32, md5.length)
        assertEquals(40, sha1.length)
        assertEquals(64, sha256.length)
    }

    @Test
    fun testParseHex() {
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        val hex = HashUtils.bytesToHex(bytes)
        assertEquals("12345678", hex)

        val parsedBytes = HashUtils.hexToBytes(hex)
        assertArrayEquals(bytes, parsedBytes)
    }

    @Test
    fun testParseTimeAgo() {
        val now = System.currentTimeMillis()
        
        val justNow = DateUtils.getTimeAgo(now)
        assertEquals("just now", justNow)

        val minutes = DateUtils.getTimeAgo(now - 5 * 60 * 1000)
        assertTrue(minutes.contains("minutes"))

        val hours = DateUtils.getTimeAgo(now - 2 * 60 * 60 * 1000)
        assertTrue(hours.contains("hours"))

        val days = DateUtils.getTimeAgo(now - 3 * 24 * 60 * 60 * 1000)
        assertTrue(days.contains("days"))
    }

    @Test
    fun testParseTimeUntil() {
        val now = System.currentTimeMillis()
        
        val future = now + 5 * 60 * 1000
        val minutes = DateUtils.getTimeUntil(future)
        assertTrue(minutes.contains("minutes"))

        val farFuture = now + 2 * 60 * 60 * 1000
        val hours = DateUtils.getTimeUntil(farFuture)
        assertTrue(hours.contains("hours"))
    }

    @Test
    fun testParseDuration() {
        assertEquals("01:30", DateUtils.formatDuration(90 * 1000))
        assertEquals("02:05:30", DateUtils.formatDuration(2 * 60 * 60 * 1000 + 5 * 60 * 1000 + 30 * 1000))
        assertEquals("0:45", DateUtils.formatDuration(45 * 1000))
    }

    @Test
    fun testParseFileType() {
        val fileType = FileTypes.fromExtension("jpg")
        assertNotNull(fileType)
        assertEquals(AppConstants.FileCategory.IMAGES, fileType?.category)

        val mimeType = FileTypes.getMimeType("pdf")
        assertEquals("application/pdf", mimeType)

        assertTrue(FileTypes.isMediaFile("mp4"))
        assertTrue(FileTypes.isDocument("pdf"))
        assertTrue(FileTypes.isExecutable("apk"))
    }

    @Test
    fun testParseCategory() {
        assertEquals("Images", FileTypes.getCategoryDisplayName(AppConstants.FileCategory.IMAGES))
        assertEquals("📸", FileTypes.getCategoryEmoji(AppConstants.FileCategory.IMAGES))
        assertTrue(FileTypes.getCategoryColor(AppConstants.FileCategory.IMAGES) != 0)
    }

    @Test
    fun testParseStorageStats() {
        val stats = StorageStats.createSample()
        
        assertTrue(stats.totalSpace > 0)
        assertTrue(stats.usedSpace > 0)
        assertTrue(stats.freeSpace > 0)

        assertNotNull(stats.usedSpaceFormatted)
        assertNotNull(stats.freeSpaceFormatted)
        assertNotNull(stats.totalSpaceFormatted)

        assertTrue(stats.usedPercentage > 0)
        assertTrue(stats.freePercentage > 0)
    }
}
