# Replaces android:textStyle="bold" with the Figtree weight the HTML uses at that font size.
$res = Join-Path $PSScriptRoot "..\app\src\main\res"
$enc = New-Object Text.UTF8Encoding $false
$named = @{ text_display=28; text_headline=22; text_title=20; text_subtitle=17; text_body=15; text_body_small=14; text_label=13; text_caption=12; text_micro=10 }
# style name -> text size (own item only)
$styleSize = @{}
$cur = ""
foreach ($l in [IO.File]::ReadAllLines("$res\values\styles.xml")) {
  if ($l -match '<style name="([^"]+)"') { $cur = $Matches[1] }
  if ($l -match 'name="android:textSize">@dimen/([^<]+)<') { $d = $Matches[1]; $styleSize[$cur] = if ($named.ContainsKey($d)) { $named[$d] } elseif ($d -match '^_(\d+)(?:_(\d))?sdp$') { [double]("$($Matches[1])." + $(if ($Matches[2]) { $Matches[2] } else { "0" })) } else { $null } }
}
function StyleSize($name) {
  while ($name) { if ($styleSize.ContainsKey($name) -and $styleSize[$name]) { return $styleSize[$name] }; $i = $name.LastIndexOf("."); if ($i -lt 0) { break }; $name = $name.Substring(0, $i) }
  return $null
}
function DimSize($d) {
  if ($named.ContainsKey($d)) { return $named[$d] }
  if ($d -match '^_(\d+)(?:_(\d))?sdp$') { return [double]("$($Matches[1])." + $(if ($Matches[2]) { $Matches[2] } else { "0" })) }
  return $null
}
function Weight($size) {
  if ($null -eq $size) { return "bold" }
  if ($size -ge 12.5 -and $size -le 16.5) { return "semibold" }
  return "bold"
}
$report = New-Object System.Collections.Generic.List[string]
Get-ChildItem $res -Recurse -Include *.xml | Where-Object { $_.FullName.Contains("\layout") } | ForEach-Object {
  $f = $_; $t = [IO.File]::ReadAllText($f.FullName)
  $new = [regex]::Replace($t, '<[A-Za-z][\w.]*(?=[\s>])[^<>]*android:textStyle="bold"[^<>]*>', {
    param($m)
    $tag = $m.Value
    $size = $null
    if ($tag -match 'android:textSize="@dimen/([^"]+)"') { $size = DimSize $Matches[1] }
    elseif ($tag -match 'style="@style/([^"]+)"') { $size = StyleSize $Matches[1] }
    $w = Weight $size
    $id = if ($tag -match 'android:id="@\+id/([^"]+)"') { $Matches[1] } else { "-" }
    $script:report.Add("$($f.Name) $id size=$size -> $w")
    $tag -replace 'android:textStyle="bold"', ('android:fontFamily="@font/figtree_' + $w + '"')
  })
  if ($new -ne $t) { [IO.File]::WriteAllText($f.FullName, $new, $enc) }
}
$report | Sort-Object | Out-File (Join-Path $PSScriptRoot "fonts_layouts_report.txt") -Encoding utf8
"replaced: $($report.Count)"
