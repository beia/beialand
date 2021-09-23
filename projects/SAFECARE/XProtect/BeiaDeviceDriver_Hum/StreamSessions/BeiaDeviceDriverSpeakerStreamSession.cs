using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver_Hum
{
    /// <summary>
    /// Class for working with one speaker audio stream session
    /// </summary>
    internal class BeiaDeviceDriver_HumSpeakerStreamSession : BaseBeiaDeviceDriver_HumStreamSession
    {
        private byte[] _currentSpeakerData = null;
        private AudioHeader _currentSpeakerHeader = null;

        public BeiaDeviceDriver_HumSpeakerStreamSession(ISettingsManager settingsManager, BeiaDeviceDriver_HumConnectionManager connectionManager, Guid sessionId, string deviceId, Guid streamId) :
             base(settingsManager, connectionManager, sessionId, deviceId, streamId)
        {
            // TODO: Set Channel to correct channel number
            Channel = 1;
        }

        protected override bool GetLiveFrameInternal(TimeSpan timeout, out BaseDataHeader header, out byte[] data)
        {
            header = null;
            data = null;
            if (_currentSpeakerData != null && _currentSpeakerHeader != null)
            {
                header = _currentSpeakerHeader.Clone();
                data = _currentSpeakerData;
                _currentSpeakerData = null;
                return true;
            }
            return false;
        }

        public void StoreFrameForLoopback(AudioHeader ah, byte[] data)
        {
            _currentSpeakerHeader = ah;
            _currentSpeakerData = data;
        }
    }
}
