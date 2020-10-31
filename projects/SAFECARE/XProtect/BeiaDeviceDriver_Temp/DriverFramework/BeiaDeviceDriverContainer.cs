using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Temp
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_TempContainer : Container
    {
        public new BeiaDeviceDriver_TempConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_TempConnectionManager;
        public new BeiaDeviceDriver_TempStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_TempStreamManager;

        public BeiaDeviceDriver_TempContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_TempStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_TempPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_TempOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_TempSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_TempPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_TempConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_TempConfigurationManager(this);
        }
    }
}
