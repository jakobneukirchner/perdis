# Perdis Android App

Android-Anwendung zum Auslesen und Anzeigen von Perdis-Dienstplänen mit WebView-basiertem Login und Perlschnur-Layout.

## Features

- **Unsichtbarer WebView-Login**: Automatische Authentifizierung ohne sichtbare UI
- **Dienstplan-Anzeige**: Perlschnur-ähnliches Layout mit Fahrtinformationen
- **PDF-Download**: Dienstplan als PDF herunterladen
- **PDF-Sharing**: Dienstplan über Android ShareSheet teilen
- **Verschüsselte Speicherung**: Login-Daten verschlüsselt lokal speichern
- **Material Design 3**: Moderne Benutzeroberfläche

## Technologie-Stack

- **Kotlin & Coroutines**: Asynchrone Programmierung
- **Jetpack Compose**: Moderne UI-Framework
- **MVVM + Repository Pattern**: Saubere Architektur
- **Jsoup**: HTML-Scraping
- **EncryptedSharedPreferences**: Sichere Credential-Speicherung
- **Material Design 3**: Design System

## Installation

1. Repository klonen:
```bash
git clone https://github.com/jakobneukirchner/perdis.git
```

2. In Android Studio öffnen

3. Gradle Sync durchführen

4. Auf Emulator oder Gerät starten (min. Android 7.0 / API 24)

## Architektur

```
app/src/main/java/com/jakobneukirchner/perdis/
├── MainActivity.kt
├── model/Model.kt
├── data/
│   ├── LoginRepository.kt
│   ├── DienstplanRepository.kt
│   ├── PDFRepository.kt
│   └── util/
├── viewmodel/
│   ├── LoginViewModel.kt
│   ├── DienstplanViewModel.kt
│   └── PerdisViewModelFactory.kt
└── ui/
    ├── LoginScreen.kt
    ├── DienstplanScreen.kt
    └── theme/Theme.kt
```

## Verwendete APIs

- **Login**: `https://perdisweb.verkehrs-ag.de/WebComm/default.aspx`
- **Roster**: `https://perdisweb.verkehrs-ag.de/WebComm/roster.aspx`
- **PDF-Download**: `https://perdisweb.verkehrs-ag.de/WebComm/shiprint.aspx?YYYY-MM-DD`

## Lizenz

Privates Projekt
