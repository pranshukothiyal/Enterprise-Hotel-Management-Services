$env:HEADED = "1"
$env:PWDEBUG = "1"

dotnet test --settings .runsettings -- --filter-query "/*/*/Frontend*"
