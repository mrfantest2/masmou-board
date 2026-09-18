# Masmou Board — Current Handoff

Repository: `mrfantest2/masmou-board`
Local source: `C:\Users\Administrator\masmou-board`
Local project hub: `C:\xampp\htdocs\masmou-board`

## Current phase

Android-first software prototype v1.0.

## Current implementation

The software prototype is functionally complete for the agreed MVP scope:

- `app-patient` — landscape bedside communication app
- `app-family` — companion event/history/alert app
- `core-model` — shared versioned communication event protocol
- local Android TextToSpeech
- freehand finger/stylus canvas
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- typed text -> Speak
- Arabic / English with RTL/LTR
- low-vision mode, semantics and haptics
- Patient BLE GATT server and Family BLE client/reconnect path
- bounded local Family event history
- repeated-pain rule: 2 Pain events within 60 minutes
- high-priority Family notification
- bedside keep-awake / immersive behavior

## Verification snapshot — 18 September 2026

Final Gradle verification passed:

```
:core-model:test
:app-patient:assembleDebug
:app-patient:lintDebug
:app-family:assembleDebug
:app-family:lintDebug
BUILD SUCCESSFUL
```

Emulator `emulator-5554` QA passed for:

- Patient launch / landscape layout
- two Pain events -> Family event history
- repeated-pain UI alert
- Android notification
- Family restart persistence
- Arabic UI / RTL
- low-vision toggle
- two-step clear confirmation
- canvas lock disabling Clear

Per user instruction, OnePlus 7 is excluded from current and future QA runs.

## Transport note

Production builds retain the real BLE implementation.
Debug builds include an emulator-only local event receiver so Patient/Family workflow can be regression-tested without physical BLE hardware.

## Storage note

M7 behavior is implemented with Android `SQLiteOpenHelper`, not Room.
The store is bounded to the 100 newest events. This is an intentional implementation simplification, not a claim that Room was completed.

## Source-of-truth order

1. active user task
2. GitHub `main`
3. `AGENTS.md`
4. this file
5. `docs/DECISIONS.md`
6. `docs/PRODUCT_SPEC.md`
7. `docs/ARCHITECTURE.md`
8. `docs/MILESTONES.md`

## Next work

Software MVP should now receive only demonstrated fixes or explicitly requested enhancements.
Dedicated hardware research remains deferred.
