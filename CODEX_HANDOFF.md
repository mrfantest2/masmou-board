# CODEX HANDOFF — Masmou Board

Repository: `mrfantest2/masmou-board`

Local working directory:

```text
C:\Users\Administrator\masmou-board
```

Product working name: **Masmou Board | مسموع**

Current product stage: **Android software prototype only**

---

# 1. Mission

Build an Android-first communication board that helps people communicate when speaking, movement, vision, or medical equipment makes ordinary communication difficult.

Primary users may include:
- conscious ICU patients who cannot speak normally
- people recovering from stroke
- older adults
- people with low vision
- users with limited fine-motor control
- family members
- caregivers
- nursing staff

The product must feel calm, respectful, simple, and dignified rather than childish or gadget-heavy.

The Android app is the proof-of-concept for the future dedicated mini-tablet device. The hardware/manufacturing phase is intentionally deferred until the software interaction model is proven.

---

# 2. Scope decision: Android now, hardware later

The broader concept includes a future small purpose-built tablet, potentially around iPad Mini/A5 class size, with manufacturing/OEM/ODM research later.

For the current phase, DO NOT work on:
- custom electronics
- PCB design
- enclosure CAD
- screen sourcing
- manufacturing BOM
- OEM/ODM supplier selection
- China/Shenzhen sourcing
- Malaysia/Penang sourcing
- Vietnam/India sourcing
- travel or visa planning
- cellular modem hardware
- industrial design production files

Those topics belong to a later hardware milestone.

The immediate objective is to validate the complete communication experience on ordinary Android phones/tablets.

---

# 3. Core product principles

1. **Offline first**
   - Patient communication must work without Internet.

2. **Communication before connectivity**
   - Writing, quick actions, and speech must still work when Bluetooth is unavailable.

3. **Accessibility is not optional**
   - Large controls, TalkBack semantics, high contrast, haptics, scalable text, RTL support.

4. **Arabic and English are equal first-class languages**
   - Arabic RTL must be native and correct.
   - English LTR must be native and correct.

5. **Privacy first**
   - No continuous microphone recording.
   - No cloud account requirement.
   - No analytics SDK.
   - No advertising.
   - No remote data collection in the MVP.

6. **Minimal architecture**
   - Do not build infrastructure before a real milestone needs it.

7. **One-tap communication**
   - Common requests should require as little physical/cognitive effort as possible.

8. **Graceful degradation**
   - TTS failure must not break visual communication.
   - BLE failure must not break local communication.
   - Family app disconnect must not affect Patient app operation.

---

# 4. Applications

## 4.1 Masmou Patient

Primary app used by the patient.

Target package namespace:

```text
com.fantest.masmou.patient
```

Primary orientation:

```text
Landscape-first
```

Portrait can be supported where practical, but landscape is the design target for the prototype.

Core responsibilities:
- freehand communication canvas
- large one-touch communication buttons
- typed communication
- text-to-speech
- Arabic/English UI
- accessibility modes
- local communication event generation
- optional later BLE connection to Family app

## 4.2 Masmou Family

Separate Android app added in a later milestone.

Responsibilities:
- display Patient connection state
- receive discrete communication events
- show recent events and timestamps
- show stronger indication for urgent/pain events
- later persist bounded event history

The Family app does not control or gate the Patient app.

---

# 5. Patient main screen

Build a clean landscape-first interface with three major regions.

## A. Header/status area

Show only useful status such as:
- Masmou identity
- selected language
- accessibility mode
- optional later Family connection state

Do not clutter this area.

## B. Main communication area

Large central white canvas for writing/drawing.

Requirements:
- finger input
- stylus input
- smooth freehand strokes
- simple stroke representation
- black
- blue
- red
- green
- clear action
- lock/unlock action

Lock behavior:
- when locked, destructive clearing is prevented
- intentional unlock is required before clearing protected content

Do NOT add OCR or handwriting recognition in the first prototype.

## C. Quick actions

Large communication buttons suitable for users with limited fine motor control.

Initial actions:

```text
YES
NO
WATER
PAIN
TOILET
NURSE / HELP
FAMILY
URGENT
```

Arabic equivalents must be included through localized resources.

Each action should provide immediate feedback:
- visual state feedback
- haptic feedback where appropriate
- spoken phrase when TTS is enabled/available

The Urgent action is a communication signal only.

DO NOT automatically dial emergency services in the MVP.

---

# 6. Typed communication

Provide a simple optional text input mode.

The user should be able to:
- type a short message
- see it clearly
- tap Speak
- hear Android TextToSpeech read it

Typed messages can later become CommunicationEvents if needed.

Do not overcomplicate text editing.

---

# 7. Voice / Text-to-Speech requirements

Use Android's native `TextToSpeech` APIs first.

## Required behavior

The app must support speech for:
- quick-action phrases
- typed text

## Arabic

Preferred behavior:
- use Arabic TTS when available
- prefer a clear neutral/Modern Standard Arabic-style voice for general understanding
- do not hardcode a device-specific voice name
- query installed TTS voices/languages at runtime
- use an appropriate Arabic locale available on the device
- if a more specific Arabic locale is unavailable, fall back safely to a supported Arabic locale

The UI must clearly remain usable if Arabic TTS is unavailable.

## English

Use an available English TTS locale.

Do not hardcode a single vendor-specific English voice.

## Voice settings

For the prototype, keep settings minimal:
- speech enabled/disabled
- speech rate
- optional voice selection only if this can be implemented simply using installed Android TTS voices

Do not add cloud TTS in the MVP.

Do not require Internet for speech.

## TTS acceptance tests

Test at minimum:
- Arabic quick actions
- English quick actions
- typed Arabic
- typed English
- missing/unavailable TTS language
- TTS engine initialization failure

Expected behavior on failure:
- visual communication continues normally
- app does not crash
- user receives a clear non-blocking indication

---

# 8. Accessibility modes

## Standard mode

- large white communication surface
- clear icons
- readable labels
- large touch targets

## Low-vision mode

- larger text/icons
- high contrast
- strong control boundaries
- haptic confirmations
- spoken confirmation where useful

## Blind-user software support

For Android prototype:
- TalkBack semantics
- meaningful content descriptions
- logical focus order
- no icon-only essential control without an accessible label
- haptic feedback
- spoken UI feedback where useful

Mechanical raised/tactile buttons are a future hardware feature, not current Android work.

---

# 9. Communication event model

Create a platform-neutral domain model when the milestone requires it.

Conceptual model:

```kotlin
data class CommunicationEvent(
    val id: String,
    val type: CommunicationEventType,
    val timestampEpochMillis: Long,
    val text: String? = null,
    val urgent: Boolean = false,
    val protocolVersion: Int = 1
)
```

Suggested event types:

```text
YES
NO
WATER
PAIN
TOILET
NURSE
FAMILY
URGENT
TEXT
```

The domain model must not depend on Compose or BLE.

---

# 10. Family synchronization

Add only when the BLE milestone begins.

Preferred flow:

```text
Patient action
   -> CommunicationEvent
   -> local UI/TTS
   -> optional BLE encoder
   -> BLE
   -> Family decoder
   -> Family event UI/history
```

Requirements:
- versioned message format
- small payloads
- malformed messages rejected safely
- automatic reconnect where practical
- connection status visible
- Patient app remains fully usable during disconnect

Compact JSON with `kotlinx.serialization` is acceptable for the prototype if a serialization dependency is already justified by this milestone.

Do not build a server to solve local Bluetooth communication.

---

# 11. Event history and pain alert

Add persistence only when the dedicated milestone begins.

Use Room at that stage.

Family event history should be bounded rather than growing forever.

Initial repeated-pain rule:

```text
2 PAIN events within 60 minutes
```

When triggered, Family app may show a stronger notification/alert.

This is a communication heuristic, not diagnosis or clinical decision support.

Unit-test this rule.

---

# 12. Privacy and safety boundaries

Do not add:
- continuous audio recording
- always-on microphone
- background room recording
- patient surveillance
- medical diagnosis
- clinical recommendations
- real emergency dispatch
- cloud medical records
- Firebase analytics
- advertising SDKs

Only explicit communication events should be transmitted/persisted.

The app is a prototype communication aid, not a certified medical device.

Do not represent it as a replacement for nurses, caregivers, monitoring equipment, emergency systems, or medical judgment.

---

# 13. Visual design direction

Desired character:
- calm
- clinical-professional
- modern
- respectful
- simple
- high legibility

Suggested visual language:
- white primary canvas/surfaces
- dark navy accents
- restrained status colors where necessary

Avoid:
- rainbow-heavy styling
- childish visual language
- unnecessary gradients
- excessive animation
- small controls
- dense menus

Animation must never delay communication.

---

# 14. Android technical stack

Use:
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- Android TextToSpeech
- Android localization resources
- Android accessibility semantics
- Android haptics
- Android BLE in its milestone
- Room in its milestone

Prefer stable Android/platform APIs.

Avoid unnecessary dependencies.

Do not add:
- Firebase
- Retrofit
- cloud SDKs
- analytics SDKs
- DI framework just for architecture fashion
- navigation framework unless multiple screens actually need it
- OCR/AI libraries

A simple architecture is preferable to an elaborate clean-architecture template.

---

# 15. Repository strategy

Current repo:

```text
mrfantest2/masmou-board
```

Local path:

```text
C:\Users\Administrator\masmou-board
```

Expected structure should evolve only as needed.

Initial possible shape:

```text
masmou-board/
├── AGENTS.md
├── CODEX_HANDOFF.md
├── README.md
├── app-patient/
├── core-model/        # create only when shared domain model is needed
├── app-family/        # create only at Family milestone
├── core-ble/          # create only at BLE milestone
└── core-storage/      # create only at persistence milestone
```

Do not scaffold empty future modules just to match this diagram.

---

# 16. Milestones

Work sequentially.

## M0 — Android Foundation

GitHub issue: `#1`

Goal:
Create the minimum healthy Android project.

Scope:
- Kotlin
- Compose
- Material 3
- Gradle Kotlin DSL
- `app-patient`
- namespace `com.fantest.masmou.patient`
- app name `Masmou Patient`
- landscape-first support
- Arabic + English resource foundation
- simple launch screen
- no Internet dependency

Do NOT implement:
- real drawing
- BLE
- Family app
- Room
- cloud/backend
- accounts
- OCR
- future milestones

Acceptance:
- project syncs
- app launches
- landscape works
- Arabic resources can be selected by locale
- `assembleDebug` passes
- relevant targeted tests pass

STOP after M0.

---

## M1 — Patient Communication Shell

GitHub issue: `#2`

Build:
- header/status area
- large communication-area placeholder
- quick-action panel
- bottom tool/action area
- responsive landscape layout
- Arabic RTL
- English LTR

No real drawing yet.

---

## M2 — Writing Canvas

GitHub issue: `#3`

Build:
- Compose Canvas
- finger/stylus drawing
- black/blue/red/green
- clear
- lock/unlock
- destructive-clear protection

No OCR.

---

## M3 — Quick Actions + TTS

GitHub issue: `#4`

Build:
- Yes
- No
- Water
- Pain
- Toilet
- Nurse
- Family
- Urgent
- Arabic/English labels
- Android TTS
- haptic/visual feedback

---

## M4 — Accessibility

GitHub issue: `#5`

Build/test:
- standard mode
- low-vision mode
- TalkBack
- high contrast
- larger text/touch targets
- haptics
- logical focus order

---

## M5 — Family App Foundation

GitHub issue: `#6`

Build:
- separate `Masmou Family` Android app
- connection placeholder
- recent-event UI
- shared CommunicationEvent model

No BLE transport yet.

---

## M6 — BLE Communication

GitHub issue: `#7`

Build:
- Patient -> Family BLE transfer
- versioned encoding
- connection state
- reconnect behavior
- malformed-message rejection

Test on two real Android devices.

---

## M7 — Event History + Pain Alert

GitHub issue: `#8`

Build:
- Room persistence
- bounded event history
- timestamps
- repeated-pain rule
- Family notification behavior

---

## M8 — Kiosk / Reliability Hardening

GitHub issue: `#9`

Build/test:
- full-screen/kiosk-friendly use
- accidental-exit reduction
- lifecycle recovery
- state restoration
- BLE recovery
- accessibility regression

---

## M9 — Real Device QA

GitHub issue: `#10`

Test:
- tablet
- phone
- stylus if available
- Arabic
- English
- TalkBack
- TTS Arabic/English
- BLE distance/reconnect
- long-running sessions

Fix demonstrated issues only.

---

## M10 — Hardware research (DEFERRED)

GitHub issue: `#11`

Do not start until software interaction is validated.

Future scope can include:
- dedicated mini-tablet form factor
- display
- enclosure
- stylus retention
- tactile controls
- battery
- charging
- Bluetooth/cellular variants
- bedside mounting
- weight/thickness targets
- manufacturing BOM
- OEM/ODM research

---

# 17. Codex usage efficiency rules

This project is intentionally optimized to reduce Codex usage.

Codex must:
- work one issue at a time
- read `AGENTS.md`
- read this file
- read the active issue
- avoid scanning unrelated issues/files
- avoid broad planning when requirements already exist here
- avoid rewriting working code
- avoid implementing future milestones
- use targeted compile/tests while iterating
- run broader milestone verification once at completion
- stop immediately when acceptance criteria are met

Do not repeatedly explain the product back to the user.

Do not ask for approval between safe implementation steps inside an active milestone.

If a safe build/compiler/lint failure is directly caused by the current change, fix it autonomously.

Ask the user only when a genuine external decision or physical-device interaction is required.

---

# 18. Build verification policy

During implementation:
- compile the relevant module
- run targeted unit tests where useful

At milestone completion:
- run relevant unit tests
- run `lintDebug` where configured
- run `assembleDebug`

Do not claim success without real build output.

If Android SDK/Java/Gradle configuration prevents execution, report the exact blocker.

Do not convert an unverified build into a success claim.

---

# 19. Git workflow

Prefer one branch per milestone, for example:

```text
codex/m0-foundation
codex/m1-patient-shell
codex/m2-writing-canvas
codex/m3-quick-actions-tts
codex/m4-accessibility
codex/m5-family-foundation
codex/m6-ble
codex/m7-events-alerts
codex/m8-hardening
```

Keep commits focused.

Do not create large unrelated refactors inside milestone branches.

---

# 20. First Codex execution instruction

Use this exact task for the first implementation run:

```text
Work in C:\Users\Administrator\masmou-board.

Read AGENTS.md and CODEX_HANDOFF.md, then implement GitHub Issue #1 (M0 — Android Foundation) only.

Do not implement M1 or later.

Create the minimum healthy Kotlin + Jetpack Compose + Material 3 Android project with Gradle Kotlin DSL for app-patient, namespace com.fantest.masmou.patient, app name Masmou Patient, landscape-first support, Arabic and English resource foundations, a simple launch screen, and no Internet dependency.

Prefer stable compatible Android/Kotlin/Compose versions already supported by the local environment. Do not add unnecessary dependencies.

Verify with targeted checks and assembleDebug. Fix safe implementation/build failures autonomously.

Stop immediately once M0 acceptance criteria pass.

Final output only:
- status
- changed files
- exact verification commands/results
- blocker if any
```

---

# 21. Definition of success for Android prototype

The Android prototype is successful when a real user can:

1. launch Masmou Patient quickly
2. understand the interface with minimal instruction
3. draw/write using finger or stylus
4. request common needs with one large tap
5. hear selected phrases through local Android TTS
6. type and speak short messages
7. use Arabic correctly in RTL
8. use English correctly in LTR
9. use high-contrast/low-vision mode
10. navigate essential controls with TalkBack
11. continue communicating completely offline
12. later transmit discrete communication events to nearby Masmou Family over BLE
13. continue local communication even if Family/BLE disconnects

Only after these interactions are validated should the project move toward a dedicated physical mini-tablet and manufacturing research.

---

# 22. Current instruction to Codex

**CURRENT ACTIVE WORK: M0 / GitHub Issue #1 only.**

Do not start hardware work.
Do not start manufacturing research.
Do not start generic smart-home/POS/education tablet variants.
Do not start Family/BLE before their milestones.

Build the Android communication product first.
