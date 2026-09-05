# Masmou Board — General Chat Handoff

Use this document when continuing the project in ChatGPT or another assistant
that does not have Codex workspace access.

## What the project is

Masmou Board is an Android-first, offline-first communication-board prototype.
The Patient app is intended to help a person communicate visually, by writing,
through quick needs, and eventually with local text-to-speech. Arabic RTL and
English LTR are first-class requirements.

Repository: https://github.com/mrfantest2/masmou-board

Local Android project: `C:\Users\Administrator\masmou-board`

Local project website: `C:\xampp\htdocs\masmou-board`

Website URL while XAMPP Apache is running:
http://localhost/masmou-board/

## Current verified status

M0 — Android Foundation is complete.

- GitHub Issue #1 is closed.
- Pull request #15 is merged:
  https://github.com/mrfantest2/masmou-board/pull/15
- Implementation commit:
  `8a50de9a6262423fa972276b45b7c41cfb358f22`
- Merge commit:
  `a8810eb2cd8c88486fd31c22e9ff3ae0dd2665f9`
- `main` matches `origin/main` at the merge commit.

The M0 app uses Kotlin, Jetpack Compose, Material 3, and Gradle Kotlin DSL.
Its module is `app-patient`, namespace/application ID is
`com.fantest.masmou.patient`, and its visible name is `Masmou Patient`.

## What the current APK does

The APK is a foundation build, not yet a communication board. It:

- opens a calm welcome screen;
- requests landscape orientation;
- chooses Arabic or English through Android locale resources;
- supports RTL layout for Arabic;
- includes basic accessibility heading semantics;
- has no Internet permission and works locally.

It does not yet contain the communication shell, drawing, quick actions,
text-to-speech, Bluetooth, Family app, event history, alerts, accounts,
backend, OCR, emergency calling, or hardware features.

## Verification already performed

- Kotlin application and instrumentation-test compilation passed.
- `assembleDebug` passed.
- `lintDebug` passed with 0 errors and 8 documented advisories.
- Three connected tests passed on a GM1903 running Android 12 / API 31.
- APK installation and cold launch passed.
- The packaged APK was inspected and has no
  `android.permission.INTERNET` permission.
- The launch screen was visually inspected in landscape.

The advisories are five dependency-update notices, one target API 35 notice,
one warning caused by the required landscape orientation, and one backup-rule
notice. Details are in `docs/M0_IMPLEMENTATION.md`.

## Important local-file situation

The repository was initialized around an existing folder. Several original
handoff, documentation, hardware, and PDF files intentionally remain as local
modified or untracked files. They belong to the user and must not be deleted,
reset, overwritten, cleaned, or accidentally committed with unrelated work.

Never use destructive commands such as:

```text
git reset --hard
git clean -fd
git checkout -- .
```

Before any future implementation, inspect `git status` and preserve all
existing local files. The previous workflow verified that all 18 original
files remained byte-for-byte intact during M0.

## Source-of-truth reading order

For implementation work, read:

1. `AGENTS.md`
2. `docs/HANDOFF.md`
3. the active GitHub issue or explicit user request
4. `docs/GITHUB_WORKFLOW.md`
5. only the relevant milestone section in `docs/MILESTONES.md`

Read the other product documents only when the active task needs them. Do not
read the source PDF unless the user explicitly asks to verify the original
concept.

## Required development workflow

- Work on exactly one requested issue or milestone.
- Do not begin M1 or any later milestone without an explicit request.
- One issue equals one branch and one pull request.
- Synchronize `main` before creating the exact milestone branch.
- Never discard unrelated user changes.
- Implement the smallest correct scope.
- Run targeted checks first, then relevant tests, `lintDebug`, and
  `assembleDebug` at completion.
- Commit and push only files belonging to the active issue.
- Open a pull request if GitHub tooling is already authenticated.
- Put `Closes #<issue>` in the pull-request body.
- Do not merge during the same implementation run unless the user separately
  and explicitly approves the merge.
- Stop after the pull request is created.

## Product invariants

- Arabic RTL and English LTR are first-class.
- Core Patient communication works without Internet.
- Bluetooth failure never blocks local Patient communication.
- Text-to-speech failure never blocks visual communication.
- No continuous microphone recording.
- No cloud backend, advertising, or analytics SDK in the prototype.
- Large touch targets and accessibility are mandatory.
- Do not add OCR or handwriting AI in V1.
- Do not implement real emergency-service dialing in V1.
- Do not begin hardware or manufacturing work during Android milestones.

## Roadmap boundary

M1 is the next planned milestone: Patient Communication Shell. Its documented
scope is a header/status area, central canvas placeholder, large quick-action
area, bottom tool area, RTL/LTR behavior, and responsive landscape layout. It
contains no real drawing yet.

M1 is only background context. Do not implement it until the user explicitly
requests GitHub Issue #2 or otherwise clearly authorizes M1.

## APK and website handoff

Verified APK in the Android project:
`app-patient\build\outputs\apk\debug\app-patient-debug.apk`

Website copy:
`C:\xampp\htdocs\masmou-board\downloads\masmou-patient-m0-debug.apk`

APK SHA-256:
`4a2b821ddfe3db5323c025a294a66f72acd4be961e02ac19cab70360167c6847`

The website is a static local status and download hub. It includes M0 status,
M0–M10 roadmap entries, verification results, the APK, source documentation,
and a handoff ZIP. It has no analytics or backend.

## Suggested opening message for another assistant

Copy this after providing the assistant with this handoff and any files it
needs:

> Continue the Masmou Board project using the attached handoff as context.
> First tell me what the current M0 app does and what remains planned. Do not
> modify files, start M1, create branches, or change GitHub until I explicitly
> request one specific issue. Preserve all existing local documentation and
> PDF files.
