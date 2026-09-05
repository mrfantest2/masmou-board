$ErrorActionPreference = "Stop"

$Target = "C:\Users\Administrator\masmou-board"
$Source = Split-Path -Parent $MyInvocation.MyCommand.Path

if (-not (Test-Path -LiteralPath $Target)) {
    throw "Target repository does not exist: $Target"
}

Write-Host "Installing Masmou Codex handoff into $Target"

$items = @(
    "AGENTS.md",
    "CODEX_HANDOFF.md",
    "FIRST_CODEX_PROMPT.txt",
    "GITHUB_FLOW_QUICK.md",
    "NON_CODEX_HANDOFF.md",
    "PACKAGE_MANIFEST.json",
    "README.md",
    "docs",
    "hardware",
    "website"
)

foreach ($item in $items) {
    $src = Join-Path $Source $item
    $dst = Join-Path $Target $item

    if (Test-Path -LiteralPath $src) {
        if ((Get-Item -LiteralPath $src).PSIsContainer) {
            New-Item -ItemType Directory -Force -Path $dst | Out-Null
            Copy-Item -LiteralPath (Join-Path $src "*") -Destination $dst -Recurse -Force
        } else {
            Copy-Item -LiteralPath $src -Destination $dst -Force
        }
    }
}

Write-Host ""
Write-Host "Installed."
Write-Host "Next:"
Write-Host "  cd $Target"
Write-Host "  git status"
Write-Host ""
Write-Host "Give Codex FIRST_CODEX_PROMPT.txt"
