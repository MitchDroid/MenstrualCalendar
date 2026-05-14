package com.bloomcycle.app.data.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles writing export files to the app's cache directory
 * and launching Android share intents.
 */
@Singleton
class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val EXPORT_DIR = "exports"
    }

    /**
     * Writes CSV content to a cache file and returns a share Intent.
     */
    fun exportCsv(csvContent: String): Intent {
        val fileName = "bloomcycle_logs_${LocalDate.now()}.csv"
        val file = writeToExportDir(fileName, csvContent)
        return createShareIntent(file, "text/csv", "Export BloomCycle Data (CSV)")
    }

    /**
     * Writes a text summary to a cache file and returns a share Intent.
     */
    fun exportTextReport(textContent: String): Intent {
        val fileName = "bloomcycle_report_${LocalDate.now()}.txt"
        val file = writeToExportDir(fileName, textContent)
        return createShareIntent(file, "text/plain", "Share BloomCycle Report")
    }

    /**
     * Creates a plain text share intent (no file — just inline text).
     * Useful for quick sharing to messaging apps.
     */
    fun shareTextSummary(textContent: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "BloomCycle Health Report")
            putExtra(Intent.EXTRA_TEXT, textContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    // ── Private Helpers ─────────────────────────────────────────

    private fun writeToExportDir(fileName: String, content: String): File {
        val exportDir = File(context.cacheDir, EXPORT_DIR).also {
            if (!it.exists()) it.mkdirs()
        }

        // Clean up old exports (keep last 5)
        exportDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.drop(5)
            ?.forEach { it.delete() }

        val file = File(exportDir, fileName)
        file.writeText(content)
        return file
    }

    private fun createShareIntent(file: File, mimeType: String, title: String): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
