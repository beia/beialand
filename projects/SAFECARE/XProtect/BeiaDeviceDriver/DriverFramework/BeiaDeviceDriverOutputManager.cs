using System;
using System.Collections.Generic;
using System.Threading;
using VideoOS.Platform.DriverFramework.Definitions;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver
{
    /// <summary>
    /// Class for triggering outputs.
    /// TODO: Implement, or remove the class if not supported by the device.
    /// </summary>
    public class BeiaDeviceDriverOutputManager : OutputManager
    {
        private readonly HashSet<TriggerTimerMap> _triggerTimers = new HashSet<TriggerTimerMap>();

        private new BeiaDeviceDriverContainer Container => base.Container as BeiaDeviceDriverContainer;

        public BeiaDeviceDriverOutputManager(BeiaDeviceDriverContainer container) : base(container)
        {
        }

        public override bool? IsActivated(string deviceId)
        {
            // TODO: If supported make request to device
            return null;
        }

        public override void TriggerOutput(string deviceId, int durationMs)
        {
            if (new Guid(deviceId) == Constants.Output1)
            {
                ActivateOutput(deviceId);

                Container.EventManager.NewEvent(deviceId, EventId.OutputActivated);

                TriggerTimerMap map = new TriggerTimerMap()
                {
                    DeviceId = deviceId
                };
                map.TriggerTimer = new Timer(TimerCallback, map, durationMs, Timeout.Infinite);
                _triggerTimers.Add(map);
                return;
            }
            throw new MIPDriverException("Device does not support Output commands");
        }

        public override void ActivateOutput(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Output1)
            {
                // TODO: make request to device
                Container.EventManager.NewEvent(deviceId, EventId.OutputActivated);
                return;
            }
            throw new MIPDriverException("Device does not support Output commands");
        }

        public override void DeactivateOutput(string deviceId)
        {
            if (new Guid(deviceId) == Constants.Output1)
            {
                // TODO: make request to device
                Container.EventManager.NewEvent(deviceId, EventId.OutputDeactivated);
                return;
            }
            throw new MIPDriverException("Device does not support Output commands");
        }

        private void TimerCallback(object state)
        {
            TriggerTimerMap map = state as TriggerTimerMap;
            if (map == null) throw new Exception("Map state unknown");

            DeactivateOutput(map.DeviceId);
            _triggerTimers.Remove(map);
            map.TriggerTimer.Dispose();
        }
    }

    internal class TriggerTimerMap
    {
        internal Timer TriggerTimer;
        internal string DeviceId;
    }
}
