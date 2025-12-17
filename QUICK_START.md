# Perdis Android App - Quick Start

## Schritt 1: Repository klonen

```bash
git clone https://github.com/jakobneukirchner/perdis.git
cd perdis
```

## Schritt 2: Android Studio öffnen

1. Android Studio starten
2. File > Open > perdis-Ordner
3. Gradle Sync warten (grüner Play-Button erscheint)

## Schritt 3: App starten

1. Grünen Play-Button klicken
2. Emulator oder Gerät auswählen
3. App startet automatisch

## Login testen

Benutzername: `[dein Perdis Benutzername]`
Password: `[dein Perdis Passwort]`

Nach erfolgreichem Login siehst du den Dienstplan mit Perlschnur-Layout.

## Features zum Ausprobieren

- **Dienstplan-Liste**: Scrolle durch die Dienste
- **PDF-Download**: Klick auf "PDF" Button in jeder Dienst-Karte
- **PDF-Sharing**: Nach Download: Long-Press oder Share-Button
- **Logout**: Oben rechts auf "Mehr"-Button

## Wenn etwas nicht funktioniert

1. Gradle Clean + Rebuild (Build > Clean Project)
2. Android Studio neu starten
3. Emulator neu starten (virtuelles Gerät stoppen/starten)
4. GitHub Issues erstellen

## Nächste Entwicklung

- [ ] Kalender-Widget für Datumswahl
- [ ] "Mein Tag" Screen
- [ ] In-App PDF-Viewer
- [ ] Offline-PDF-Caching
- [ ] Unit Tests

## Dateistruktur

```
perdis/
├── app/src/main/java/com/jakobneukirchner/perdis/
│   ├── MainActivity.kt              # App-Einstieg
│   ├── model/Model.kt               # Datenmodelle
│   ├── data/                        # Repositories + Utils
│   ├── viewmodel/                   # State Management
│   └── ui/                          # Compose Screens
├── app/build.gradle                 # Dependencies
├── build.gradle                     # Root Gradle
├── README.md                        # Dokumentation
├── SETUP.md                         # Ausführliches Setup
└── IMPLEMENTATION_GUIDE.md          # Technische Details
```

## Kontakt

Fragen? GitHub Issues unter https://github.com/jakobneukirchner/perdis/issues
