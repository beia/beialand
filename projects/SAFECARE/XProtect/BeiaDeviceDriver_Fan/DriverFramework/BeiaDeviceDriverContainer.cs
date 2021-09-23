using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Fan
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_FanContainer : Container
    {
        public new BeiaDeviceDriver_FanConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_FanConnectionManager;
        public new BeiaDeviceDriver_FanStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_FanStreamManager;

        public BeiaDeviceDriver_FanContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_FanStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_FanPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_FanOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_FanSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_FanPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_FanConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_FanConfigurationManager(this);
        }
    }
}
