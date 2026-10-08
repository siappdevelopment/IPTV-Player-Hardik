# Extracts extra Material icons (rounded set) from Android Studio's android.jar into res/drawable.
Add-Type -AssemblyName System.IO.Compression.FileSystem
$jar = "C:\Program Files\Android\Android Studio\plugins\android\lib\android.jar"
$res = Join-Path $PSScriptRoot "..\app\src\main\res\drawable"
$names = "star_border content_copy sms mail bluetooth person playlist_play ads_click analytics tune".Split(" ")
$z = [IO.Compression.ZipFile]::OpenRead($jar)
foreach ($n in $names) {
  $e = $z.GetEntry("images/material/icons/materialiconsround/$n/round_${n}_24.xml")
  if (-not $e) { "MISSING $n"; continue }
  $r = New-Object IO.StreamReader($e.Open()); $t = $r.ReadToEnd(); $r.Close()
  $t = $t -replace '\s*android:tint="[^"]*"', ''
  [IO.File]::WriteAllText("$res\ic_$n.xml", $t, (New-Object System.Text.UTF8Encoding($false)))
  "ok $n"
}
$z.Dispose()
