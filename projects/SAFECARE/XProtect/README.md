# IoTDevices

This folder contains the XProtect drivers written for the IoT devices.

The orginal repository can be found at [zenjieli/IoTDevices](https://github.com/zenjieli/IoTDevices).

## Deployment

Run CopyDlls.bat or CopyDllsRelease.bat in admin mode.

## Debug

* Run Visual Studio admin mode
* Attach to VideoOS.IO.DriverFrameworkProcess.exe from Visual Studio
 
## Logging
  
The logs are saved in ```C:\ProgramData\Milestone\XProtect Recording Server\Logs\DriverFramework_BeiaDeviceDriver.log```.

## Config MQTT topic

1. Add hardware to Recording Servers in Management Client
2. Change *MQTT topic* in device settings
3. Uncheck *Enabled* to disconnect the device
4. Check *Enabled* to connect the device again
