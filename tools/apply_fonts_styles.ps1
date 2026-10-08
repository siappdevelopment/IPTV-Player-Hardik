$p = Join-Path $PSScriptRoot "..\app\src\main\res\values\styles.xml"
$weights = @{ "Text.Display"="extrabold"; "Text.Headline"="bold"; "Text.Title"="bold"; "Text.Subtitle"="bold"; "Text.BodyStrong"="semibold"; "Text.Section"="bold"; "Button.Primary"="semibold"; "Button.Text"="semibold"; "Chip"="semibold" }
$out = New-Object System.Collections.Generic.List[string]
$cur = ""
foreach ($line in [IO.File]::ReadAllLines($p)) {
  if ($line -match '<style name="([^"]+)"') { $cur = $Matches[1] }
  if ($line -match 'name="android:fontFamily"') { continue }          # drop old sans-serif*
  if ($line -match 'name="android:textStyle">bold<') {
    if ($weights.ContainsKey($cur)) { $out.Add('        <item name="android:fontFamily">@font/figtree_' + $weights[$cur] + '</item>') }
    continue
  }
  $out.Add($line)
  if ($line -match '<style name="Text" parent=""') { $out.Add('        <item name="android:fontFamily">@font/figtree_regular</item>') }
}
[IO.File]::WriteAllLines($p, $out, (New-Object Text.UTF8Encoding $false))
