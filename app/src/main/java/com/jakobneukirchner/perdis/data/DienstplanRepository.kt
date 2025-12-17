package com.jakobneukirchner.perdis.data

import com.jakobneukirchner.perdis.data.util.PerdisWebViewManager
import com.jakobneukirchner.perdis.model.Dienst
import com.jakobneukirchner.perdis.model.Fahrt
import org.jsoup.Jsoup

class DienstplanRepository(
    private val webViewManager: PerdisWebViewManager
) {

    suspend fun loadShifts(): List<Dienst> {
        return try {
            // Get HTML from persistent WebView (already logged in)
            val html = webViewManager.getRosterHtml()
            if (html.isNotEmpty()) {
                parseRosterHtml(html)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getShiftDetails(date: String): List<Dienst> {
        return try {
            val html = webViewManager.getShiftHtml(date)
            if (html.isNotEmpty()) {
                parseShiftHtml(html)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseRosterHtml(html: String): List<Dienst> {
        val doc = Jsoup.parse(html)
        val dienste = mutableListOf<Dienst>()

        try {
            // Parse roster page (calendar view with shift numbers)
            val table = doc.select("table").firstOrNull()
            table?.select("tbody tr")?.forEachIndexed { index, row ->
                val cells = row.select("td")
                if (cells.size >= 2) {
                    val datum = cells.getOrNull(0)?.text() ?: ""
                    val schichtnummer = cells.getOrNull(1)?.text() ?: ""

                    val dienst = Dienst(
                        id = schichtnummer,
                        datum = datum,
                        bezeichnung = "Schicht $schichtnummer",
                        fahrten = emptyList()
                    )
                    if (datum.isNotEmpty()) {
                        dienste.add(dienst)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dienste
    }

    private fun parseShiftHtml(html: String): List<Dienst> {
        val doc = Jsoup.parse(html)
        val dienste = mutableListOf<Dienst>()

        try {
            val table = doc.select("table").firstOrNull()
            table?.select("tbody tr")?.forEachIndexed { index, row ->
                val cells = row.select("td")
                if (cells.size >= 7) {
                    val linie = cells.getOrNull(0)?.text() ?: ""
                    val abfahrt = cells.getOrNull(1)?.text() ?: ""
                    val ankunft = cells.getOrNull(2)?.text() ?: ""
                    val ort = cells.getOrNull(3)?.text() ?: ""

                    val fahrt = Fahrt(
                        id = "$index",
                        abfahrtszeit = abfahrt,
                        ankunftszeit = ankunft,
                        linie = linie,
                        von = ort,
                        nach = ort,
                        ort = ort
                    )

                    val dienst = Dienst(
                        id = "$index",
                        datum = "",
                        bezeichnung = "Fahrt",
                        fahrten = listOf(fahrt)
                    )
                    dienste.add(dienst)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dienste
    }
}
