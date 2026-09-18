# Masmou Board — Accepted Decisions

## D001 — Android first
Accepted.

Current implementation is Android software before dedicated hardware.

## D002 — Offline-first Patient experience
Accepted.

Core Patient communication must work without Internet.

## D003 — No OCR in V1
Accepted.

Freehand writing is visual communication. Speech uses quick actions and typed text.

## D004 — Local Android TextToSpeech
Accepted.

Do not require cloud TTS.

## D005 — No continuous audio recording
Accepted.

Store discrete communication events instead.

## D006 — Separate Patient and Family apps
Accepted.

Family is a later companion app.

## D007 — BLE optional to Patient core
Accepted.

Disconnection never stops local communication.

## D008 — Source family owner/invite model preserved
Accepted as a product requirement.

Implementation starts local/offline; cloud account infrastructure is deferred pending a separate decision.

## D009 — No real emergency dialing in V1
Accepted.

Urgent communicates urgency only.

## D010 — Mechanical accessibility is hardware-stage work
Accepted.

Android uses TalkBack, haptics, larger UI, contrast, and speech.

## D011 — No cloud platform in MVP
Accepted.

No Firebase/backend/accounts unless explicitly approved later.

## D012 — Hardware and manufacturing deferred
Accepted.

A5/250g/7–9mm/stylus/magnetic concepts are archived targets only.

## D013 — One milestone per Codex run
Superseded for the v1 completion run by explicit user authorization to finish the project end-to-end.

## D014 — Emulator-first release QA
Accepted.

Current release regression testing uses Android emulator `emulator-5554`. OnePlus 7 is excluded from current and future QA runs per user instruction.

## D015 — Minimal local Family storage
Accepted.

Use bounded `SQLiteOpenHelper` persistence for the current MVP instead of adding Room solely to satisfy the earlier implementation preference.
