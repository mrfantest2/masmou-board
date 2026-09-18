# Masmou Board — General Chat Handoff

Repository: https://github.com/mrfantest2/masmou-board
Local Android project: `C:\Users\Administrator\masmou-board`
Local project website: `C:\xampp\htdocs\masmou-board`
Website URL: http://localhost/masmou-board/

## Current verified status

Masmou Board v1.0 software MVP is implemented.

### Patient
- landscape bedside communication UI
- freehand drawing, colors, eraser, lock and protected clear
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- typed text -> local Android TextToSpeech
- Arabic / English live switch and RTL/LTR
- low-vision mode, semantics and haptics
- production BLE GATT server / advertising
- local communication remains usable without BLE or Internet

### Family
- separate Android companion app
- production BLE scan/connect/reconnect client
- bounded local event history
- timestamps and urgency highlighting
- repeated-pain rule: 2 Pain events within 60 minutes
- high-importance Android notification
- Arabic / English UI

### Shared core
- versioned `CommunicationEvent` protocol
- malformed-message rejection
- BLE UUID contract
- repeated-pain domain rule and unit tests

## Verification

Final build gates pass:

```
:core-model:test
:app-patient:assembleDebug
:app-patient:lintDebug
:app-family:assembleDebug
:app-family:lintDebug
BUILD SUCCESSFUL
```

Release regression QA uses Android emulator `emulator-5554`.
Verified: Patient launch, two Pain events, Family history, repeated-pain UI alert, notification, Family restart persistence, Arabic/RTL, low vision, protected clear and lock behavior.

Per user instruction, OnePlus 7 must not be used for current or future QA runs.

## Emulator transport boundary

Debug builds contain an emulator-only local event receiver so Patient -> Family behavior can be regression-tested without physical BLE hardware.
Production builds retain the real BLE implementation.

## Storage boundary

Family persistence uses Android `SQLiteOpenHelper`, bounded to the 100 newest events. Room was not added.

## Product invariants

- offline-first Patient communication
- no cloud backend / Firebase / analytics
- no continuous microphone recording
- no OCR / handwriting AI in v1
- no real emergency-service dialing
- Bluetooth/TTS failure must not block visual communication
- hardware/manufacturing remains deferred

## Start here

1. `AGENTS.md`
2. `docs/HANDOFF.md`
3. `docs/V1_IMPLEMENTATION.md`
4. active GitHub issue or explicit user request

## Release payload

- `website/downloads/masmou-patient-v1-debug.apk`
- `website/downloads/masmou-family-v1-debug.apk`
- `website/downloads/masmou-v1-handoff.zip`

Do not use destructive Git cleanup commands. Preserve `.tmp.driveupload/` and unrelated user files.
