package `in`.vinit.storyghost.core.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FileUtilsTest {

    @Test
    fun `getReadableFileSize returns 0 B for empty file`() {
        val file = File.createTempFile("test", "txt").apply {
            deleteOnExit()
        }
        assertEquals("0 B", FileUtils.getReadableFileSize(file))
    }

    @Test
    fun `getReadableFileSize formats KB correctly`() {
        val file = File.createTempFile("test_kb", "txt").apply {
            writeBytes(ByteArray(2048))
            deleteOnExit()
        }
        assertEquals("2 KB", FileUtils.getReadableFileSize(file))
    }
}
