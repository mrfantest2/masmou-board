# Masmou Board v1.0 — Implementation & Verification

Date: 18 September 2026

## Modules

### app-patient

- Kotlin + Jetpack Compose + Material 3
- landscape-first bedside UI
- drawing canvas with black / blue / red / green
- eraser, lock/unlock, protected clear
- eight quick communication needs
- local Android TextToSpeech
- typed speech and speech-rate control
- English / Arabic live switch with RTL/LTR
- low-vision mode, semantics and haptics
- keep-awake / immersive bedside behavior
- BLE advertising + GATT event notifications
- core communication still works if BLE is unavailable

### app-family

- separate Android companion app
- BLE scanning, connection and reconnect path
- versioned event decoding
- bounded local history (100 newest events)
- timestamps and urgency highlighting
- repeated-pain rule
- high-importance notification channel
- English / Arabic UI

### core-model

- `CommunicationEvent`
- `EventType`
- protocol v1 encoder/decoder
- malformed-message rejection
- BLE service / characteristic UUID contract
- repeated-pain domain rule
- unit tests

## Repeated-pain rule

Two Pain events within a rolling 60-minute window trigger the Family alert.

This is explicitly a communication alert, not a medical diagnosis.

## Verification

Final verification command:

```powershell
.\gradlew.bat --no-daemon --console=plain \
  :core-model:test \
  :app-patient:assembleDebug \
  :app-patient:lintDebug \
  :app-family:assembleDebug \
  :app-family:lintDebug
```

Result: `BUILD SUCCESSFUL`.

## Emulator QA

Device: `emulator-5554`

Passed:

- Patient launch
- Patient landscape UI
- two Pain events transferred through debug-only emulator transport
- Family shows two persisted Pain events
- repeated-pain UI alert
- Android notification posted
- Family force-stop/restart retains event history and alert state
- Arabic controls visible and RTL mode active
- low-vision control enabled successfully
- first Clear arms confirmation
- locked canvas changes action to Unlock and disables Clear

## BLE verification boundary

Production BLE source compiles and passes lint. Current release validation after the user's instruction uses only the emulator; OnePlus 7 is not part of the release QA evidence.

## Known implementation notes

- Family persistence uses `SQLiteOpenHelper` instead of Room.
- Android BLE compatibility paths intentionally use several deprecated platform calls for older API support; these are warnings, not build/lint failures.
- No cloud backend, analytics, continuous audio recording, OCR, or emergency dialing is included.
