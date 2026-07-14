$files = Get-ChildItem -Path src\main\java\me\robomonkey\versus -Recurse -Filter *.java
$utf8NoBom = New-Object System.Text.UTF8Encoding $False
foreach ($file in $files) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
}
