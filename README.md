# 📱 AsilApp

AsilApp è un'applicazione Android sviluppata per supportare i richiedenti asilo nella gestione del loro profilo sanitario e nella comunicazione con gli operatori sanitari delle strutture di accoglienza.

## 🚀 Funzionalità principali

- **Gestione del profilo sanitario**: Diagnosi, terapie e vaccinazioni accessibili direttamente dall'app.
- **Gestione delle spese**: Tracciamento e analisi settimanale con grafici interattivi.
- **Mappa della struttura e POI**: Visualizzazione di ospedali, farmacie e supermercati nei dintorni.
- **Strumenti biomedici virtuali**: Simulazione di misurazioni sanitarie per il monitoraggio del paziente.
- **Sistema di messaggistica**: Comunicazione medico-paziente (attualmente unidirezionale).

## 🛠️ Tecnologie utilizzate

- **Linguaggio**: Java
- **IDE**: Android Studio
- **Database**: Firebase (Realtime Database, Firestore)
- **Librerie principali**:
  - MPAndroidChart e AnyChart per la visualizzazione dei dati
  - Google Maps API per la gestione della geolocalizzazione
  - ZXing per la scansione di QR code
  - YouTube Player API per l’integrazione di video informativi

## 📂 Struttura del progetto

```
AsilApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/asilapp/
│   │   │   ├── res/
│   │   ├── AndroidManifest.xml
├── gradle/
├── README.md
```

## 🔧 Installazione

1. **Clona il repository**
   ```bash
   git clone https://github.com/tuo-username/AsilApp.git
   ```
2. **Apri il progetto in Android Studio**
3. **Configura Firebase**:
   - Crea un progetto su Firebase Console
   - Scarica `google-services.json` e posizionalo nella cartella `app/`
4. **Esegui l’app** su un emulatore o dispositivo reale

## 📌 Possibili sviluppi futuri

- 📍 Integrazione GPS per percorsi verso i punti di interesse
- 📍 Estensione del profilo sanitario per includere prescrizioni digitali
- 📍 Chat bidirezionale per migliorare la comunicazione medico-paziente

## 👨‍💻 Team di sviluppo

- **Francesco Coletta** – Gestione spese, struttura, mappa, impostazioni
- **Giacomo Doria** – Login, registrazione, strumenti biomedici, video
- **Raffaele Monti** – Home, profilo sanitario, UI

---

