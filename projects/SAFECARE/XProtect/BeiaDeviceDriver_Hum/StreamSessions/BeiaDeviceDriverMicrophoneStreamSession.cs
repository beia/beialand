using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver_Hum
{
    /// <summary>
    /// Class for working with one microphone audio stream session
    /// TODO: Implement request for fetching audio data
    /// </summary>
    internal class BeiaDeviceDriver_HumMicrophoneStreamSession : BaseBeiaDeviceDriver_HumStreamSession
    {
        // TODO: Update this to reflect the data type returned by your device.
        private AudioHeader _currentAudioHeader = new AudioHeader
        {
            SequenceNumber = 0,
            CodecType = AudioCodecType.PCM,
            CodecSubtype = 0,
            Timestamp = DateTime.Now,
            ChannelCount = 1,
            BitsPerSample = 16,
            Frequency = 16000,
            SampleCount = 0
        };

        public BeiaDeviceDriver_HumMicrophoneStreamSession(ISettingsManager settingsManager, BeiaDeviceDriver_HumConnectionManager connectionManager, Guid sessionId, string deviceId, Guid streamId) :
             base(settingsManager, connectionManager, sessionId, deviceId, streamId)
        {
            // TODO: Set Channel to correct channel number
            Channel = 1;
        }

        protected override bool GetLiveFrameInternal(TimeSpan timeout, out BaseDataHeader header, out byte[] data)
        {
            header = null;
            data = null;
            byte[] frame = null;

            // TODO: Implement request for fetching frame data from device

            if (frame == null)
            {
                return false;
            }
            _currentAudioHeader.Length = (ulong)frame.Length;
            _currentAudioHeader.Timestamp = DateTime.UtcNow;
            _currentAudioHeader.SequenceNumber++;
            _currentAudioHeader.SampleCount = frame.Length / 2; // Assume 16 bits per sample
            header = _currentAudioHeader.Clone();
            data = frame;
            return true;
        }
    }
}
