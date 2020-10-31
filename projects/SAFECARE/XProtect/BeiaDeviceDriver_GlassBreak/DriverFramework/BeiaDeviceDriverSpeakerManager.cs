using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;
using VideoOS.Platform.DriverFramework.Utilities;

namespace Safecare.BeiaDeviceDriver_GlassBreak
{
    /// <summary>
    /// Class for sending audio to a speaker
    /// TODO: Implement using the appropriate request for sending audio, or remove the class entirely if speaker is not supported.
    /// </summary>
    public class BeiaDeviceDriver_GlassBreakSpeakerManager : SpeakerManager
    {
        private Guid _streamId;

        private new BeiaDeviceDriver_GlassBreakContainer Container => base.Container as BeiaDeviceDriver_GlassBreakContainer;

        internal BeiaDeviceDriver_GlassBreakSpeakerManager(BeiaDeviceDriver_GlassBreakContainer container) : base(container)
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

            BeiaDeviceDriver_GlassBreakSpeakerStreamSession s = Container.StreamManager.GetSession(1 /* TODO: Specify correct channel numer */) as BeiaDeviceDriver_GlassBreakSpeakerStreamSession;
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
