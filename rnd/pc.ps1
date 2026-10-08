param([string]$id,[string]$then="")
# show player controls then tap element by resource id (retries until visible)
$adb="$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
for($i=0;$i -lt 4;$i++){
  & $adb shell input tap 540 400 | Out-Null
  & $adb shell uiautomator dump /sdcard/ui.xml | Out-Null
  & $adb pull /sdcard/ui.xml "$PSScriptRoot\ui.xml" | Out-Null
  [xml]$x=Get-Content "$PSScriptRoot\ui.xml" -Encoding UTF8
  $n=$x.SelectNodes("//node") | ? { $_.'resource-id' -like "*:id/$id" } | Select-Object -First 1
  if($n -and $n.bounds -match '\[(\d+),(\d+)\]\[(\d+),(\d+)\]'){
    $cx=[int](([int]$Matches[1]+[int]$Matches[3])/2); $cy=[int](([int]$Matches[2]+[int]$Matches[4])/2)
    if($cy -lt 1000){ & $adb shell input tap $cx $cy | Out-Null; "tapped $id at $cx,$cy"; return }
  }
}
"element $id not found/visible"
