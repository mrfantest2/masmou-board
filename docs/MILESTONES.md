# Masmou Board — Engineering Milestones

Status snapshot: 18 September 2026

## M0 — Android Foundation
Status: Complete / merged.
- Kotlin, Compose, Material 3, Gradle Kotlin DSL
- Patient package + landscape base
- Arabic / English resources

## M1 — Patient Communication Shell
Status: Complete / merged.
- responsive landscape shell
- header/status, communication region, quick actions, tools
- RTL/LTR

## M2 — Writing Canvas
Status: Complete.
- finger/stylus drawing
- black / blue / red / green
- eraser
- lock/unlock
- two-step clear protection

## M3 — Quick Actions + TTS
Status: Complete.
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- local Android TextToSpeech
- typed text -> Speak
- speech-rate control
- Arabic / English phrases

## M4 — Accessibility
Status: Complete for MVP.
- low-vision mode
- large touch targets
- high contrast
- Compose semantics
- haptics

## M5 — Family App Foundation
Status: Complete.
- separate Family app
- shared event model
- connection state
- event history UI

## M6 — BLE Communication
Status: Implemented.
- Patient BLE advertising + GATT server
- Family scan/connect/reconnect path
- versioned messages
- malformed-message rejection
- Patient local communication remains independent of BLE

Release validation after the user's latest instruction is emulator-only. Production BLE code compiles and lints, but current release QA does not claim a fresh physical BLE verification.

## M7 — Event History + Pain Alert
Status: Complete behavior with implementation deviation.
- bounded 100-event local history
- timestamps
- repeated-pain rule: 2 Pain events within 60 minutes
- Family UI alert
- Android notification

Deviation: persistence uses Android `SQLiteOpenHelper` instead of Room.

## M8 — Reliability / Bedside Hardening
Status: Complete for MVP.
- keep-awake
- immersive UI
- canvas lock
- protected clear
- Family lifecycle refresh
- event persistence
- BLE reconnect path

## M9 — QA
Status: Emulator regression complete.

Verified on `emulator-5554`:
- Patient landscape launch
- Patient -> Family debug-only emulator event flow
- two Pain events + repeated-pain alert
- Android notification
- Family restart persistence
- Arabic / RTL
- low-vision mode
- clear confirmation
- lock behavior

Per user instruction, OnePlus 7 is excluded from current/future QA runs.

## M10 — Hardware Prototype Research
Status: Deferred.

Preserved targets:
- A5-class device
- ~250 g target
- ~7–9 mm target
- easy-grip retained stylus
- magnetic/bedside placement
- tactile controls
- speaker/haptics
- BLE
- optional later cellular version
- cleaning/durability
- BOM / OEM / ODM / manufacturing
