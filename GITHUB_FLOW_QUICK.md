# Masmou GitHub Flow — Quick Reference

```powershell
cd C:\Users\Administrator\masmou-board

git status
git switch main
git pull --ff-only
git switch -c <branch-from-docs/GITHUB_WORKFLOW.md>

# Implement and verify exactly one explicitly requested issue here.

git status
git diff --check
git add <only-active-issue-files>
git commit -m "<focused commit message>"
git push -u origin <active-issue-branch>

# Only if gh already works:
gh pr create --base main --head <active-issue-branch> --title "<issue title>" --body "Closes #<issue>"
```

Do not merge in the same implementation run.

M0 is already merged through PR #15. Do not recreate its branch or PR.
