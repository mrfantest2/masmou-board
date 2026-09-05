# Masmou Board local project hub

Open http://localhost/masmou-board/ with XAMPP Apache running.

This is a static completed M0 snapshot from 5 September 2026, stored independently of
the Android repository. No npm, database, remote fonts, or backend is needed.
The page includes all 11 engineering milestones, product phases A–F, the
verified debug APK, source documentation, a documentation ZIP, and build notes.

## Refresh after a future approved milestone

1. Verify the new APK in the Android repository first.
2. Copy the verified APK into `downloads/` and update its SHA-256 file.
3. Refresh the relevant documentation copies and regenerate the handoff ZIP.
4. Update the visible version, commit, PR state, date, verification results,
   milestone status, download size, and screenshot in `index.html`.
5. Verify each download through Apache before sharing the updated page.

`index.html` and `styles.css` are the website source. `assets/` holds the
actual M0 device screenshot. `downloads/` contains copies; original repository
documents and PDFs were not modified. The APK is a debug prototype, not a
production release. PR #15 and Issue #1 are recorded as merged and closed; this is not a live status feed.

For another device on the same network, substitute this PC's LAN address
for `localhost`, subject to the existing Apache and firewall configuration.
