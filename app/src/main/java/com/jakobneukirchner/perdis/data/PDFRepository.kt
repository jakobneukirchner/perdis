package com.jakobneukirchner.perdis.data

import android.content.Context
import com.jakobneukirchner.perdis.data.util.PDFUtil
import com.jakobneukirchner.perdis.data.util.WebViewScraperUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PDFRepository(
    context: Context,
    private val webViewScraper: WebViewScraperUtil
) {
    private val pdfUtil = PDFUtil(context)

    suspend fun downloadDienstplanPdf(date: String): File? {
        return try {
            val pdfUrl = webViewScraper.getPdfUrl(date)
            val fileName = "Dienstplan_$date.pdf"
            pdfUtil.downloadPdf(pdfUrl, fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openPdfFile(file: File) {
        pdfUtil.openPdf(file)
    }

    fun sharePdfFile(file: File) {
        pdfUtil.sharePdf(file)
    }

    fun listDownloadedPdfs(): List<File> {
        return pdfUtil.listPdfs()
    }

    fun getPdfDirectory(): File {
        return pdfUtil.getPdfDirectory()
    }
}
