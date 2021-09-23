using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Flood
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_FloodContainer : Container
    {
        public new BeiaDeviceDriver_FloodConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_FloodConnectionManager;
        public new BeiaDeviceDriver_FloodStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_FloodStreamManager;

        public BeiaDeviceDriver_FloodContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_FloodStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_FloodPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_FloodOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_FloodSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_FloodPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_FloodConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_FloodConfigurationManager(this);
        }
    }
}
