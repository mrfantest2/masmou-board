# Masmou Patient — Windows Test Client

This is a native Windows test version of the Patient experience.

It mirrors the Android MVP interaction model for desktop testing:

- drawing with mouse, touch or pen pointer input
- black / blue / red / green
- eraser
- canvas lock
- two-step clear protection
- Yes / No / Water / Pain / Toilet / Nurse / Family / Urgent
- typed message + local Windows speech
- English / Arabic switch
- low-vision mode
- offline operation

## Boundary

This is a testing client, not the canonical production Patient implementation.
The Android Patient app remains the product source of truth for mobile/bedside behavior.
The Windows test client does not include the Android BLE Patient transport.

## Run

`python app.py`

## Build

`pyinstaller --noconfirm --clean --onefile --windowed --name MasmouPatientWindows app.py`

Output: `dist\MasmouPatientWindows.exe`
