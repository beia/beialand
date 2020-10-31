dir /T:W .\BeiaDeviceDriver\bin\Release\BeiaDeviceDriver.dll
net stop "Milestone XProtect Recording Server"
copy .\BeiaDeviceDriver\bin\Release\BeiaDeviceDriver.* "C:\Program Files\Milestone\MIPDrivers\BeiaDeviceDriver\" /y
copy .\BeiaDeviceDriver\bin\Debug\M2Mqtt.Net.dll "C:\Program Files\Milestone\MIPDrivers\BeiaDeviceDriver\" /y
net start "Milestone XProtect Recording Server"