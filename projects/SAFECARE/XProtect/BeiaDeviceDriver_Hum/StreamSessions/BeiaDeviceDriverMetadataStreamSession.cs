using System;
using System.Text;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver_Hum
{
    /// <summary>
    /// Class for working with one metadata stream session
    /// TODO: Implement request for fetching metadata
    /// </summary>
    internal class BeiaDeviceDriver_HumMetadataStreamSession : BaseBeiaDeviceDriver_HumStreamSession
    {
        public BeiaDeviceDriver_HumMetadataStreamSession(ISettingsManager settingsManager, BeiaDeviceDriver_HumConnectionManager connectionManager, Guid sessionId, string deviceId, Guid streamId, int channel) :
            base(settingsManager, connectionManager, sessionId, deviceId, streamId)
        {
            // TODO: Set Channel to correct channel number
            Channel = 1;
        }

        protected override bool GetLiveFrameInternal(TimeSpan timeout, out BaseDataHeader header, out byte[] data)
        {
            data = null;
            header = null;

            var measuredData = _connectionManager?.CurrentData;
            if (measuredData == null)
                return false;

            data = Encoding.UTF8.GetBytes(measuredData.Serialize());
            if (data == null || data.Length == 0)
            {
                return false;
            }
            header = new MetadataHeader
            {
                Length = (ulong)data.Length,
                SequenceNumber = _sequence++,
                Timestamp = measuredData.Time
            };
            return true;
        }
    }
}
