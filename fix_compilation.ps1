$base = "C:\Users\L900m\Downloads\Versus-main(1)\Versus-main\src\main\java\me\robomonkey\versus"

# 1. DuelManager.java
$f = "$base\duel\manager\DuelManager.java"
if (Test-Path $f) {
    (Get-Content $f) -replace 'import me\.robomonkey\.versus\.duel\.eventlisteners\.\*;', '' | Set-Content $f
}

# 2. CosmeticsManager.java
$f = "$base\cosmetic\manager\CosmeticsManager.java"
if (Test-Path $f) {
    (Get-Content $f) -replace 'import me\.robomonkey\.versus\.cosmetics\.killeffects\.\*;', '' -replace 'import me\.robomonkey\.versus\.cosmetics\.victoryeffects\.\*;', '' | Set-Content $f
}

# 3. SetCommand.java
$f = "$base\arena\command\SetCommand.java"
if (Test-Path $f) {
    (Get-Content $f) -replace 'import me\.robomonkey\.versus\.arena\.\*;', '' -replace 'import me\.robomonkey\.versus\.config\.command\.SetCommand;', '' | Set-Content $f
}

# 4. DuelGroup commands
$dir = "$base\duel\command"
if (Test-Path $dir) {
    foreach ($file in Get-ChildItem "$dir\DuelGroup*.java") {
        $content = Get-Content $file.FullName
        $newContent = @()
        $added = $false
        foreach ($line in $content) {
            if ($line -match "^package " -and -not $added) {
                $newContent += $line
                $newContent += "import me.robomonkey.versus.command.AbstractCommand;"
                $newContent += "import me.robomonkey.versus.command.RootCommand;"
                $added = $true
            } else {
                $newContent += $line
            }
        }
        Set-Content -Path $file.FullName -Value $newContent
    }
}
