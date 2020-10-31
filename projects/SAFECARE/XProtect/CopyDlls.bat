dir /T:W .\BeiaDeviceDriver\bin\Debug\BeiaDeviceDriver.dll
net stop "Milestone XProtect Recording Server"
copy .\BeiaDeviceDriver\bin\Debug\BeiaDeviceDriver.* "C:\Program Files\Milestone\MIPDrivers\BeiaDeviceDriver\" /y
copy .\BeiaDeviceDriver\bin\Debug\M2Mqtt.Net.* "C:\Program Files\Milestone\MIPDrivers\BeiaDeviceDriver\" /y
net start "Milestone XProtect Recording Server"