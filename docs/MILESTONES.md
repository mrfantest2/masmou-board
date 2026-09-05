# Masmou Board — Engineering Milestones

## M0 — Android Foundation

Scope:
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- `app-patient`
- `com.fantest.masmou.patient`
- app name `Masmou Patient`
- landscape-first base
- Arabic + English resources
- simple professional launch screen
- no Internet dependency

Do not implement:
- drawing
- TTS feature
- BLE
- Family app
- Room
- networking
- hardware

Acceptance:
- project config is healthy
- app launches
- landscape works
- localization foundation exists
- `assembleDebug` passes
- `lintDebug` is clean or any blocker is explicitly documented

GitHub issue:
`#1`

Required branch:
`codex/m0-foundation`

Completion workflow:
- verify M0
- commit
- push branch
- open PR titled `M0 — Android Foundation`
- include `Closes #1` in PR body
- stop without merging or starting M1

---

## M1 — Patient Communication Shell

Scope:
- header/status
- central canvas placeholder
- large quick-action area
- bottom tool area
- RTL/LTR
- responsive landscape

No real drawing yet.

---

## M2 — Writing Canvas

Scope:
- finger/stylus drawing
- black/blue/red/green
- eraser
- clear
- lock/unlock
- accidental-clear protection

No OCR.

---

## M3 — Quick Actions + TTS

Scope:
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- local Android TextToSpeech
- Arabic/English phrases
- typed text -> Speak
- speech-rate control
- missing voice fallback

---

## M4 — Accessibility

Scope:
- standard mode
- low-vision mode
- TalkBack semantics
- larger UI
- strong contrast
- haptics
- focus order

---

## M5 — Family App Foundation

Scope:
- separate Android app
- local patient/family profile foundation
- connection placeholder
- recent events UI
- shared event model

No BLE transport yet.

---

## M6 — BLE Communication

Scope:
- Patient -> Family BLE/local Bluetooth
- connection state
- versioned messages
- reconnect
- malformed-message rejection
- typed message/event synchronization

Acceptance must include real-device testing.

---

## M7 — Event History + Pain Alert

Scope:
- Room
- bounded event history
- timestamps
- repeated-pain rule
- Family notification behavior

Initial rule:
2 Pain events within 60 minutes.

---

## M8 — Reliability / Bedside Hardening

Scope:
- full-screen/kiosk-friendly behavior
- accidental exit reduction
- lifecycle recovery
- state restoration
- BLE recovery
- accessibility regression checks

---

## M9 — Real Device QA

Test:
- tablet
- phone
- stylus if available
- Arabic
- English
- TalkBack
- TTS Arabic/English
- BLE range/reconnect
- long-session stability

Fix demonstrated issues only.

---

## M10 — Hardware Prototype Research

Begin only after Android UX is validated.

Preserved source targets:
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
- BOM
- OEM/ODM
- manufacturing
