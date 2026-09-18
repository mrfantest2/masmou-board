from __future__ import annotations

import base64
import subprocess
import tkinter as tk
from tkinter import font as tkfont
from tkinter import messagebox
from typing import Optional


NAVY = "#15324B"
TEAL = "#2F6F62"
BG = "#F3F6F8"
SOFT = "#EAF0F4"
BORDER = "#D4DEE5"
RED = "#C63D3D"
PALE_RED = "#FDE2E2"
PALE_GREEN = "#DDEDE9"
WHITE = "#FFFFFF"
BLACK = "#111111"
BLUE = "#1F5FAE"
GREEN = "#2F7D4A"


ACTIONS = [
    ("Yes", "نعم", False),
    ("No", "لا", False),
    ("Water", "أريد ماء", False),
    ("Pain", "لدي ألم", True),
    ("Toilet", "أحتاج الحمام", False),
    ("Nurse / Help", "أحتاج الممرضة", False),
    ("Family", "أريد عائلتي", False),
    ("Urgent", "الأمر عاجل", True),
]


class MasmouPatientWindows:
    def __init__(self, root: tk.Tk) -> None:
        self.root = root
        self.root.title("Masmou Patient — Windows Test")
        self.root.configure(bg=BG)
        self.root.minsize(1024, 640)
        try:
            self.root.state("zoomed")
        except tk.TclError:
            self.root.geometry("1400x820")

        self.arabic = False
        self.low_vision = False
        self.locked = False
        self.eraser = False
        self.clear_armed = False
        self.draw_color = BLACK
        self.last_x: Optional[int] = None
        self.last_y: Optional[int] = None
        self.speech_process: Optional[subprocess.Popen] = None

        self.base_font = tkfont.Font(family="Segoe UI", size=12)
        self.small_font = tkfont.Font(family="Segoe UI", size=10)
        self.heading_font = tkfont.Font(family="Segoe UI", size=20, weight="bold")
        self.section_font = tkfont.Font(family="Segoe UI", size=14, weight="bold")
        self.button_font = tkfont.Font(family="Segoe UI", size=13, weight="bold")
        self.big_button_font = tkfont.Font(family="Segoe UI", size=16, weight="bold")

        self.status_var = tk.StringVar()
        self.last_message_var = tk.StringVar(value="")
        self.typed_var = tk.StringVar()
        self.speech_rate = tk.IntVar(value=90)

        self._build_ui()
        self._apply_language()
        self._apply_scale()

    def _build_ui(self) -> None:
        outer = tk.Frame(self.root, bg=BG)
        outer.pack(fill="both", expand=True, padx=14, pady=14)

        self.header = tk.Frame(outer, bg=WHITE, bd=0, highlightthickness=1, highlightbackground=BORDER)
        self.header.pack(fill="x", pady=(0, 12))
        self.header.grid_columnconfigure(0, weight=1)

        left_header = tk.Frame(self.header, bg=WHITE)
        left_header.grid(row=0, column=0, sticky="w", padx=16, pady=10)

        self.title_label = tk.Label(left_header, text="", bg=WHITE, fg=NAVY, font=self.heading_font)
        self.title_label.pack(anchor="w")

        self.subtitle_label = tk.Label(left_header, text="", bg=WHITE, fg=NAVY, font=self.small_font)
        self.subtitle_label.pack(anchor="w", pady=(2, 0))

        right_header = tk.Frame(self.header, bg=WHITE)
        right_header.grid(row=0, column=1, sticky="e", padx=12, pady=8)

        self.status_badge = tk.Label(
            right_header,
            textvariable=self.status_var,
            bg=PALE_GREEN,
            fg=NAVY,
            font=self.small_font,
            padx=12,
            pady=8,
        )
        self.status_badge.pack(side="left", padx=5)

        self.low_vision_button = tk.Button(
            right_header,
            command=self._toggle_low_vision,
            relief="flat",
            bg=SOFT,
            fg=NAVY,
            activebackground="#DCE6EC",
            font=self.small_font,
            padx=12,
            pady=7,
        )
        self.low_vision_button.pack(side="left", padx=5)

        self.language_button = tk.Button(
            right_header,
            command=self._toggle_language,
            relief="flat",
            bg=NAVY,
            fg=WHITE,
            activebackground="#0E2539",
            activeforeground=WHITE,
            font=self.button_font,
            padx=16,
            pady=7,
        )
        self.language_button.pack(side="left", padx=5)

        body = tk.Frame(outer, bg=BG)
        body.pack(fill="both", expand=True)
        body.grid_columnconfigure(0, weight=3, uniform="body")
        body.grid_columnconfigure(1, weight=2, uniform="body")
        body.grid_rowconfigure(0, weight=1)

        self.canvas_panel = tk.Frame(body, bg=WHITE, highlightthickness=1, highlightbackground=BORDER)
        self.canvas_panel.grid(row=0, column=0, sticky="nsew", padx=(0, 8))
        self.canvas_panel.grid_rowconfigure(1, weight=1)
        self.canvas_panel.grid_columnconfigure(0, weight=1)

        toolbar = tk.Frame(self.canvas_panel, bg=WHITE)
        toolbar.grid(row=0, column=0, sticky="ew", padx=10, pady=10)
        toolbar.grid_columnconfigure(0, weight=1)

        self.canvas_title = tk.Label(toolbar, text="", bg=WHITE, fg=NAVY, font=self.section_font)
        self.canvas_title.grid(row=0, column=0, sticky="w")

        color_bar = tk.Frame(toolbar, bg=WHITE)
        color_bar.grid(row=0, column=1, sticky="e")

        self.color_buttons: list[tk.Button] = []
        for color in (BLACK, BLUE, RED, GREEN):
            btn = tk.Button(
                color_bar,
                bg=color,
                activebackground=color,
                width=2,
                height=1,
                relief="solid",
                bd=2,
                command=lambda c=color: self._select_color(c),
            )
            btn.pack(side="left", padx=3)
            self.color_buttons.append(btn)

        self.eraser_button = tk.Button(
            color_bar,
            command=self._toggle_eraser,
            relief="flat",
            bg=SOFT,
            fg=NAVY,
            font=self.small_font,
            padx=10,
            pady=5,
        )
        self.eraser_button.pack(side="left", padx=4)

        self.lock_button = tk.Button(
            color_bar,
            command=self._toggle_lock,
            relief="flat",
            bg=SOFT,
            fg=NAVY,
            font=self.small_font,
            padx=10,
            pady=5,
        )
        self.lock_button.pack(side="left", padx=4)

        self.clear_button = tk.Button(
            color_bar,
            command=self._clear_canvas,
            relief="flat",
            bg=SOFT,
            fg=NAVY,
            font=self.small_font,
            padx=10,
            pady=5,
        )
        self.clear_button.pack(side="left", padx=4)

        self.canvas = tk.Canvas(
            self.canvas_panel,
            bg=WHITE,
            highlightthickness=2,
            highlightbackground="#DDE5E9",
            cursor="pencil",
        )
        self.canvas.grid(row=1, column=0, sticky="nsew", padx=10, pady=(0, 10))
        self.canvas.bind("<ButtonPress-1>", self._draw_start)
        self.canvas.bind("<B1-Motion>", self._draw_move)
        self.canvas.bind("<ButtonRelease-1>", self._draw_end)

        self.canvas_hint = self.canvas.create_text(
            0,
            0,
            text="",
            fill="#83929B",
            font=self.section_font,
            anchor="center",
        )
        self.canvas.bind("<Configure>", self._center_hint)

        self.actions_panel = tk.Frame(body, bg=WHITE, highlightthickness=1, highlightbackground=BORDER)
        self.actions_panel.grid(row=0, column=1, sticky="nsew", padx=(8, 0))
        self.actions_panel.grid_columnconfigure(0, weight=1)
        self.actions_panel.grid_columnconfigure(1, weight=1)
        for r in range(5):
            self.actions_panel.grid_rowconfigure(r, weight=1 if r > 0 else 0)

        self.actions_title = tk.Label(self.actions_panel, text="", bg=WHITE, fg=NAVY, font=self.section_font)
        self.actions_title.grid(row=0, column=0, columnspan=2, sticky="w", padx=12, pady=(12, 8))

        self.action_buttons: list[tuple[tk.Button, str, str, bool]] = []
        for index, (en, ar, urgent) in enumerate(ACTIONS):
            row = index // 2 + 1
            col = index % 2
            btn = tk.Button(
                self.actions_panel,
                relief="flat",
                bg=PALE_RED if urgent else SOFT,
                fg=RED if urgent else NAVY,
                activebackground="#F6CCCC" if urgent else "#DCE6EC",
                activeforeground=RED if urgent else NAVY,
                font=self.big_button_font,
                wraplength=180,
                command=lambda e=en, a=ar: self._action(e, a),
            )
            btn.grid(row=row, column=col, sticky="nsew", padx=6, pady=6)
            self.action_buttons.append((btn, en, ar, urgent))

        bottom = tk.Frame(outer, bg=WHITE, highlightthickness=1, highlightbackground=BORDER)
        bottom.pack(fill="x", pady=(12, 0))
        bottom.grid_columnconfigure(0, weight=3)
        bottom.grid_columnconfigure(4, weight=2)

        self.message_entry = tk.Entry(
            bottom,
            textvariable=self.typed_var,
            relief="solid",
            bd=1,
            font=self.base_font,
        )
        self.message_entry.grid(row=0, column=0, sticky="ew", padx=(12, 8), pady=12, ipady=9)
        self.message_entry.bind("<Return>", lambda _e: self._speak_typed())

        self.speak_button = tk.Button(
            bottom,
            command=self._speak_typed,
            relief="flat",
            bg=NAVY,
            fg=WHITE,
            activebackground="#0E2539",
            activeforeground=WHITE,
            font=self.button_font,
            padx=18,
            pady=9,
        )
        self.speak_button.grid(row=0, column=1, padx=4, pady=10)

        self.clear_text_button = tk.Button(
            bottom,
            command=lambda: self.typed_var.set(""),
            relief="flat",
            bg=SOFT,
            fg=NAVY,
            font=self.small_font,
            padx=12,
            pady=9,
        )
        self.clear_text_button.grid(row=0, column=2, padx=4, pady=10)

        rate_frame = tk.Frame(bottom, bg=WHITE)
        rate_frame.grid(row=0, column=3, padx=10, pady=5)

        self.rate_label = tk.Label(rate_frame, bg=WHITE, fg=NAVY, font=self.small_font)
        self.rate_label.pack()
        self.rate_scale = tk.Scale(
            rate_frame,
            from_=50,
            to=130,
            orient="horizontal",
            variable=self.speech_rate,
            showvalue=False,
            length=140,
            bg=WHITE,
            highlightthickness=0,
            troughcolor=SOFT,
            command=lambda _v: self._update_rate_label(),
        )
        self.rate_scale.pack()

        self.last_message_label = tk.Label(
            bottom,
            textvariable=self.last_message_var,
            bg=WHITE,
            fg="#5A6D78",
            font=self.small_font,
            anchor="w",
            justify="left",
            wraplength=300,
        )
        self.last_message_label.grid(row=0, column=4, sticky="ew", padx=(8, 12), pady=12)

    def _toggle_language(self) -> None:
        self.arabic = not self.arabic
        self._apply_language()

    def _toggle_low_vision(self) -> None:
        self.low_vision = not self.low_vision
        self._apply_scale()
        self._apply_language()

    def _apply_scale(self) -> None:
        if self.low_vision:
            sizes = (14, 12, 27, 18, 16, 20)
        else:
            sizes = (12, 10, 20, 14, 13, 16)
        self.base_font.configure(size=sizes[0])
        self.small_font.configure(size=sizes[1])
        self.heading_font.configure(size=sizes[2])
        self.section_font.configure(size=sizes[3])
        self.button_font.configure(size=sizes[4])
        self.big_button_font.configure(size=sizes[5])
        self.root.update_idletasks()

    def _apply_language(self) -> None:
        if self.arabic:
            self.title_label.configure(text="مسموع")
            self.subtitle_label.configure(text="مساحة تواصل بسيطة وآمنة")
            self.status_var.set("جاهز بدون إنترنت")
            self.low_vision_button.configure(text="رؤية أوضح" + (" ✓" if self.low_vision else ""))
            self.language_button.configure(text="EN")
            self.canvas_title.configure(text="اكتب أو ارسم")
            self.actions_title.configure(text="احتياجات سريعة")
            self.eraser_button.configure(text="ممحاة")
            self.lock_button.configure(text="فتح" if self.locked else "قفل")
            self.clear_button.configure(text="تأكيد المسح" if self.clear_armed else "مسح")
            self.message_entry.configure(justify="right")
            self.speak_button.configure(text="تحدث")
            self.clear_text_button.configure(text="حذف")
            self.canvas.itemconfigure(
                self.canvas_hint,
                text="اللوحة مقفلة" if self.locked else "استخدم إصبعك أو الفأرة أو القلم",
            )
        else:
            self.title_label.configure(text="Masmou Patient")
            self.subtitle_label.configure(text="Simple, dignified communication")
            self.status_var.set("Offline ready")
            self.low_vision_button.configure(text="Low vision" + (" ✓" if self.low_vision else ""))
            self.language_button.configure(text="عربي")
            self.canvas_title.configure(text="Write or draw")
            self.actions_title.configure(text="Quick needs")
            self.eraser_button.configure(text="Eraser")
            self.lock_button.configure(text="Unlock" if self.locked else "Lock")
            self.clear_button.configure(text="Confirm clear" if self.clear_armed else "Clear")
            self.message_entry.configure(justify="left")
            self.speak_button.configure(text="Speak")
            self.clear_text_button.configure(text="Clear text")
            self.canvas.itemconfigure(
                self.canvas_hint,
                text="Canvas locked" if self.locked else "Use your finger, mouse, or stylus",
            )

        for btn, en, ar, _urgent in self.action_buttons:
            btn.configure(text=ar if self.arabic else en)

        self._update_rate_label()
        if self.last_message_var.get():
            prefix = "آخر رسالة: " if self.arabic else "Last: "
            value = self.last_message_var.get().split(": ", 1)[-1]
            self.last_message_var.set(prefix + value)

    def _update_rate_label(self) -> None:
        prefix = "سرعة الصوت " if self.arabic else "Speech "
        self.rate_label.configure(text=f"{prefix}{self.speech_rate.get()}%")

    def _select_color(self, color: str) -> None:
        self.draw_color = color
        self.eraser = False
        self.eraser_button.configure(bg=SOFT)
        self.clear_armed = False
        self._apply_language()

    def _toggle_eraser(self) -> None:
        self.eraser = not self.eraser
        self.eraser_button.configure(bg="#CBD9E2" if self.eraser else SOFT)
        self.clear_armed = False
        self._apply_language()

    def _toggle_lock(self) -> None:
        self.locked = not self.locked
        if self.locked:
            self.clear_armed = False
        self.clear_button.configure(state="disabled" if self.locked else "normal")
        self.canvas.configure(cursor="arrow" if self.locked else "pencil")
        self._apply_language()

    def _clear_canvas(self) -> None:
        if self.locked:
            return
        if not self.clear_armed:
            self.clear_armed = True
            self._apply_language()
            return
        self.canvas.delete("stroke")
        self.clear_armed = False
        self._apply_language()

    def _center_hint(self, event: tk.Event) -> None:
        self.canvas.coords(self.canvas_hint, event.width / 2, event.height / 2)

    def _draw_start(self, event: tk.Event) -> None:
        if self.locked:
            return
        self.clear_armed = False
        self._apply_language()
        self.last_x, self.last_y = event.x, event.y
        self.canvas.itemconfigure(self.canvas_hint, state="hidden")

    def _draw_move(self, event: tk.Event) -> None:
        if self.locked or self.last_x is None or self.last_y is None:
            return
        width = 28 if self.eraser else (10 if self.low_vision else 6)
        color = WHITE if self.eraser else self.draw_color
        self.canvas.create_line(
            self.last_x,
            self.last_y,
            event.x,
            event.y,
            fill=color,
            width=width,
            capstyle=tk.ROUND,
            smooth=True,
            splinesteps=16,
            tags="stroke",
        )
        self.last_x, self.last_y = event.x, event.y

    def _draw_end(self, _event: tk.Event) -> None:
        self.last_x = None
        self.last_y = None

    def _action(self, en: str, ar: str) -> None:
        self._speak(ar if self.arabic else en)

    def _speak_typed(self) -> None:
        text = self.typed_var.get().strip()
        if text:
            self._speak(text)

    def _speak(self, text: str) -> None:
        self.last_message_var.set(("آخر رسالة: " if self.arabic else "Last: ") + text)
        self._windows_tts(text, "ar" if self.arabic else "en")

    def _windows_tts(self, text: str, lang: str) -> None:
        if self.speech_process and self.speech_process.poll() is None:
            try:
                self.speech_process.terminate()
            except OSError:
                pass

        payload = base64.b64encode(text.encode("utf-8")).decode("ascii")
        rate = max(-10, min(10, round((self.speech_rate.get() - 90) / 8)))
        script = f"""
Add-Type -AssemblyName System.Speech
$s = New-Object System.Speech.Synthesis.SpeechSynthesizer
$s.Rate = {rate}
$target = '{lang}'
$voice = $s.GetInstalledVoices() | Where-Object {{
    $_.VoiceInfo.Culture.Name.ToLower().StartsWith($target)
}} | Select-Object -First 1
if ($voice) {{ $s.SelectVoice($voice.VoiceInfo.Name) }}
$bytes = [Convert]::FromBase64String('{payload}')
$text = [Text.Encoding]::UTF8.GetString($bytes)
$s.Speak($text)
$s.Dispose()
"""
        encoded = base64.b64encode(script.encode("utf-16le")).decode("ascii")
        try:
            self.speech_process = subprocess.Popen(
                [
                    "powershell.exe",
                    "-NoProfile",
                    "-NonInteractive",
                    "-WindowStyle",
                    "Hidden",
                    "-EncodedCommand",
                    encoded,
                ],
                creationflags=getattr(subprocess, "CREATE_NO_WINDOW", 0),
            )
        except OSError:
            messagebox.showwarning(
                "Masmou Patient",
                "Windows speech could not be started. Visual communication remains available.",
            )


def main() -> None:
    root = tk.Tk()
    MasmouPatientWindows(root)
    root.mainloop()


if __name__ == "__main__":
    main()
