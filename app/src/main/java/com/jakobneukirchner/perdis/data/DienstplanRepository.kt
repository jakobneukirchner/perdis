package com.jakobneukirchner.perdis.data

import com.jakobneukirchner.perdis.data.util.WebViewScraperUtil
import com.jakobneukirchner.perdis.model.Dienst
import com.jakobneukirchner.perdis.model.Fahrt
import org.jsoup.Jsoup

class DienstplanRepository(
    private val webViewScraper: WebViewScraperUtil
) {

    suspend fun loadRoster(): List<Dienst> {
        return try {
            val html = webViewScraper.loginAndScrapeRoster(
                com.jakobneukirchner.perdis.model.Credentials("", "")
            )
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

    private fun parseRosterHtml(html: String): List<Dienst> {
        val doc = Jsoup.parse(html)
        val dienste = mutableListOf<Dienst>()

        try {
            val table = doc.select("table[summary], table.roster, table").firstOrNull()
            table?.select("tbody tr")?.forEachIndexed { index, row ->
                val cells = row.select("td")
                if (cells.size >= 4) {
                    val datum = cells.getOrNull(0)?.text() ?: ""
                    val von = cells.getOrNull(2)?.text() ?: ""
                    val nach = cells.getOrNull(3)?.text() ?: ""
                    val abfahrt = cells.getOrNull(1)?.text() ?: ""
                    val ankunft = cells.getOrNull(1)?.text() ?: ""
                    val linie = cells.getOrNull(4)?.text() ?: ""

                    val fahrt = Fahrt(
                        id = "$index",
                        abfahrtszeit = abfahrt,
                        ankunftszeit = ankunft,
                        linie = linie,
                        von = von,
                        nach = nach,
                        ort = von
                    )

                    val dienst = Dienst(
                        id = "$index",
                        datum = datum,
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
