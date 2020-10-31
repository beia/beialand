using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Hum
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_HumContainer : Container
    {
        public new BeiaDeviceDriver_HumConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_HumConnectionManager;
        public new BeiaDeviceDriver_HumStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_HumStreamManager;

        public BeiaDeviceDriver_HumContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_HumStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_HumPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_HumOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_HumSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_HumPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_HumConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_HumConfigurationManager(this);
        }
    }
}
