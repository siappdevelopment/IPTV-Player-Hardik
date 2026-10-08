param([string]$cmd,[string]$a1,[string]$a2,[string]$a3,[string]$a4,[string]$a5)
$adb="$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$d=$PSScriptRoot
switch($cmd){
 "ui" {
   & $adb shell uiautomator dump /sdcard/ui.xml | Out-Null
   & $adb pull /sdcard/ui.xml "$d\ui.xml" | Out-Null
   [xml]$x=Get-Content "$d\ui.xml" -Encoding UTF8
   $cur = (& $adb shell "dumpsys window | grep mCurrentFocus") -join ""
   "FOCUS: $cur"
   foreach($n in $x.SelectNodes("//node")){
     $t=$n.text; $cd=$n.'content-desc'; $rid=$n.'resource-id' -replace '^.*:id/',''
     $flags=@(); if($n.clickable -eq 'true'){$flags+='C'}; if($n.'long-clickable' -eq 'true'){$flags+='L'}; if($n.scrollable -eq 'true'){$flags+='S'}; if($n.checkable -eq 'true'){$flags+='K:'+$n.checked}; if($n.class -like '*EditText*'){$flags+='E'}
     if($t -or $cd -or $flags.Count -gt 0){
       $cls=($n.class -split '\.')[-1]
       "{0} [{1}] t='{2}' cd='{3}' id={4} {5}" -f $cls,($flags -join ''),$t,$cd,$rid,$n.bounds
     }
   }
 }
 "shot" { & $adb shell screencap -p /sdcard/s.png; & $adb pull /sdcard/s.png "$d\shots\$a1.png" | Out-Null; "saved $a1" }
 "tap" { & $adb shell input tap $a1 $a2 }
 "long" { & $adb shell input swipe $a1 $a2 $a1 $a2 1000 }
 "swipe" { & $adb shell input swipe $a1 $a2 $a3 $a4 $a5 }
 "back" { & $adb shell input keyevent 4 }
 "key" { & $adb shell input keyevent $a1 }
 "text" { & $adb shell input text $a1 }
 "start" { & $adb shell am start -n "com.iptvplayer.streaming.watch.channels.smartiptv/.Activity.SplashActivity" }
 "stop" { & $adb shell am force-stop com.iptvplayer.streaming.watch.channels.smartiptv }
 "sh" { & $adb shell $a1 }
}
