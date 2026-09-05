# AGENTS.md

## Project

Masmou Board is an Android-first, offline-first communication-board prototype.

Repository: `mrfantest2/masmou-board`
Local path: `C:\Users\Administrator\masmou-board`

## Context-efficiency rule

Read in this order:

1. `AGENTS.md`
2. `docs/HANDOFF.md`
3. active issue / current user task

Read `docs/PRODUCT_SPEC.md`, `docs/ARCHITECTURE.md`, `docs/DECISIONS.md`,
`docs/MILESTONES.md`, or `docs/SOURCE_NOTES.md` only when the active task needs them.

Do not read the source PDF unless explicitly asked to verify the original concept.

## Execution rules

- Work on exactly one requested milestone or issue.
- Do not implement future milestones.
- Prefer the smallest correct change.
- Inspect only files relevant to the current task.
- Avoid broad repository scans unless required.
- Reuse working components.
- Do not refactor unrelated working code.
- Do not add dependencies without a present requirement.
- Do not invent product scope.
- Fix safe build/compiler/lint issues autonomously.
- Stop when acceptance criteria pass.
- Do not re-plan the full product on every run.

## Android stack

Prefer:

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- Android TextToSpeech
- Android accessibility APIs
- Android BLE when the BLE milestone starts
- Room only when persistent event history starts

Prefer stable Android/platform APIs over extra libraries.

## Product invariants

- Arabic RTL and English LTR are first-class.
- Core Patient communication works without Internet.
- Bluetooth failure never blocks local Patient communication.
- Text-to-speech failure never blocks visual communication.
- No continuous microphone recording.
- No cloud backend in the initial prototype.
- No advertising or analytics SDK in the prototype.
- Large touch targets are mandatory.
- Accessibility is mandatory.
- Do not add OCR/handwriting AI in V1.
- Do not implement real emergency-service dialing in V1.
- Do not begin hardware/manufacturing work during Android milestones.

## Build discipline

During implementation:
- use targeted compile/tests first

At milestone completion:
- run relevant unit tests
- run `lintDebug` where available
- run `assembleDebug`

Do not repeatedly run the entire suite after tiny edits.

Never claim a build/test passed unless it actually ran successfully.

## Completion report

Keep the final report short:

- status
- changed files
- verification performed
- blocker, if any


## GitHub milestone workflow

For implementation milestones, also read `docs/GITHUB_WORKFLOW.md`.

Required sequence:

1. active GitHub issue
2. exact milestone branch
3. implementation
4. verification
5. commit
6. push
7. open PR
8. stop

Do not automatically merge the PR.
Do not start the next milestone after opening the PR.
Do not discard unrelated user changes.

## GitHub workflow

Follow `docs/GITHUB_WORKFLOW.md`.

For implementation work:

- one issue = one branch = one PR
- synchronize `main` once before branching
- never discard unrelated user changes
- use the milestone branch name from `docs/GITHUB_WORKFLOW.md`
- verify before commit
- push only the active branch
- create a PR only if `gh` is already installed/authenticated
- do not install/authenticate GitHub CLI just for the PR
- do not merge the PR during the same implementation run
- use `Closes #<issue>` in the PR body
- stop after push/PR creation
