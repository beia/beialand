
using System;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Utilities;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver
{
    /// <summary>
    /// Base stream session class. Specialized in other classes for specific devices.
    /// </summary>
    internal abstract class BaseBeiaDeviceDriverStreamSession : BaseStreamSession
    {
        public Guid Id { get; }

        public int Channel { get; protected set; }

        protected readonly string _deviceId;
        protected readonly Guid _streamId;

        protected readonly BeiaDeviceDriverConnectionManager _connectionManager;
        protected readonly ISettingsManager _settingsManager;

        protected int _sequence = 0;

        protected abstract bool GetLiveFrameInternal(TimeSpan timeout, out BaseDataHeader header, out byte[] data);

        public BaseBeiaDeviceDriverStreamSession(ISettingsManager settingsManager, BeiaDeviceDriverConnectionManager connectionManager, Guid sessionId, string deviceId, Guid streamId)
        {
            Id = sessionId;
            _settingsManager = settingsManager;
            _connectionManager = connectionManager;
            _deviceId = deviceId;
            _streamId = streamId;
            try
            {
                // TODO: Make request for starting live stream
            }
            catch (Exception ex)
            {
                throw new ConnectionLostException(ex.Message + ex.StackTrace);
            }
        }

        public sealed override bool GetLiveFrame(TimeSpan timeout, out BaseDataHeader header, out byte[] data)
        {
            try
            {
                return GetLiveFrameInternal(timeout, out header, out data);
            }
            catch (Exception ex)
            {
                Toolbox.Log.LogError(GetType().Name,
                    "{0}, Channel {1}: {2}", nameof(GetLiveFrame), Channel, ex.Message + ex.StackTrace);
                throw new ConnectionLostException(ex.Message + ex.StackTrace);
            }
        }

        public override void Close()
        {
            try
            {
                _sequence = 0;
                // TODO: Make request for stopping live stream
            }
            catch (Exception ex)
            {
                Toolbox.Log.LogError(this.GetType().Name, "Error calling Destroy: {0}", ex.Message);
            }
        }
    }
}
