package com.storagedetective.core.utils

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.*

class ExtensionsTest {

    @Test
    fun testStringExtensions() {
        assertTrue("test@example.com".isValidEmail())
        assertFalse("invalid-email".isValidEmail())

        assertTrue("https://example.com".isValidUrl())
        assertFalse("invalid-url".isValidUrl())

        assertEquals("Hello World", "hello world".capitalizeWords())
        assertEquals("Hello...", "Hello World".truncate(5))
    }

    @Test
    fun testNumberExtensions() {
        assertEquals("50.0%", 50.0.toPercentage())
        assertEquals("$50.00", 50.0.toCurrency())

        assertEquals(1.0, 1024L.toKb(), 0.001)
        assertEquals(1.0, (1024 * 1024).toLong().toMb(), 0.001)
        assertEquals(1.0, (1024 * 1024 * 1024).toLong().toGb(), 0.001)
    }

    @Test
    fun testBooleanExtensions() {
        assertEquals(1, true.toInt())
        assertEquals(0, false.toInt())
        assertEquals("Yes", true.toYesNo())
        assertEquals("No", false.toYesNo())
        assertEquals("Enabled", true.toEnabledDisabled())
        assertEquals("Disabled", false.toEnabledDisabled())
    }

    @Test
    fun testDateExtensions() {
        val date = Date()
        val formatted = date.format("dd/MM/yyyy")
        assertNotNull(formatted)

        val timestamp = System.currentTimeMillis()
        assertTrue(timestamp.toDate() is Date)
    }

    @Test
    fun testFileExtensions() {
        val file = File.createTempFile("test", ".txt")
        file.writeText("test")

        assertTrue(file.isFile)
        assertTrue(file.exists())

        val sizeFormatted = file.formatSize()
        assertNotNull(sizeFormatted)

        assertFalse(file.isImage())
        assertFalse(file.isVideo())
        assertFalse(file.isAudio())
        assertFalse(file.isDocument())
        assertFalse(file.isArchive())
        assertFalse(file.isApk())

        file.delete()
    }

    @Test
    fun testCollectionExtensions() {
        val list = listOf(1, 2, 3, 4, 5)
        
        assertEquals(arrayListOf(1, 2, 3), list.toArrayList())
        assertEquals(listOf(1, 2), list.chunkedBySize(2)[0])
        assertEquals(3.0, list.median(), 0.001)
        assertTrue(list.randomElement() in list)

        val mutableList = list.toMutableList()
        mutableList.move(0, 2)
        assertEquals(listOf(2, 3, 1, 4, 5), mutableList)
    }

    @Test
    fun testMapExtensions() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        val sorted = map.toSortedMapByValue(compareBy { it.value })
        assertEquals(listOf("a", "b", "c"), sorted.keys.toList())
    }

    @Test
    fun testListMedian() {
        assertEquals(3.0, listOf(1, 2, 3, 4, 5).median(), 0.001)
        assertEquals(2.5, listOf(1, 2, 3, 4).median(), 0.001)
        assertEquals(0.0, emptyList<Int>().median(), 0.001)
    }

    @Test
    fun testTimeAgo() {
        val now = System.currentTimeMillis()
        
        assertEquals("just now", now.toDate().toTimeAgo())
        assertEquals("2 minutes ago", (now - 2 * 60 * 1000).toDate().toTimeAgo())
        assertEquals("3 hours ago", (now - 3 * 60 * 60 * 1000).toDate().toTimeAgo())
        assertEquals("5 days ago", (now - 5 * 24 * 60 * 60 * 1000).toDate().toTimeAgo())
    }

    @Test
    fun testThrottleFirst() = runBlocking {
        val flow = flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
        }.throttleFirst(150)

        val values = flow.toList()
        assertEquals(listOf(1, 3), values)
    }

    @Test
    fun testLaunchDelayed() = runBlocking {
        var executed = false
        val job = GlobalScope.launchDelayed(100) {
            executed = true
        }
        delay(50)
        assertFalse(executed)
        delay(100)
        assertTrue(executed)
        job.cancel()
    }
}
