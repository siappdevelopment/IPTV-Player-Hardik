# Converts Figma-exported icon SVGs (home screen call) into vector drawables on the 24-unit icon grid.
# The Figma frame is the glyph bounding box, centred in the HTML font-size box S; so the vector keeps the
# icon's layout size (S px box) by scaling 24/S and centring the glyph.
param([string]$Src = "C:\Users\DREAMWORLD\AppData\Local\Temp\claude\figma_assets")
$res = Join-Path $PSScriptRoot "..\app\src\main\res\drawable"
$inv = [Globalization.CultureInfo]::InvariantCulture
$map = @(
  @("frame1","ic_search",22), @("frame2","ic_settings",24), @("frame3","ic_grid_view",21), @("frame4","ic_view_list_off",21),
  @("frame5","ic_menu",19), @("vector2","ic_link",19), @("vector3","ic_description",19), @("frame6","ic_edit",18),
  @("frame7","ic_delete",18), @("frame8","ic_family_restroom",21), @("frame10","ic_chevron_right",22),
  @("frame11","ic_theaters",21), @("frame13","ic_movie",21), @("frame14","ic_sports_soccer",21),
  @("frame15","ic_video_library",24), @("frame16","ic_live_tv_off",24), @("frame17","ic_history_off",24),
  @("frame18","ic_favorite_border",24), @("frame19","ic_add",32)
)
function F([double]$v) { return $v.ToString("0.####", $inv) }
foreach ($m in $map) {
  $svg = Get-Content (Join-Path $Src ($m[0] + ".svg")) -Raw
  $w = [double]::Parse(([regex]::Match($svg, '<svg[^>]*\swidth="([\d.]+)"')).Groups[1].Value, $inv)
  $h = [double]::Parse(([regex]::Match($svg, '<svg[^>]*\sheight="([\d.]+)"')).Groups[1].Value, $inv)
  $s = [double]$m[2]
  $k = 24.0 / $s
  $tx = $k * ($s - $w) / 2; $ty = $k * ($s - $h) / 2
  $paths = [regex]::Matches($svg, '<path[^>]*\sd="([^"]+)"[^>]*>')
  $sb = New-Object Text.StringBuilder
  [void]$sb.AppendLine('<?xml version="1.0" encoding="utf-8"?>')
  [void]$sb.AppendLine("<!-- Figma asset ($($m[0]).svg): glyph ${w} x ${h} in a ${s}px icon box -->")
  [void]$sb.AppendLine('<vector xmlns:android="http://schemas.android.com/apk/res/android"')
  [void]$sb.AppendLine('    android:width="@dimen/_24sdp"')
  [void]$sb.AppendLine('    android:height="@dimen/_24sdp"')
  [void]$sb.AppendLine('    android:viewportWidth="24"')
  [void]$sb.AppendLine('    android:viewportHeight="24">')
  [void]$sb.AppendLine("    <group android:translateX=""$(F $tx)"" android:translateY=""$(F $ty)"" android:scaleX=""$(F $k)"" android:scaleY=""$(F $k)"">")
  foreach ($p in $paths) {
    $tag = $p.Value
    $fill = if ($tag -match 'fill-rule="evenodd"') { ' android:fillType="evenOdd"' } else { '' }
    [void]$sb.AppendLine("        <path android:fillColor=""@android:color/white""$fill android:pathData=""$($p.Groups[1].Value)"" />")
  }
  [void]$sb.AppendLine('    </group>')
  [void]$sb.AppendLine('</vector>')
  [IO.File]::WriteAllText((Join-Path $res ($m[1] + ".xml")), $sb.ToString(), (New-Object Text.UTF8Encoding $false))
  "{0} -> {1} ({2}x{3} @ {4}px, {5} path)" -f $m[0], $m[1], $w, $h, $s, $paths.Count
}
