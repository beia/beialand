using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_Movement
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_MovementContainer : Container
    {
        public new BeiaDeviceDriver_MovementConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_MovementConnectionManager;
        public new BeiaDeviceDriver_MovementStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_MovementStreamManager;

        public BeiaDeviceDriver_MovementContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_MovementStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_MovementPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_MovementOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_MovementSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_MovementPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_MovementConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_MovementConfigurationManager(this);
        }
    }
}
