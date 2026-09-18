# Masmou Board | مسموع

**Repository:** `mrfantest2/masmou-board`  
**Local working directory:** `C:\Users\Administrator\masmou-board`  
**Current stage:** Android-first software prototype v1.0

**Current status:** Patient + Family MVP implemented. Final build/lint and emulator regression QA pass.

Masmou Board is an accessible communication-board concept for people who may have difficulty speaking, moving, or seeing. The Android prototype focuses on dignified, low-effort bedside communication for patients, families, caregivers, and clinical staff.

## v1.0 software scope

- Patient landscape communication app
- drawing canvas with colors / eraser / lock / protected clear
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- typed message -> local Android TextToSpeech
- Arabic / English with RTL/LTR
- low-vision mode, semantics and haptics
- separate Family app
- production BLE Patient -> Family event transport
- versioned shared event protocol
- bounded Family event history
- repeated-pain rule and notification
- offline-first; no backend, analytics, OCR or continuous recording
- native Windows Patient test client in `windows-patient/` for desktop interaction testing

## Verification

Final verification:

```
:core-model:test
:app-patient:assembleDebug
:app-patient:lintDebug
:app-family:assembleDebug
:app-family:lintDebug
BUILD SUCCESSFUL
```

Release QA uses `emulator-5554` per user instruction. OnePlus 7 is excluded from current/future QA runs.

See `docs/V1_IMPLEMENTATION.md` for exact verification evidence and boundaries.

## Project hub

Static project/release hub lives in `website/` and is published locally to:

`C:\xampp\htdocs\masmou-board`

## Hardware direction

The dedicated mini-tablet / custom hardware direction remains part of the roadmap, but OEM/ODM sourcing, enclosure design, BOM work, manufacturing and pilot production remain deferred until the Android UX is intentionally advanced.

## Start here

1. `AGENTS.md`
2. `docs/HANDOFF.md`
3. `docs/V1_IMPLEMENTATION.md`
4. active GitHub issue / current user request
