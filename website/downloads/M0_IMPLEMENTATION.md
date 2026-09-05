# M0 Android foundation

Scope: GitHub issue #1 only. One `app-patient` application uses Kotlin,
Compose, Material 3, and Gradle Kotlin DSL. The single activity displays a
static, scrollable welcome screen with safe system-bar insets and a TalkBack
heading. Android locale selects English or Arabic. The application requests
landscape orientation and has no network permissions or runtime services.

## Build and verify

Install JDK 17 or newer and Android SDK platform 35. Set `ANDROID_HOME` or
create an untracked `local.properties` containing `sdk.dir=<SDK path>`.
Dependency downloads require network access on the first build; the installed
application does not require internet access.

```powershell
.\gradlew.bat :app-patient:compileDebugKotlin
.\gradlew.bat :app-patient:testDebugUnitTest :app-patient:lintDebug :app-patient:assembleDebug
.\gradlew.bat :app-patient:connectedDebugAndroidTest
```

The connected tests require an unlocked Android device or emulator (API 26+).
They check launch visibility, landscape orientation, and locale resource/layout
direction selection. No business logic exists yet, so no JVM unit tests are
included. APK: `app-patient/build/outputs/apk/debug/app-patient-debug.apk`.

## Git preservation

The existing directory was attached to the fetched `origin/main` history
without replacing its working files. Existing handoff/documentation differences
and PDFs remain local and are excluded from the M0 implementation commit.
No M1 or later functionality is included.

## Verification on 2026-09-05

- `:app-patient:compileDebugKotlin :app-patient:compileDebugAndroidTestKotlin`: passed.
- `:app-patient:testDebugUnitTest`: `NO-SOURCE` (no JVM business logic).
- `:app-patient:lintDebug :app-patient:assembleDebug :app-patient:connectedDebugAndroidTest`:
  passed; 3 device tests passed on GM1903, Android 12 / API 31.
- Lint: 0 errors, 8 warnings. Five dependency-update advisories are retained
  for the pinned compatible versions; one target-API advisory reflects API 35;
  one fixed-orientation advisory reflects the explicit landscape requirement;
  one backup-rules advisory is retained because M0 stores no patient data and
  disables cloud backup. Persistent data and backup policy are outside M0.
- Packaged APK permissions: no `android.permission.INTERNET`.
- All 18 pre-existing files retained identical SHA-256 hashes.
- `git diff --cached --check`: passed. Whole-working-tree `git diff --check`
  reports pre-existing Markdown trailing spaces in `AGENTS.md`,
  `CODEX_HANDOFF.md`, and `README.md`; those files are preserved and unstaged.

Initial checks caught the absent manifest during scaffolding and an API-27
theme attribute in the API-26 base theme. Both were fixed before the final
successful build. System-bar icon styles are explicitly light-theme styles
to keep the pale launch palette readable when the device uses dark mode.
