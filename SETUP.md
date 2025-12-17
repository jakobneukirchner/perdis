# Setup Guide für Perdis App

## Schritt 1: Repository klonen

```bash
cd ~/AndroidProjects
git clone https://github.com/jakobneukirchner/perdis.git
cd perdis
```

## Schritt 2: Android Studio laden

1. Android Studio starten
2. "Open an Existing Project" wählen
3. Den `perdis`-Ordner auswählen

## Schritt 3: Gradle Sync

1. Android Studio wird automatisch fragen, ob Gradle Sync durchgeführt werden soll
2. "Sync Now" klicken
3. Warten bis alle Dependencies heruntergeladen sind (~5-10 Min.)

## Schritt 4: Emulator/Gerät vorbereiten

### Emulator (empfohlen für Entwicklung):

1. AVD Manager öffnen (Tools > Device Manager)
2. Neues Virtual Device anlegen oder existiertes starten
3. Min. Android 7.0 (API 24)

### Physisches Gerät:

1. USB-Debugging aktivieren (Einstellungen > Entwickleroptionen)
2. Per USB mit Computer verbinden
3. Vertrauenserkennung akzeptieren

## Schritt 5: App starten

1. Run > Run 'app' (oder Green Play Button)
2. Gerät auswählen
3. Warten bis App startet

## Fehlerbehandlung

### Gradle Sync schlägt fehl

```bash
# Terminal öffnen und durchführen:
cd perdis
./gradlew clean
./gradlew build
```

### Java/Kotlin Fehler

1. Project Structure (File > Project Structure)
2. SDK Location überprüfen
3. Gradle JDK auf Temurin 17 setzen

### WebView Fehler

1. Im Manifest prüfen, dass `android.permission.INTERNET` vorhanden ist
2. Emulator: Chrome per `adb install` manuell installieren, falls nötig

## Debugging

```bash
# Logcat anschauen
adb logcat | grep perdis

# Auf Gerät debuggen
adb shell am start -n com.jakobneukirchner.perdis/.MainActivity
```
