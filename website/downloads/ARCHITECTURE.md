# Masmou Board — Architecture

## Principle

Offline-first Android prototype with separate Patient and Family applications and a minimal shared domain module.

## Modules

### app-patient
- Kotlin + Jetpack Compose + Material 3
- communication UI
- Compose Canvas drawing
- quick actions
- typed communication
- local Android TextToSpeech
- Arabic / English mode
- accessibility / low vision
- BLE GATT server + advertising

### app-family
- Kotlin + Jetpack Compose + Material 3
- BLE scan/connect/reconnect
- receive/decode events
- bounded local event history
- repeated-pain alert UI
- Android notification channel

### core-model
- `CommunicationEvent`
- `EventType`
- versioned protocol encoder/decoder
- BLE UUID contract
- repeated-pain rule
- unit tests

## Drawing

Compose Canvas / pointer input. V1 strokes are in memory. No OCR.

## TextToSpeech

Android `TextToSpeech`; local communication continues even when TTS is unavailable.

## BLE flow

Patient action -> `CommunicationEvent` -> protocol encoder -> BLE GATT notification -> Family decoder -> local store -> UI/alert.

Patient local communication does not depend on BLE connectivity.

Debug builds contain an emulator-only local event receiver so Patient/Family behavior can be regression-tested without physical BLE. Production behavior retains the BLE transport.

## Persistence

Family uses Android `SQLiteOpenHelper` with a 100-event bound. This replaces the earlier planned Room implementation for the current MVP.

Stored fields:
- id
- type
- timestamp
- optional text
- urgency
- protocol version

## Networking

No backend, Firebase, analytics or Internet dependency in the MVP.

## Localization

- English + Arabic
- live language switch
- RTL/LTR via Compose layout direction

## Accessibility

- large touch targets
- semantics
- low-vision mode
- haptics
- high contrast

## Verification

Milestone/release gates:
- `:core-model:test`
- `:app-patient:assembleDebug`
- `:app-patient:lintDebug`
- `:app-family:assembleDebug`
- `:app-family:lintDebug`

Current release QA uses Android emulator `emulator-5554`.
