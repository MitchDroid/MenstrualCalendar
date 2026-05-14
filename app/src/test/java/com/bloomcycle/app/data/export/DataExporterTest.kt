package com.bloomcycle.app.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * JVM unit tests for [DataExporter].
 *
 * Intent property assertions (action, type, extras, flags) are NOT possible in
 * JVM tests because android.content.Intent is a stub that doesn't store values.
 * Those behaviors are verified in instrumented (androidTest) tests instead.
 * Here we focus on: file creation, content, naming, cleanup, and that each
 * public method completes without error.
 */
class DataExporterTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var dataExporter: DataExporter
    private lateinit var cacheDir: File

    private val fakeUri: Uri = mockk(relaxed = true)

    @Before
    fun setUp() {
        cacheDir = tmpFolder.newFolder("cache")

        context = mockk(relaxed = true)
        every { context.cacheDir } returns cacheDir
        every { context.packageName } returns "com.bloomcycle.app"

        // Mock Uri.parse — android.net.Uri is not available in JVM tests
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns fakeUri

        // Mock FileProvider to return the fake URI
        mockkStatic(FileProvider::class)
        every {
            FileProvider.getUriForFile(any(), any(), any())
        } returns fakeUri

        dataExporter = DataExporter(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(FileProvider::class)
        unmockkStatic(Uri::class)
    }

    // ── exportCsv() ───────────────────────────────────────────

    @Test
    fun `exportCsv creates file in exports directory`() {
        dataExporter.exportCsv("Date,Flow\n2026-05-14,Heavy")

        val exportDir = File(cacheDir, "exports")
        assertTrue(exportDir.exists())
        assertTrue(exportDir.listFiles()!!.isNotEmpty())
    }

    @Test
    fun `exportCsv file contains provided content`() {
        val csvContent = "Date,Flow,Mood\n2026-05-14,Heavy,Happy"
        dataExporter.exportCsv(csvContent)

        val exportDir = File(cacheDir, "exports")
        val file = exportDir.listFiles()!!.first()

        assertEquals(csvContent, file.readText())
    }

    @Test
    fun `exportCsv file name contains csv extension`() {
        dataExporter.exportCsv("data")

        val exportDir = File(cacheDir, "exports")
        val file = exportDir.listFiles()!!.first()

        assertTrue(file.name.endsWith(".csv"))
    }

    @Test
    fun `exportCsv returns non-null intent`() {
        val intent = dataExporter.exportCsv("data")

        assertNotNull(intent)
    }

    @Test
    fun `exportCsv calls FileProvider for URI`() {
        dataExporter.exportCsv("data")

        verify { FileProvider.getUriForFile(any(), any(), any()) }
    }

    // ── exportTextReport() ────────────────────────────────────

    @Test
    fun `exportTextReport creates txt file`() {
        dataExporter.exportTextReport("Report content here")

        val exportDir = File(cacheDir, "exports")
        val file = exportDir.listFiles()!!.first()

        assertTrue(file.name.endsWith(".txt"))
        assertEquals("Report content here", file.readText())
    }

    @Test
    fun `exportTextReport returns non-null intent`() {
        val intent = dataExporter.exportTextReport("content")

        assertNotNull(intent)
    }

    @Test
    fun `exportTextReport calls FileProvider for URI`() {
        dataExporter.exportTextReport("content")

        verify { FileProvider.getUriForFile(any(), any(), any()) }
    }

    // ── shareTextSummary() ────────────────────────────────────

    @Test
    fun `shareTextSummary returns non-null intent`() {
        val intent = dataExporter.shareTextSummary("Quick summary")

        assertNotNull(intent)
    }

    @Test
    fun `shareTextSummary does not create any files`() {
        dataExporter.shareTextSummary("Inline text")

        val exportDir = File(cacheDir, "exports")
        // Export dir might not exist or should be empty
        assertFalse(exportDir.exists() && exportDir.listFiles()!!.isNotEmpty())
    }

    @Test
    fun `shareTextSummary does not call FileProvider`() {
        dataExporter.shareTextSummary("Inline text")

        verify(exactly = 0) { FileProvider.getUriForFile(any(), any(), any()) }
    }

    // ── File cleanup ──────────────────────────────────────────

    @Test
    fun `export cleans up old files keeping only last 5`() {
        // Pre-create 7 old files in the exports directory
        val exportDir = File(cacheDir, "exports").also { it.mkdirs() }
        (1..7).forEach { i ->
            File(exportDir, "old_file_$i.csv").also {
                it.writeText("old content $i")
                // Stagger modification times
                it.setLastModified(System.currentTimeMillis() - (10_000L * i))
            }
        }

        // Export a new file — this should trigger cleanup
        dataExporter.exportCsv("new data")

        val remainingFiles = exportDir.listFiles()!!
        assertTrue(
            "Expected at most 6 files after cleanup, got ${remainingFiles.size}",
            remainingFiles.size <= 6
        )
    }

    @Test
    fun `export creates exports directory if it does not exist`() {
        val exportDir = File(cacheDir, "exports")
        assertFalse(exportDir.exists())

        dataExporter.exportCsv("first export")

        assertTrue(exportDir.exists())
        assertTrue(exportDir.isDirectory)
    }
}
