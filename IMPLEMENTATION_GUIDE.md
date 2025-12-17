# Implementierungs-Guide: Perdis Android App

## Überblick

Diese App folgt dem MVVM-Pattern mit Repository Layer für saubere Architektur.

## Phase 1: Authentifizierung

**Klassen**: `LoginRepository.kt`, `WebViewScraperUtil.kt`, `CredentialsManager.kt`

Das System:
1. Führt unsichtbares WebView-Login durch
2. Speichert Credentials verschlüsselt mit `EncryptedSharedPreferences`
3. Validiert Login durch Scraping von `roster.aspx`

**Verwendung**:
```kotlin
val creds = Credentials("username", "password")
val success = loginRepository.login(creds)
```

## Phase 2: Dienstplan-Scraping

**Klasse**: `DienstplanRepository.kt` + `WebViewScraperUtil.kt`

Das System:
1. Lädt HTML von `roster.aspx«
2. Parst Tabelle mit Jsoup
3. Extrahiert `Fahrt`-Objekte (Linie, Abfahrt, Ankunft, Ort)

**HTML-Parsing**:
```kotlin
val doc = Jsoup.parse(html)
val rows = doc.select("table tbody tr")
// Fahrt-Extraktion per CSS-Selektoren
```

## Phase 3: PDF-Management

**Klasse**: `PDFRepository.kt` + `PDFUtil.kt`

Das System:
1. Downloaded PDF von `shiprint.aspx?YYYY-MM-DD`
2. Speichert lokal in `getExternalFilesDir()/perdis_pdfs/`
3. Teilt über `Intent.ACTION_SEND` (ShareSheet)

**Download**:
```kotlin
val file = pdfRepository.downloadDienstplanPdf("2025-12-17")
pdfRepository.sharePdfFile(file)
```

## Phase 4: UI mit Compose

**Screens**:
- `LoginScreen`: Benutzername + Passwort Eingabe
- `DienstplanScreen`: Perlschnur-Layout Liste

**Perlschnur-Layout**:
```
Linie   Abfahrt  →  Ankunft   Ort
------- --------- --- --------- --------
Linie 5 08:15     →  08:45     Berlin
Linie 6 09:00     →  09:30     Hamburg
```

## Phase 5: State Management

**ViewModels**:
- `LoginViewModel`: Verwaltet Authentifizierungs-State
- `DienstplanViewModel`: Verwaltet Dienstplan-Daten

**State Flow Pattern**:
```kotlin
val state: StateFlow<DienstplanState> = _state
// UI beobachtet State änderungen
```

## Phase 6: Dependency Injection

**Factory**: `PerdisViewModelFactory.kt`

Erzeugt ViewModels mit allen Repositories:
```kotlin
val factory = PerdisViewModelFactory(context)
val loginVM = viewModel<LoginViewModel>(factory = factory)
```

## Testing

Unit Tests für:
- LoginRepository: Mock WebView, test Credentials
- DienstplanRepository: Jsoup-Parsing mit Test-HTML
- PDF Util: Datei-Erstellung und FileProvider

## Erweiterungen

### Kalender-Widget (Tageswahl)

Implementieren in `DienstplanScreen`:
```kotlin
var selectedDate by remember { mutableStateOf(LocalDate.now()) }
DatePicker(selectedDate) { date ->
    viewModel.loadDienstplanForDate(date)
}
```

### In-App PDF-Viewer

Option 1: WebView mit PDF-URL
```kotlin
WebView(...).apply {
    settings.javaScriptEnabled = true
    loadUrl("https://docs.google.com/gview?url=$pdfUrl")
}
```

Option 2: PDF-Library (z.B. PdfRenderer)
```gradle
implementation "com.github.barteksc:android-pdf-viewer:3.2.0-beta.1"
```

### Offline-Caching

```kotlin
val cachedPdfs = pdfRepository.listDownloadedPdfs()
if (cachedPdfs.isNotEmpty()) {
    // Show cached PDFs
}
```

## Troubleshooting

### WebView Login schlägt fehl

1. Prüfe aktuelle Website-Struktur (HTML-Selektoren könnten sich geändert haben)
2. Cookies werden gelöscht; setze `CookieManager.getInstance().setAcceptCookie(true)`
3. JavaScript aktivieren: `webView.settings.javaScriptEnabled = true`

### Jsoup Scraping findet Tabelle nicht

1. Debug HTML mit `println(html)` ausgeben
2. CSS-Selektoren anpassen (Inspector in Browser nutzen)
3. Probs: Tabelle wird durch JavaScript generiert → WebView evaluateJavascript nutzen

### PDF-Download funktioniert nicht

1. Prüfe `https://perdisweb.verkehrs-ag.de/WebComm/shiprint.aspx?YYYY-MM-DD` direkt im Browser
2. Verifiziere, dass Login-Session noch gültig ist
3. Probs: Falsche URL oder Session abgelaufen → Re-Login

## Performance-Tipps

- WebView-Scraping läuft im Hintergrund (Coroutines + IO Dispatcher)
- Jsoup-Parsing: Große HTML-Dokumente chunken?
- UI-Rendering mit Compose ist effizient; LazyColumn für lange Listen nutzen

## Sicherheit

- Credentials werden mit AES-256 verschlüsselt
- WebView hat keine persistent storage (Cache wird gelöscht)
- FileProvider kontrolliert PDF-Zugriff

## Zusammenfassung

1. **Architektur**: MVVM + Repository + Coroutines
2. **Auth**: Invisibles WebView-Login + EncryptedPrefs
3. **Scraping**: Jsoup HTML-Parsing
4. **UI**: Compose mit Perlschnur-Layout
5. **PDF**: Download + Share via Intent

Bei Fragen: GitHub Issues erstellen!
