using System;
using System.Linq;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Managers;
using VideoOS.Platform.DriverFramework.Utilities;

namespace Safecare.BeiaDeviceDriver_Fire
{
    /// <summary>
    /// Class for overall stream handling.
    /// TODO: Update CreateSession to include the stream types supported by your hardware.
    /// </summary>
    public class BeiaDeviceDriver_FireStreamManager : SessionEnabledStreamManager
    {
        private new BeiaDeviceDriver_FireContainer Container => base.Container as BeiaDeviceDriver_FireContainer;

        public BeiaDeviceDriver_FireStreamManager(BeiaDeviceDriver_FireContainer container) : base(container)
        {
        }

        public override GetLiveFrameResult GetLiveFrame(Guid sessionId, TimeSpan timeout)
        {
            try
            {
                return base.GetLiveFrame(sessionId, timeout);
            }
            catch (System.ServiceModel.CommunicationException ex)
            {
                Toolbox.Log.Trace("BeiaDeviceDriver_Fire.StreamManager.GetLiveFrame: Exception={0}", ex.Message);
                return GetLiveFrameResult.ErrorResult(StreamLiveStatus.NoConnection);
            }
        }

        internal BaseStreamSession GetSession(int channel)
        {
            return GetAllSessions().OfType<BaseBeiaDeviceDriver_FireStreamSession>()
                .FirstOrDefault(s => s.Channel == channel);
        }

        protected override BaseStreamSession CreateSession(string deviceId, Guid streamId, Guid sessionId)
        {
            Guid dev = new Guid(deviceId);
            // TODO: Modify below to reflect the streams supported by your device
            if (dev == Constants.Video1)
            {
                return new BeiaDeviceDriver_FireVideoStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Speaker1)
            {
                return new BeiaDeviceDriver_FireSpeakerStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Audio1)
            {
                return new BeiaDeviceDriver_FireMicrophoneStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Metadata1)
            {
                return new BeiaDeviceDriver_FireMetadataStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId, 1 /* TODO: replace with correct channel ID */);
            }

            Toolbox.Log.LogError("This device ID: {0} is not supported", deviceId);
            throw new MIPDriverException();
        }
    }
}
