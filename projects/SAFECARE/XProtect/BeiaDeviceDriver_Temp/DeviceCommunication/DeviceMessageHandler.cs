using System;

namespace Safecare.BeiaDeviceDriver_Temp
{
    internal class DeviceMessageHandler
    {
        public ThermometerData Data { get; private set; }

        public bool Initialized => Data != null;

        public void Update(string newBuffer)
        {
            string message = newBuffer.Replace("\\", ""); ;

            try
            {
                Data = ThermometerData.Deserialize(message);
                Data.SetDateTimeIfEmpty();
            }
            catch (Exception ex)
            {
                Data = null;
                LogUtils.LogError("Thermometer value parsing failed. \r\n" + ex, "DeviceMessageContainer/Update");
            }
        }
    }
}
