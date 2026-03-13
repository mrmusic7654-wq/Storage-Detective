package com.storagedetective.core.utils

import org.junit.Assert.*
import org.junit.Test

class FormatterTest {

    @Test
    fun testFileSizeFormatter() {
        val formatter = FileSizeFormatter

        // Test format with different types
        assertEquals("1.2 GB", formatter.format(1_200_000_000))
        assertEquals("1.2 Gigabytes", formatter.format(1_200_000_000, FileSizeFormatter.FormatType.LONG))
        assertEquals("1.2G", formatter.format(1_200_000_000, FileSizeFormatter.FormatType.COMPACT))
        
        // Test with decimals
        assertEquals("1.20 GB", formatter.format(1_200_000_000, FileSizeFormatter.FormatType.SHORT, 2))
        
        // Test zero
        assertEquals("0 B", formatter.format(0))
        
        // Test negative
        assertEquals("-1.2 GB", formatter.format(-1_200_000_000))
    }

    @Test
    fun testFormatAuto() {
        assertEquals("512 B", FileSizeFormatter.formatAuto(512))
        assertEquals("1.5 KB", FileSizeFormatter.formatAuto(1536))
        assertEquals("2.3 MB", FileSizeFormatter.formatAuto(2_400_000))
        assertEquals("4.5 GB", FileSizeFormatter.formatAuto(4_800_000_000))
    }

    @Test
    fun testFormatWithColor() {
        val small = FileSizeFormatter.formatWithColor(512)
        val medium = FileSizeFormatter.formatWithColor(50 * 1024 * 1024)
        val large = FileSizeFormatter.formatWithColor(500 * 1024 * 1024)
        val huge = FileSizeFormatter.formatWithColor(2L * 1024 * 1024 * 1024)

        assertTrue(small.contains("B") || small.contains("KB"))
        assertTrue(medium.contains("📦") || medium.contains("MB"))
        assertTrue(large.contains("💾") || large.contains("GB"))
        assertTrue(huge.contains("🚀") || huge.contains("GB"))
    }

    @Test
    fun testFormatAsPercentage() {
        assertEquals("50.0%", FileSizeFormatter.formatAsPercentage(50, 100))
        assertEquals("33.3%", FileSizeFormatter.formatAsPercentage(33, 99))
        assertEquals("0%", FileSizeFormatter.formatAsPercentage(0, 100))
    }

    @Test
    fun testFormatProgressBar() {
        val progress = FileSizeFormatter.formatProgressBar(50, 100, 20)
        assertEquals(10, progress.count { it == '█' })
        assertEquals(10, progress.count { it == '░' })

        val full = FileSizeFormatter.formatProgressBar(100, 100, 20)
        assertEquals(20, full.count { it == '█' })

        val empty = FileSizeFormatter.formatProgressBar(0, 100, 20)
        assertEquals(20, empty.count { it == '░' })
    }

    @Test
    fun testFormatWithThreshold() {
        val normal = FileSizeFormatter.formatWithThreshold(100, 500, 1000)
        assertTrue(normal.startsWith("🟢"))

        val warning = FileSizeFormatter.formatWithThreshold(600, 500, 1000)
        assertTrue(warning.startsWith("🟡"))

        val critical = FileSizeFormatter.formatWithThreshold(1500, 500, 1000)
        assertTrue(critical.startsWith("🔴"))
    }

    @Test
    fun testFormatSpeed() {
        assertEquals("1.0 B/s", FileSizeFormatter.formatSpeed(1.0))
        assertEquals("1.5 KB/s", FileSizeFormatter.formatSpeed(1536.0))
        assertEquals("2.3 MB/s", FileSizeFormatter.formatSpeed(2_400_000.0))
        assertEquals("4.5 GB/s", FileSizeFormatter.formatSpeed(4_800_000_000.0))
    }

    @Test
    fun testFormatTimeRemaining() {
        assertEquals("10s", FileSizeFormatter.formatTimeRemaining(10 * 1024 * 1024, 1024 * 1024))
        assertEquals("2m 30s", FileSizeFormatter.formatTimeRemaining(150 * 1024 * 1024, 1024 * 1024))
        assertEquals("1h 0m", FileSizeFormatter.formatTimeRemaining(3600 * 1024 * 1024, 1024 * 1024))
        assertEquals("2d 3h", FileSizeFormatter.formatTimeRemaining(183 * 3600 * 1024 * 1024, 1024 * 1024))
        assertEquals("Unknown", FileSizeFormatter.formatTimeRemaining(100, 0.0))
    }

    @Test
    fun testParse() {
        assertEquals(1024, FileSizeFormatter.parse("1 KB"))
        assertEquals(1024 * 1024, FileSizeFormatter.parse("1 MB"))
        assertEquals(1024 * 1024 * 1024, FileSizeFormatter.parse("1 GB"))
        assertEquals(1024L * 1024 * 1024 * 1024, FileSizeFormatter.parse("1 TB"))
        assertNull(FileSizeFormatter.parse("invalid"))
    }

    @Test
    fun testCompare() {
        assertTrue(FileSizeFormatter.compare(100, 200) < 0)
        assertTrue(FileSizeFormatter.compare(200, 100) > 0)
        assertTrue(FileSizeFormatter.compare(100, 100) == 0)
    }

    @Test
    fun testGetCategory() {
        assertEquals(FileSizeFormatter.SizeCategory.BYTE, FileSizeFormatter.getCategory(500))
        assertEquals(FileSizeFormatter.SizeCategory.KILOBYTE, FileSizeFormatter.getCategory(5 * 1024))
        assertEquals(FileSizeFormatter.SizeCategory.SMALL_MEGABYTE, FileSizeFormatter.getCategory(5 * 1024 * 1024))
        assertEquals(FileSizeFormatter.SizeCategory.LARGE_GIGABYTE, FileSizeFormatter.getCategory(500L * 1024 * 1024 * 1024))
    }

    @Test
    fun testGetValueAndUnit() {
        val (value, unit) = FileSizeFormatter.getValueAndUnit(1_500_000)
        assertEquals(1.5, value, 0.1)
        assertEquals("MB", unit)
    }

    @Test
    fun testFormatWithCommas() {
        assertEquals("1,234,567", FileSizeFormatter.formatWithCommas(1_234_567))
        assertEquals("1,000", FileSizeFormatter.formatWithCommas(1000))
    }

    @Test
    fun testConvertToUnit() {
        assertEquals(1.0, FileSizeFormatter.convertToUnit(1024, "KB"), 0.001)
        assertEquals(1.0, FileSizeFormatter.convertToUnit(1024 * 1024, "MB"), 0.001)
        assertEquals(1.0, FileSizeFormatter.convertToUnit(1024 * 1024 * 1024, "GB"), 0.001)
    }

    @Test
    fun testCompressionRatio() {
        assertEquals(2.0, FileSizeFormatter.compressionRatio(200, 100), 0.001)
        assertEquals(1.5, FileSizeFormatter.compressionRatio(150, 100), 0.001)
        assertEquals(0.0, FileSizeFormatter.compressionRatio(100, 0), 0.001)
    }

    @Test
    fun testFormatCompressionRatio() {
        assertTrue(FileSizeFormatter.formatCompressionRatio(200, 100).contains("Excellent"))
        assertTrue(FileSizeFormatter.formatCompressionRatio(130, 100).contains("Good"))
        assertTrue(FileSizeFormatter.formatCompressionRatio(110, 100).contains("Moderate"))
        assertTrue(FileSizeFormatter.formatCompressionRatio(100, 100).contains("Poor"))
    }
}
