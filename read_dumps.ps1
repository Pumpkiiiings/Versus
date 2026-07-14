$files = @(
'C:\Users\L900m\.gemini\antigravity-ide\brain\5632072f-262a-448f-9141-8a005964e95e\implementation_plan.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\7868373b-ea0c-408a-8d11-1c1c5b209aad\implementation_plan.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\5632072f-262a-448f-9141-8a005964e95e\walkthrough.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\97babd96-d3c9-421b-9825-ab112a9a3d77\walkthrough.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\b0774596-1a11-4c99-a292-102ce568d3d0\walkthrough.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\b0774596-1a11-4c99-a292-102ce568d3d0\implementation_plan.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\7868373b-ea0c-408a-8d11-1c1c5b209aad\walkthrough.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\e019fe28-f1ce-4f9b-adf7-cd6dbe064f82\walkthrough.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\e019fe28-f1ce-4f9b-adf7-cd6dbe064f82\task.md',
'C:\Users\L900m\.gemini\antigravity-ide\brain\737018cf-46f6-4aab-b2a4-75baa069a383\implementation_plan.md'
)

$out = "C:\Users\L900m\Downloads\Versus-main(1)\Versus-main\dumps.txt"
if (Test-Path $out) { Remove-Item $out }

foreach ($f in $files) {
    if (Test-Path $f) {
        Add-Content -Path $out -Value "==================================================="
        Add-Content -Path $out -Value "File: $f"
        Add-Content -Path $out -Value "==================================================="
        Get-Content $f | Add-Content -Path $out
        Add-Content -Path $out -Value ""
    }
}
