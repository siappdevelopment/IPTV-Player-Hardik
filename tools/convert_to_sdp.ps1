# Converts every dp / sp / dip literal in app resources to @dimen/_Nsdp references (see gen_sdp.ps1).
$res = Join-Path $PSScriptRoot "..\app\src\main\res"
$enc = New-Object Text.UTF8Encoding $false
function SdpName([string]$num) {
  $neg = $num.StartsWith("-")
  $n = $num.TrimStart("-")
  if ($n.Contains(".")) { $n = $n.TrimEnd("0").TrimEnd("."); }
  $n = $n.Replace(".", "_")
  if ($neg) { return "@dimen/_minus${n}sdp" }
  return "@dimen/_${n}sdp"
}
$files = Get-ChildItem $res -Recurse -Filter *.xml | Where-Object {
  $_.Name -ne "sdp.xml" -and $_.FullName -notmatch "mipmap-" -and $_.Name -notmatch "^ic_launcher"
}
$count = 0
foreach ($f in $files) {
  $t = [IO.File]::ReadAllText($f.FullName)
  $o = $t
  $t = [regex]::Replace($t, '="(-?\d+(?:\.\d+)?)(?:dp|sp|dip)"', { param($m) '="' + (SdpName $m.Groups[1].Value) + '"' })
  $t = [regex]::Replace($t, '>(-?\d+(?:\.\d+)?)(?:dp|sp|dip)<', { param($m) '>' + (SdpName $m.Groups[1].Value) + '<' })
  if ($t -ne $o) { [IO.File]::WriteAllText($f.FullName, $t, $enc); $count++ }
}
"converted files: $count"
