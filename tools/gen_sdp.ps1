$res = "E:\Hardik 2026\IP TV\com.iptvplayer.xtreamiptv.myiptvpro\app\src\main\res"
$inv = [Globalization.CultureInfo]::InvariantCulture
$ref = 393.0   # HTML design viewport width: 1 sdp == 1 HTML px at this smallest width
function Emit($path, $scale, $label) {
  $sb = New-Object Text.StringBuilder
  [void]$sb.AppendLine('<?xml version="1.0" encoding="utf-8"?>')
  [void]$sb.AppendLine("<!-- SDP scale set ($label): 1 sdp = 1 px of the 393-wide HTML design, scaled by the screen's smallest width. Generated: tools/gen_sdp.ps1 -->")
  [void]$sb.AppendLine('<resources>')
  function Add($name, $v) {
    $s = ($v * $scale).ToString("0.##", $inv)
    [void]$sb.AppendLine("    <dimen name=""$name"">${s}dp</dimen>")
  }
  for ($i = 0; $i -le 520; $i++) { Add "_${i}sdp" $i }
  for ($i = 1; $i -le 60; $i++) { Add "_minus${i}sdp" (-$i) }
  for ($i = 0; $i -le 60; $i++) { Add "_${i}_5sdp" ($i + 0.5) }
  for ($i = 0; $i -le 60; $i++) { Add "_minus${i}_5sdp" (-($i + 0.5)) }
  [void]$sb.AppendLine('</resources>')
  [IO.File]::WriteAllText($path, $sb.ToString(), (New-Object Text.UTF8Encoding $false))
}
Emit "$res\values\sdp.xml" (300.0 / $ref) "default, sw<300"
foreach ($q in 300,330,360,390,420,450,480,510,540,570,600,630,660,690,720,750,780,810,840,870,900,930,960,990,1020,1050,1080) {
  $dir = "$res\values-sw${q}dp"
  if (!(Test-Path $dir)) { New-Item -ItemType Directory $dir | Out-Null }
  Emit "$dir\sdp.xml" ($q / $ref) "sw$q"
}
"done"
