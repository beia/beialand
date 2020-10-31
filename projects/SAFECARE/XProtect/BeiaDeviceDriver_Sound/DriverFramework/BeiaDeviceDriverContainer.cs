using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Sound
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_SoundContainer : Container
    {
        public new BeiaDeviceDriver_SoundConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_SoundConnectionManager;
        public new BeiaDeviceDriver_SoundStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_SoundStreamManager;

        public BeiaDeviceDriver_SoundContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_SoundStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_SoundPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_SoundOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_SoundSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_SoundPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_SoundConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_SoundConfigurationManager(this);
        }
    }
}
