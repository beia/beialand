using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver_Hum
{
    /// <summary>
    /// Class for working with one video stream session
    /// </summary>
    internal class BeiaDeviceDriver_HumVideoStreamSession : BaseBeiaDeviceDriver_HumStreamSession
    {

        public BeiaDeviceDriver_HumVideoStreamSession(ISettingsManager settingsManager, BeiaDeviceDriver_HumConnectionManager connectionManager, Guid sessionId, string deviceId, Guid streamId) :
            base(settingsManager, connectionManager, sessionId, deviceId, streamId)
        {
            Channel = 1;
        }

        protected override bool GetLiveFrameInternal(TimeSpan timeout, out BaseDataHeader header, out byte[] data)
        {
            header = null;
            data = _connectionManager.PopFrame();

            if (data == null || data.Length == 0)
            {
                return false;
            }
            DateTime dt = DateTime.UtcNow;

            header = new VideoHeader
            {
                CodecType = VideoCodecType.JPEG,
                Length = (ulong)data.Length,
                SequenceNumber = _sequence++,
                SyncFrame = true,
                TimestampSync = dt,
                TimestampFrame = dt
            };
            return true;
        }
    }
}
