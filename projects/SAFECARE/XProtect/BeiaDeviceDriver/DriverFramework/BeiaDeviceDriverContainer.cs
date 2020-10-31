using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriverContainer : Container
    {
        public new BeiaDeviceDriverConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriverConnectionManager;
        public new BeiaDeviceDriverStreamManager StreamManager => base.StreamManager as BeiaDeviceDriverStreamManager;

        public BeiaDeviceDriverContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriverStreamManager(this);
            base.PtzManager = new BeiaDeviceDriverPtzManager(this);
            base.OutputManager = new BeiaDeviceDriverOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriverSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriverPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriverConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriverConfigurationManager(this);
        }
    }
}
