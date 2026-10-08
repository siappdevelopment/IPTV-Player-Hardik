$adb="$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
for($i=0;$i -lt 6;$i++){
  $f=(& $adb shell "dumpsys window | grep mCurrentFocus") -join ""
  if($f -match 'com.android.chrome|org.chromium|brave'){ & $adb shell input keyevent 4; Start-Sleep 2; continue }
  if($f -match 'AdActivity'){ & $adb shell input keyevent 4; Start-Sleep 2; continue }
  break
}
$f=(& $adb shell "dumpsys window | grep mCurrentFocus") -join ""
"NOW: " + ($f -replace '.*smartiptv/','' -replace '\}','')
