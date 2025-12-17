# Quick Start - Perdis Android App

## 30 Sekunden Setup

```bash
# 1. Klonen
git clone https://github.com/jakobneukirchner/perdis.git

# 2. Öffnen
# Android Studio > File > Open > perdis-Ordner

# 3. Sync
# "Sync Now" klicken

# 4. Starten
# Grüner Play Button > Run
```

## Login

```
Benutzername: [Perdis Username]
Password: [Perdis Passwort]
```

## Was du siehst

1. **LoginScreen**: Benutzername + Passwort Eingabe
2. **DienstplanScreen**: Deine Dienste mit Perlschnur-Layout
   - Linie | Abfahrt → Ankunft | Ort

## Funktionen

- **Login**: Mit verschlüsselter Speicherung
- **Dienstplan**: Live von Perdis WebComm scraping
- **Logout**: Oben rechts "Mehr" > Logout

## Bei Problemen

```bash
# Terminal im perdis-Ordner
./gradlew clean
./gradlew build --stacktrace
```

Dann GitHub Issues posten.
