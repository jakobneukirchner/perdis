# Perdis Android App

Android-Anwendung zum Auslesen und Anzeigen von Perdis-Dienstplänen mit WebView-basiertem Login und Perlschnur-Layout.

## Features

- **Unsichtbarer WebView-Login**: Automatische Authentifizierung ohne sichtbare UI
- **Dienstplan-Anzeige**: Perlschnur-ähnliches Layout mit Fahrtinformationen
- **PDF-Download**: Dienstplan als PDF herunterladen
- **PDF-Sharing**: Dienstplan über Android ShareSheet teilen
- **Verschlüsselte Speicherung**: Login-Daten verschlüsselt lokal speichern
- **Material Design 3**: Moderne Benutzeroberfläche

## Technologie-Stack

- **Kotlin** + **Coroutines**: Asynchrone Programmierung
- **Jetpack Compose**: Moderne UI-Framework
- **MVVM + Repository Pattern**: Saubere Architektur
- **Jsoup**: HTML-Scraping
- **EncryptedSharedPreferences**: Sichere Credential-Speicherung
- **Material Design 3**: Design System
- **Gradle DSL (Kotlin)**: Modern build system

## Installation & Start

### Schritt 1: Repository klonen

```bash
git clone https://github.com/jakobneukirchner/perdis.git
cd perdis
```

### Schritt 2: Android Studio öffnen

1. Android Studio starten
2. File > Open > perdis-Ordner wählen
3. Android Studio lädt automatisch Gradle

### Schritt 3: Gradle Sync

- "Sync Now" klicken wenn Dialog erscheint
- Warten bis alle Dependencies geladen sind (~5-10 Min.)

### Schritt 4: App starten

1. Run > Run 'app' oder grüner Play Button
2. Emulator oder physisches Gerät auswählen
3. App startet automatisch

## Login-Test

```
Benutzername: [Perdis Benutzername]
Passwort: [Perdis Passwort]
```

Nach erfolgreicher Anmeldung siehst du den Dienstplan mit Perlschnur-Layout.

## Architektur

```
app/src/main/java/com/jakobneukirchner/perdis/
├── MainActivity.kt                  # App-Einstieg + Navigation
├── model/Model.kt                   # Data Models
├── data/
│   ├── LoginRepository.kt          # Auth-Logik
│   ├── DienstplanRepository.kt     # Dienstplan-Scraping
│   ├── PDFRepository.kt            # PDF-Verwaltung
│   └── util/
│       ├── WebViewScraperUtil.kt   # Invisibles WebView-Login
│       ├── CredentialsManager.kt   # Verschlüsselte Speicherung
│       └── PDFUtil.kt              # PDF-Download & Sharing
├── viewmodel/
│   ├── LoginViewModel.kt          # Auth State Management
│   ├── DienstplanViewModel.kt     # Dienstplan State
│   └── PerdisViewModelFactory.kt  # Dependency Injection
└── ui/
    ├── LoginScreen.kt             # Login UI
    ├── DienstplanScreen.kt        # Dienstplan + Perlschnur
    └── theme/Theme.kt            # Material Design 3
```

## Verwendete APIs

- **Login**: `https://perdisweb.verkehrs-ag.de/WebComm/default.aspx`
- **Roster**: `https://perdisweb.verkehrs-ag.de/WebComm/roster.aspx`
- **PDF-Download**: `https://perdisweb.verkehrs-ag.de/WebComm/shiprint.aspx?YYYY-MM-DD`
- **Logout**: `https://perdisweb.verkehrs-ag.de/WebComm/logout.aspx`

## Build & Deployment

```bash
# Clean Build
./gradlew clean
./gradlew build

# Auf Gerät installieren
./gradlew installDebug

# Release Build
./gradlew assembleRelease
```

## Fehlerbehebung

### "Unable to find Gradle tasks"

```bash
./gradlew clean
./gradlew sync
```

### WebView Login schlägt fehl

1. Verifiziere Benutzerdaten
2. Prüfe Website-URL (kann sich geändert haben)
3. Prüfe HTML-Selektoren in WebViewScraperUtil.kt

### Gradle Sync Fehler

1. Project Structure (File > Project Structure)
2. SDK Location prüfen
3. Gradle JDK auf Java 17 setzen

## Lizenz

Privates Projekt

## Kontakt

Fragen? GitHub Issues erstellen: https://github.com/jakobneukirchner/perdis/issues
