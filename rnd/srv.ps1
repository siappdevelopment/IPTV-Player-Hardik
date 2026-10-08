$l = New-Object System.Net.HttpListener
$l.Prefixes.Add("http://127.0.0.1:8000/")
$l.Start()
$root = Join-Path $PSScriptRoot "srv"
while ($l.IsListening) {
  $c = $l.GetContext()
  $name = $c.Request.Url.AbsolutePath.TrimStart('/')
  $f = Join-Path $root $name
  if ($name -eq 'slow.m3u') { Start-Sleep 20; $f = Join-Path $root 'test.m3u' }
  if ($name -eq 'forbidden.m3u') { $c.Response.StatusCode = 403; $c.Response.Close(); continue }
  if (Test-Path $f -PathType Leaf) {
    $b = [IO.File]::ReadAllBytes($f); $c.Response.ContentType = "audio/x-mpegurl"
    $c.Response.OutputStream.Write($b,0,$b.Length)
  } else { $c.Response.StatusCode = 404 }
  $c.Response.Close()
}
