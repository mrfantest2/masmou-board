# Masmou Board — Product Specification

Version: 0.2
Stage: Android-first prototype

## Problem

The original concept addresses communication barriers in care settings, including:
- stroke patients with speech difficulty despite awareness
- ICU patients unable to express themselves because of ventilation
- older adults / people with low vision who struggle with complex smartphones
- frustration and isolation when basic needs cannot be communicated
- family anxiety when they do not know what the patient needs

## Product goal

Provide a dignified, simple, clear communication experience.

## Patient app requirements

### Core
- landscape-first
- Arabic RTL
- English LTR
- large central white communication area
- large quick actions
- local/offline operation

### Canvas
- finger input
- stylus input
- black / blue / red / green
- eraser
- clear
- lock/unlock protection
- no OCR in V1

### Quick actions
- Yes
- No
- Water
- Pain
- Toilet
- Nurse / Help
- Family
- Urgent

### Typed communication
- Arabic or English text
- Speak
- clear typed text

### Speech
- Android TextToSpeech
- Arabic + English
- runtime voice/language availability checks
- speech-rate control
- non-blocking fallback
- no cloud requirement
- no continuous audio recording

## Accessibility

### Standard visual
- high readability
- large controls

### Low vision
- larger text/icons
- stronger contrast
- haptics
- spoken confirmation

### Blind-user software support
- TalkBack semantics
- content descriptions
- logical focus
- haptics
- spoken feedback

Mechanical tactile buttons are future hardware work.

## Family app — later milestone

- local patient profile
- connection state
- recent communication event history
- timestamps
- Pain/Urgent highlighting
- incoming typed messages
- pairing model
- repeated-pain alerts

The original source mentions a primary owner and invited family members. Preserve this product requirement, but do not add cloud authentication in the MVP without a separate decision.

## BLE — later milestone

- Patient -> Family
- versioned messages
- reconnect
- malformed message handling
- visible status
- local Patient features always work without BLE

## Event log

Store discrete deliberate events, not continuous audio.

## Pain repeat rule

Initial prototype:
- 2 Pain events in 60 minutes -> stronger Family alert

Not a diagnosis.

## Privacy

- offline/local first
- no ads
- no analytics SDK
- no continuous mic recording
- no unnecessary cloud data

## Visual identity

- white
- dark navy
- professional
- calm
- not childish
- strong readability

## Non-goals for Android V1

- custom hardware
- factory integration
- firmware
- cloud backend
- Firebase
- HIS/EMR integration
- OCR
- AI chatbot
- real emergency-service calling
- continuous monitoring
