using System;
using System.Collections.Generic;
using VideoOS.Platform.DriverFramework.Data.Ptz;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver_Light
{
    /// <summary>
    /// Class implementing PTZ support.
    /// TODO: You should implement requests to your device, or if PTZ is not supported, remove the class entirely.
    /// </summary>
    public class BeiaDeviceDriver_LightPtzManager : PtzManager
    {
        private new BeiaDeviceDriver_LightContainer Container => base.Container as BeiaDeviceDriver_LightContainer;

        public BeiaDeviceDriver_LightPtzManager(BeiaDeviceDriver_LightContainer container) : base(container)
        {
        }

        public override ICollection<string> GetPresets(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Video1)
                return new string[] { }; // TODO: If the device supports presets make request to get the list, otherwise remove the method
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void ActivatePreset(string deviceId, string presetId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void AddPreset(string deviceId, string presetId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void UpdatePreset(string deviceId, string presetId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void DeletePreset(string deviceId, string presetId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void MoveStart(string deviceId, PtzMoveStartData ptzargs)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void MoveStop(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void MoveAbsolute(string deviceId, PtzMoveAbsoluteData ptzargs)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void MoveRelative(string deviceId, string direction)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void MoveHome(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void CenterAndZoomToRectangle(string deviceId, PtzRectangleData data)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void CenterOnPositionInView(string deviceId, PtzCenterData data)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override PtzGetAbsoluteData GetAbsolutePosition(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make real request
                return new PtzGetAbsoluteData() { Pan = 0.5, Tilt = 0.6, Zoom = 1.0 };
            }
            throw new MIPDriverException("Device does not support PTZ");
        }

        public override void LensCommand(string deviceId, string command)
        {
            if (new Guid(deviceId) == Constants.Video1)
            {
                // TODO: make request
                return;
            }
            throw new MIPDriverException("Device does not support PTZ");
        }
    }
}
