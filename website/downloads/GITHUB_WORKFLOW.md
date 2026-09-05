# GitHub Workflow — Masmou Board

Repository: `mrfantest2/masmou-board`

This workflow is intentionally simple to reduce Codex usage and avoid branch/repository confusion.

## Core rule

One GitHub issue = one implementation branch = one pull request.

Do not combine unrelated milestones in one branch or PR.

## Before starting an issue

From the repository root:

```powershell
git status
git switch main
git pull --ff-only
```

If the working tree contains unrelated uncommitted user changes:

- do not discard them
- do not reset them
- do not overwrite them
- stop and report the exact files blocking a safe branch start

Do not use destructive Git commands such as:

```text
git reset --hard
git clean -fd
git checkout -- .
```

unless the user explicitly instructs it.

## Branch naming

Use:

```text
codex/m0-foundation
codex/m1-patient-shell
codex/m2-writing-canvas
codex/m3-quick-actions-tts
codex/m4-accessibility
codex/m5-family-foundation
codex/m6-ble
codex/m7-events-alerts
codex/m8-hardening
codex/m9-device-qa
codex/m10-hardware-research
```

For a non-milestone issue:

```text
codex/issue-<number>-<short-slug>
```

Create the branch only after `main` is synchronized.

Example:

```powershell
git switch -c codex/m0-foundation
```

## Implementation

Read only:

1. `AGENTS.md`
2. `docs/HANDOFF.md`
3. the active GitHub issue
4. the relevant milestone section

Open other docs only if the task requires them.

Implement only the active issue.

Do not opportunistically implement the next milestone.

## Verification

During coding:

- use the smallest relevant compile/test command
- do not repeatedly run the full suite after trivial edits

At issue completion, run the milestone-required verification.

For Android milestones this normally includes:

```text
relevant targeted tests
lintDebug
assembleDebug
```

If a command cannot run because tooling is unavailable, report that fact exactly.

Never claim successful verification without successful command output.

## Commit

Prefer one clean implementation commit for a small milestone.

Suggested commit style:

```text
feat(m0): bootstrap Masmou Patient Android foundation
feat(m1): build patient communication shell
feat(m2): add freehand writing canvas
feat(m3): add quick actions and local TTS
feat(m4): add accessibility modes
feat(m5): add Family app foundation
feat(m6): add BLE patient-family transport
feat(m7): add event history and pain alert
fix(m8): harden bedside reliability
test(m9): validate real-device behavior
docs(m10): research hardware prototype
```

Before commit:

```powershell
git status
git diff --check
```

Then stage only files belonging to the active issue.

Avoid `git add -A` when unrelated files exist.

## Push

Push exactly the active branch:

```powershell
git push -u origin <branch>
```

If push fails because GitHub authentication is unavailable:

- do not spend agent time installing credential managers
- do not repeatedly retry authentication
- do not change repository remotes
- stop and report the exact Git error

## Pull request

If GitHub CLI (`gh`) is already installed and authenticated, create one PR.

Do not install or authenticate `gh` merely to create the PR.

PR title format:

```text
M0 — Android Foundation
M1 — Patient Communication Shell
...
```

PR body should stay concise:

```md
Closes #<issue-number>

## Changes
- <short bullets>

## Verification
- `<command>` — PASS
- `<command>` — PASS

## Scope
Only the requested milestone/issue is included.
```

Example:

```powershell
gh pr create `
  --base main `
  --head codex/m0-foundation `
  --title "M0 — Android Foundation" `
  --body "Closes #1`n`n## Changes`n- Bootstrapped Masmou Patient Android project.`n`n## Verification`n- assembleDebug — PASS`n- lintDebug — PASS`n`n## Scope`nOnly M0 is included."
```

If `gh` is unavailable but `git push` succeeded:

- do not install it
- stop after push
- return the branch name and commit SHA
- state that PR creation remains external

## Do not merge from the implementation run

Codex should stop after:

1. implementation completed
2. verification completed
3. commit created
4. branch pushed
5. PR created if `gh` was already available

Do not merge the PR in the same implementation run.

Do not close the issue manually. The PR body should use `Closes #N` so GitHub closes it when merged.

This creates a clean review boundary and prevents Codex from consuming more usage reviewing and merging its own work.

## After merge

The next milestone starts from a fresh synchronized `main`.

Do not reuse the previous milestone branch.

Workflow:

```powershell
git switch main
git pull --ff-only
git branch -d <merged-local-branch>
git switch -c <next-branch>
```

Delete the local merged branch only after merge is confirmed.

## Usage-efficiency rules

Do not:

- search all GitHub issues unless needed
- inspect all branches
- inspect all repo history
- open all docs
- create multiple branches for one issue
- create multiple PRs for one issue
- repeatedly poll CI
- install GitHub CLI
- troubleshoot account authentication unless specifically asked
- merge the PR during the implementation run

The desired pipeline is:

```text
Issue
  ↓
Sync main once
  ↓
Create one branch
  ↓
Implement
  ↓
Targeted verification
  ↓
Final milestone verification
  ↓
One commit
  ↓
Push once
  ↓
Create PR if gh already works
  ↓
STOP
```
