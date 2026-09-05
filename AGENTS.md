# AGENTS.md

## Project
Masmou Board is an Android-first, offline-first communication app for patients and caregivers.

## Working directory
`C:\Users\Administrator\masmou-board`

## Read order
1. `AGENTS.md`
2. `CODEX_HANDOFF.md`
3. Active GitHub issue only

Do not read the entire repository or all GitHub issues unless the active task requires it.

## Codex usage discipline
- Work on one milestone/issue at a time.
- Prefer the smallest correct change.
- Do not implement future milestones early.
- Do not perform broad refactors without need.
- Do not add dependencies unless a current requirement needs them.
- Use targeted tests during implementation.
- Run broader verification only at milestone completion.
- Stop when the active milestone acceptance criteria are met.
- Final response must be short: status, changed files, verification, blocker if any.

## Android stack
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- Android TextToSpeech
- Room only when persistence is introduced
- Android BLE only when the BLE milestone begins

## Product rules
- Arabic RTL and English LTR are first-class.
- Core patient communication must work without Internet.
- Bluetooth/family connectivity must never be required for local communication.
- No continuous microphone/audio recording.
- No AI handwriting recognition/OCR in the first prototype.
- No cloud backend, accounts, analytics SDKs, or Firebase in the first prototype.
- No real emergency-service dialing in the first prototype.
- Accessibility is mandatory.
- Large touch targets are mandatory.

## Current scope
Build the Android software prototype only.

Do not work on:
- custom tablet hardware
- OEM/ODM manufacturing
- China/Malaysia/Vietnam/India sourcing
- travel/visa planning
- cellular hardware
- industrial design

Those topics are deferred until the Android interaction model is validated.
