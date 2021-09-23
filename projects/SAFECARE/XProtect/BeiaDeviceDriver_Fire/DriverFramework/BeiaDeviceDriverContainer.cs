using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Fire
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_FireContainer : Container
    {
        public new BeiaDeviceDriver_FireConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_FireConnectionManager;
        public new BeiaDeviceDriver_FireStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_FireStreamManager;

        public BeiaDeviceDriver_FireContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_FireStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_FirePtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_FireOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_FireSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_FirePlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_FireConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_FireConfigurationManager(this);
        }
    }
}
