using VideoOS.Platform.DriverFramework;

namespace Safecare.BeiaDeviceDriver_GlassBreak
{
    /// <summary>
    /// Container holding all the different managers.
    /// TODO: If your hardware does not support some of the functionality, you can remove the class and the instantiation below.
    /// </summary>
    public class BeiaDeviceDriver_GlassBreakContainer : Container
    {
        public new BeiaDeviceDriver_GlassBreakConnectionManager ConnectionManager => base.ConnectionManager as BeiaDeviceDriver_GlassBreakConnectionManager;
        public new BeiaDeviceDriver_GlassBreakStreamManager StreamManager => base.StreamManager as BeiaDeviceDriver_GlassBreakStreamManager;

        public BeiaDeviceDriver_GlassBreakContainer(DriverDefinition definition)
            : base(definition)
        {
            base.StreamManager = new BeiaDeviceDriver_GlassBreakStreamManager(this);
            base.PtzManager = new BeiaDeviceDriver_GlassBreakPtzManager(this);
            base.OutputManager = new BeiaDeviceDriver_GlassBreakOutputManager(this);
            base.SpeakerManager = new BeiaDeviceDriver_GlassBreakSpeakerManager(this);
            base.PlaybackManager = new BeiaDeviceDriver_GlassBreakPlaybackManager(this);
            base.ConnectionManager = new BeiaDeviceDriver_GlassBreakConnectionManager(this);
            base.ConfigurationManager = new BeiaDeviceDriver_GlassBreakConfigurationManager(this);
        }
    }
}
