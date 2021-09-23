using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Light
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_LightContainer : Container
    {
        public new BeiaDeviceDriver_LightConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_LightConnectionManager;
        public new BeiaDeviceDriver_LightStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_LightStreamManager;

        public BeiaDeviceDriver_LightContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_LightStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_LightPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_LightOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_LightSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_LightPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_LightConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_LightConfigurationManager(this);
        }
    }
}
