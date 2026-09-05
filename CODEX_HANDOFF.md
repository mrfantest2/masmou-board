# FULL CODEX HANDOFF — Masmou Board | مسموع

Repository: `mrfantest2/masmou-board`
Local working directory: `C:\Users\Administrator\masmou-board`

## 1. Mission

Build a dignified communication tool for people who may have difficulty speaking, moving, or seeing, especially in patient/care settings.

The original concept specifically identifies:
- stroke patients who may have difficulty speaking despite awareness
- ICU patients who may be unable to express themselves because of ventilation
- older adults and people with low vision who may struggle with complex smartphones
- families who remain anxious when they cannot understand what the patient needs

The Android application is the **current prototype vehicle**. The dedicated physical mini-tablet remains a later product stage.

## 2. Current scope decision

### Build now

Android software, in this order:

1. Patient app foundation
2. Patient communication UI
3. freehand writing/drawing
4. quick communication actions
5. local speech output
6. accessibility modes
7. Family app foundation
8. secure local Bluetooth/BLE event synchronization
9. event history and repeated-pain alerts
10. reliability / bedside-style hardening
11. real-device QA

### Defer

Do not build yet:

- custom PCB
- custom enclosure
- firmware
- factory/OEM/ODM integration
- manufacturing BOM
- cellular hardware
- hospital HIS/EMR integration
- cloud backend
- Firebase
- advertising
- analytics
- continuous microphone recording
- handwriting OCR/AI
- real emergency-service dialing

## 3. Patient Android app

App name: `Masmou Patient`
Namespace target: `com.fantest.masmou.patient`

### Main layout

Landscape-first, reflecting the source concept's wide central writing area with large communication controls around it.

Primary areas:

- header/status area
- large central writing/communication canvas
- quick communication actions
- bottom drawing/tool controls
- prominent Speak action
- visible connection status later when Family/BLE exists

### Freehand writing

The source concept calls for a large white writing area and clear writing colors.

Implement:

- finger input
- stylus input
- black
- blue
- red
- green
- eraser
- clear all
- lock/unlock protection

No handwriting recognition in V1.

### Quick communication actions

The source explicitly includes large one-touch symbols for basic needs such as:

- Water
- Pain
- Toilet
- Nurse

The visual mockup also contains direct yes/no-style controls and family/urgent-style actions.

For the Android prototype use:

- Yes
- No
- Water
- Pain
- Toilet
- Nurse / Help
- Family
- Urgent

The Urgent action is communication only. It does not automatically dial emergency services.

### Typed communication

Add typed text communication for users who can type.

- Arabic or English text input
- Speak button
- clear typed text
- local speech output

## 4. Voice / TextToSpeech

The source concept requires converting written/selected communication into audible speech.

Use Android `TextToSpeech`.

Requirements:

- local/offline-capable behavior where the installed engine supports it
- Arabic quick-action phrases
- English quick-action phrases
- typed Arabic -> Speak
- typed English -> Speak
- speech-rate control
- detect language/voice availability
- non-blocking status if a voice is unavailable
- do not hardcode one Samsung/Google-specific voice
- TTS failure must not block the rest of the app

Arabic:
- prefer a clear installed Arabic voice appropriate for Modern Standard Arabic/general Arabic communication
- use locale capability checks at runtime

English:
- use an installed compatible English voice

No continuous audio capture or microphone recording.

## 5. Accessibility

The source describes three user groups/modes:

### Standard visual mode
- high-contrast white writing area
- clear colored symbols
- normal visual/manual control

### Low-vision mode
- larger symbols
- stronger contrast
- stronger button boundaries
- spoken confirmation
- haptic confirmation

### Blind-user support
The future hardware concept includes raised mechanical/tactile controls.

For Android V1, implement the software equivalent:

- TalkBack semantics
- meaningful content descriptions
- logical focus order
- haptic feedback
- spoken feedback where useful
- large target areas

Mechanical raised buttons remain hardware-stage work.

## 6. Family Android app

The original concept includes a Family app with:

- communication event log
- repeated-pain alerts
- family-member access
- immediate synchronization of written messages / selected symbols
- Bluetooth connection

For the Android prototype:

### Initial Family app
- local patient profile/name
- connection status
- recent communication event list
- timestamps
- Pain/Urgent highlighting
- display incoming typed messages where applicable

### Family ownership/invite concept
Preserve the source requirement for an owner/family model, but do not force a cloud-account system into the MVP.

Start with a local/offline profile and pairing model. A later cloud account/invite mechanism requires an explicit new product decision.

## 7. Communication events

Represent explicit patient actions as structured events.

Suggested domain model:

`CommunicationEvent`
- `id`
- `type`
- `timestamp`
- `text?`
- `urgency`
- `protocolVersion`

Initial types:

- YES
- NO
- WATER
- PAIN
- TOILET
- NURSE
- FAMILY
- URGENT
- TEXT

The event model must not depend on Compose or BLE.

## 8. Event history and privacy

The source explicitly prefers timestamped request events instead of continuous audio recording.

Persist only deliberate communication events.

Do not continuously record audio.

For the Family app:
- bounded event history
- event timestamps
- optional text payload for typed messages
- clear visual urgency state

## 9. Repeated-pain alert

The source gives the example:

- Pain requested twice within one hour -> family notification / stronger attention

Implement the initial prototype rule as:

`2 PAIN events within 60 minutes`

This is a communication alert, not medical diagnosis or automated clinical escalation.

## 10. Bluetooth/BLE

The source states that family phones should receive selected/written communication through secure Bluetooth.

Prototype architecture:

Patient app
-> `CommunicationEvent`
-> versioned encoder
-> BLE/local Bluetooth transport
-> decoder
-> Family app

Requirements:

- visible connection state
- reconnect where practical
- malformed data rejected safely
- small versioned messages
- local Patient communication remains fully usable when disconnected
- no Internet required for core Patient-to-Family communication

Do not over-engineer cryptography in the first build, but do not transmit personally identifying data beyond what the prototype actually needs.

## 11. Visual identity

The source calls for a calm medical visual identity:

- white
- dark navy
- professional
- not rainbow
- not childish
- large clear controls
- strong readability

Use this same tone in the Android UI.

## 12. Physical product targets — future only

Archive these targets from the source for later hardware research:

- approximately A5-class size
- around 250 g target
- around 7–9 mm class thickness
- bedside placement
- easy transport
- thick/easy-grip stylus
- stylus tether/retention
- magnetic attachment concept
- calm white/navy industrial design

These are concept targets, not validated manufacturing specifications.

## 13. Source pricing concept — archive only

The source document provides initial hardware pricing ranges for:

- simple LCD version
- smart/hybrid Bluetooth + Family app + voice version
- advanced ICU/cellular version

Do not use these ranges as current Android engineering requirements or verified manufacturing quotations.

## 14. Source rollout plan vs current Android decision

Original source plan:
1. test simple LCD board
2. smart Bluetooth prototype
3. evaluate with speech/nursing specialists
4. pilot production of roughly 50–100 units

Current user decision:
**build the Android app first.**

Therefore the software roadmap is the active engineering plan, while the source hardware rollout remains preserved for later.

## 15. Repository structure

Keep documentation compact.

```text
masmou-board/
├── AGENTS.md
├── CODEX_HANDOFF.md
├── FIRST_CODEX_PROMPT.txt
├── README.md
├── docs/
│   ├── HANDOFF.md
│   ├── PRODUCT_SPEC.md
│   ├── ARCHITECTURE.md
│   ├── DECISIONS.md
│   ├── MILESTONES.md
│   ├── ROADMAP.md
│   ├── SOURCE_NOTES.md
│   └── source/
│       └── original_concept_ar.pdf
└── hardware/
    └── README.md
```

Create Android source modules only when a real milestone requires them.

Do not build a large empty modular architecture in advance.

## 16. Codex usage-efficiency rules

To minimize usage:

- one milestone per Codex run
- read `AGENTS.md`, `docs/HANDOFF.md`, and active issue first
- do not read all docs by default
- do not read the PDF unless explicitly verifying the source
- do not re-plan the whole product
- use targeted tests during implementation
- run full milestone checks only at completion
- avoid speculative refactors
- avoid multiple agents for routine work
- prefer stable platform APIs
- stop as soon as acceptance criteria pass

## 17. Current status and next task boundary

**M0 — Android Foundation is complete and merged.**

- Issue #1: closed
- PR #15: merged
- implementation commit: `8a50de9a6262423fa972276b45b7c41cfb358f22`
- merge commit: `a8810eb2cd8c88486fd31c22e9ff3ae0dd2665f9`
- build and lint passed
- 3 connected device tests passed on Android 12 / API 31

No implementation milestone is currently active. Do not start M1 or later
until the user explicitly requests the matching issue.

The completed M0 foundation contains:

Requirements:

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- application module `app-patient`
- namespace `com.fantest.masmou.patient`
- app name `Masmou Patient`
- landscape-first base
- Arabic + English resource foundation
- simple professional launch screen
- no Internet dependency

M0 intentionally does not implement:
- canvas
- TTS feature
- BLE
- Family app
- Room
- cloud
- accounts
- OCR
- emergency dialing
- hardware
- later milestones

Completed verification:

- Gradle project setup and Kotlin compilation passed
- `assembleDebug` passed
- `lintDebug` passed with 0 errors and 8 documented advisories
- 3 connected foundation tests passed
- APK installation and cold launch passed
- APK has no Internet permission

The next assistant must run fresh verification for any future change rather
than relying on M0 results.


# 18. GitHub milestone workflow

For every implementation milestone:

1. use the matching GitHub issue
2. branch from current `main`
3. implement only that milestone
4. run verification
5. commit
6. push the milestone branch
7. open a PR to `main`
8. include `Closes #<issue>` in the PR body
9. stop

Do not auto-merge.
Do not start the next milestone.

Read `docs/GITHUB_WORKFLOW.md` for exact branch names and PR requirements.

Completed M0 record:
- Issue: `#1` — closed
- Branch: `codex/m0-foundation`
- PR: `#15 — M0 Android Foundation` — merged


# 18. GitHub implementation workflow

All engineering milestones use:

```text
one GitHub issue
    ↓
one codex/* branch
    ↓
implementation
    ↓
verification
    ↓
one focused commit
    ↓
push
    ↓
one pull request
    ↓
STOP
```

Detailed rules are in:

`docs/GITHUB_WORKFLOW.md`

Important constraints:

- Never discard unrelated local changes.
- Do not use destructive Git cleanup commands.
- Do not install or authenticate GitHub CLI merely to create a PR.
- If `gh` is already available and authenticated, create the PR.
- If `gh` is unavailable but push works, stop after push and report branch + commit SHA.
- Do not merge the PR during the implementation run.
- Use `Closes #<issue>` in the PR body.
- Start the next milestone only after the prior PR is merged and `main` is synchronized.
