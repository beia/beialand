using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;
using VideoOS.Platform.DriverFramework.Utilities;

namespace Safecare.BeiaDeviceDriver
{
    /// <summary>
    /// Class for sending audio to a speaker
    /// TODO: Implement using the appropriate request for sending audio, or remove the class entirely if speaker is not supported.
    /// </summary>
    public class BeiaDeviceDriverSpeakerManager : SpeakerManager
    {
        private Guid _streamId;

        private new BeiaDeviceDriverContainer Container => base.Container as BeiaDeviceDriverContainer;

        internal BeiaDeviceDriverSpeakerManager(BeiaDeviceDriverContainer container) : base(container)
        {
        }

        public override Guid CreateSpeakerStream(string deviceId)
        {
            _streamId = Guid.NewGuid();
            return _streamId;
        }

        public override SpeakerStreamStatus SendFrame(Guid speakerStreamInstance, AudioHeader audioHeader, byte[] data)
        {
            Toolbox.Log.Trace("Speaker header: {0}", audioHeader);

            BeiaDeviceDriverSpeakerStreamSession s = Container.StreamManager.GetSession(1 /* TODO: Specify correct channel numer */) as BeiaDeviceDriverSpeakerStreamSession;
            if (s != null)
            {
                s.StoreFrameForLoopback(audioHeader, data);
            }

            // TODO: Make request to device for sending data to the speaker

            return SpeakerStreamStatus.DataSent;
        }

        public override void Destroy(Guid speakerStreamInstance)
        {
            _streamId = Guid.Empty;
        }
    }
}
