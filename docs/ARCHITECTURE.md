# Masmou Board — Architecture

## Principle

Build vertically and add architecture only when a real milestone needs it.

## Initial Android structure

Start with:

- `app-patient`

Add later only when justified:

- `app-family`
- `core-model`
- `core-ui`
- `core-ble`
- `core-storage`

Do not create empty future modules solely for theoretical cleanliness.

## Patient app

Use:
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL

Responsibilities:
- communication UI
- drawing
- quick actions
- typed communication
- local TTS
- language
- accessibility
- local state

## Drawing

Use Compose Canvas / pointer input.

V1:
- in-memory strokes
- finger/stylus
- color
- eraser
- clear
- lock

No OCR.

## TextToSpeech

Use Android `TextToSpeech`.

Behavior:
- initialize asynchronously
- detect language support
- select appropriate installed locale/voice
- expose ready/unavailable state
- queue/stop speech safely
- allow speech-rate control

TTS failure does not block visual communication.

## Domain model

Introduce a UI-independent event model when needed.

`CommunicationEvent`
- id
- type
- timestamp
- text?
- urgency
- protocolVersion

## Family app

Separate application introduced later.

Responsibilities:
- BLE connection state
- receive events
- event history
- alert UI

## BLE

Versioned small messages.

Preferred flow:

Patient domain event
-> encoder
-> BLE transport
-> decoder
-> Family domain event/store/UI

Keep BLE separate from Patient-local core functionality.

## Persistence

Do not add Room before M7.

When added:
- bounded event history
- minimal required fields
- no unnecessary patient medical record

## Networking

No backend in MVP.
No Firebase.
No analytics.
Core Patient app should not require Internet.

## Localization

- `values/`
- `values-ar/`
- proper RTL/LTR behavior
- avoid manually mirrored hardcoded layout

## Accessibility

- semantics
- TalkBack labels
- focus order
- scalable text
- large touch targets
- haptics

## Verification

During coding:
- targeted checks

Milestone completion:
- relevant unit tests
- `lintDebug`
- `assembleDebug`

Do not repeatedly run the full suite for trivial changes.
