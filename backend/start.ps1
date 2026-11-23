# Levanta mongod portable (si no corre) y luego el servidor Node.
$root = "C:\Users\pc\mongodata"
$mongod = (Get-ChildItem "$root\bin\*\bin\mongod.exe" | Select-Object -First 1).FullName
if (-not (Get-NetTCPConnection -LocalPort 27017 -State Listen -ErrorAction SilentlyContinue)) {
  Start-Process $mongod -ArgumentList "--dbpath `"$root\db`"","--bind_ip 127.0.0.1","--port 27017","--logpath `"$root\mongod.log`"" -WindowStyle Hidden
  Start-Sleep -Seconds 4
}
Set-Location $PSScriptRoot
if (-not (Test-Path node_modules)) { npm install }
npm start
