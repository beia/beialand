using System;
using System.Linq;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Managers;
using VideoOS.Platform.DriverFramework.Utilities;

namespace Safecare.BeiaDeviceDriver_Sound
{
    /// <summary>
    /// Class for overall stream handling.
    /// TODO: Update CreateSession to include the stream types supported by your hardware.
    /// </summary>
    public class BeiaDeviceDriver_SoundStreamManager : SessionEnabledStreamManager
    {
        private new BeiaDeviceDriver_SoundContainer Container => base.Container as BeiaDeviceDriver_SoundContainer;

        public BeiaDeviceDriver_SoundStreamManager(BeiaDeviceDriver_SoundContainer container) : base(container)
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
                Toolbox.Log.Trace("BeiaDeviceDriver_Sound.StreamManager.GetLiveFrame: Exception={0}", ex.Message);
                return GetLiveFrameResult.ErrorResult(StreamLiveStatus.NoConnection);
            }
        }

        internal BaseStreamSession GetSession(int channel)
        {
            return GetAllSessions().OfType<BaseBeiaDeviceDriver_SoundStreamSession>()
                .FirstOrDefault(s => s.Channel == channel);
        }

        protected override BaseStreamSession CreateSession(string deviceId, Guid streamId, Guid sessionId)
        {
            Guid dev = new Guid(deviceId);
            // TODO: Modify below to reflect the streams supported by your device
            if (dev == Constants.Video1)
            {
                return new BeiaDeviceDriver_SoundVideoStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Speaker1)
            {
                return new BeiaDeviceDriver_SoundSpeakerStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Audio1)
            {
                return new BeiaDeviceDriver_SoundMicrophoneStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId);
            }
            if (dev == Constants.Metadata1)
            {
                return new BeiaDeviceDriver_SoundMetadataStreamSession(Container.SettingsManager, Container.ConnectionManager, sessionId, deviceId, streamId, 1 /* TODO: replace with correct channel ID */);
            }

            Toolbox.Log.LogError("This device ID: {0} is not supported", deviceId);
            throw new MIPDriverException();
        }
    }
}
