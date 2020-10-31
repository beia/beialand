using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Security;
using System.Text;
using uPLibrary.Networking.M2Mqtt;
using uPLibrary.Networking.M2Mqtt.Messages;
using VideoOS.Platform.DriverFramework.Data.Settings;
using VideoOS.Platform.DriverFramework.Managers;


namespace Safecare.BeiaDeviceDriver_Sound
{
    /// <summary>
    /// Class handling connection to one hardware
    /// </summary>
    public class BeiaDeviceDriver_SoundConnectionManager : ConnectionManager
    {
        private InputPoller _inputPoller;

        private Uri _uri;
        private bool _connected = false;
        private MqttClient _client;
        private readonly DeviceMessageHandler _messageHandler = new DeviceMessageHandler();

        public ThermometerData CurrentData => _messageHandler.Data;

        public string FakeMacAddress { get; private set; }

        public BeiaDeviceDriver_SoundConnectionManager(BeiaDeviceDriver_SoundContainer container) : base(container)
        {
        }

        static String SecureStringToString(SecureString value)
        {
            IntPtr valuePtr = IntPtr.Zero;
            try
            {
                valuePtr = Marshal.SecureStringToGlobalAllocUnicode(value);
                return Marshal.PtrToStringUni(valuePtr);
            }
            finally
            {
                Marshal.ZeroFreeGlobalAllocUnicode(valuePtr);
            }
        }

        /// <summary>
        /// Implementation of the DFW platform method.
        /// </summary>
        public override void Connect(Uri uri, string userName, SecureString password, ICollection<HardwareSetting> hardwareSettings)
        {
            LogUtils.LogDebug($"Connecting to {uri}. User: {userName}.");

            if (_connected)
            {
                return;
            }

            _uri = uri;

            FakeMacAddress = MakeFakeMacAddressFromUri(uri);

            // Establish connection
            _client = new MqttClient("mqtt.beia-telemetrie.ro");
            byte connectCode = _client.Connect(string.Empty, userName, SecureStringToString(password));
            if (connectCode == 0)
                _connected = true;
            else
            {
                _connected = false;
                LogUtils.LogError("MQTT connection failed. Error: " + connectCode, "ConnectionManager/Connect");
                return;
            }

            _client.MqttMsgPublishReceived -= MsgReceived;
            _client.MqttMsgPublishReceived += MsgReceived;

            _client.Subscribe(new[] {"odsi/mari-anais"}, new[] {MqttMsgBase.QOS_LEVEL_AT_LEAST_ONCE});

            // polling for events from the device. Might not be needed if the event mechanism of your hardware is not poll based
            _inputPoller = new InputPoller(Container.EventManager, this, _messageHandler);
            _inputPoller.Start();
        }

        public void MsgReceived(object sender, MqttMsgPublishEventArgs e)
        {
            // Handle message received
            var message = Encoding.Default.GetString(e.Message);
            _messageHandler.Update(message);
            LogUtils.LogDebug("MQTT message received. Length  " + message.Length);
        }

        /// <summary>
        /// Implementation of the DFW platform method.
        /// </summary>
        public override void Close()
        {
            _inputPoller?.Stop();
            _client?.Disconnect();

            _connected = false;
        }

        /// <summary>
        /// Implementation of the DFW platform property.
        /// </summary>
        public override bool IsConnected
        {
            get { return _connected && _inputPoller != null && _inputPoller.ReadyToRun; }
        }

        public byte[] PopFrame()
        {
            return _inputPoller.PopFrame();
        }

        private static string MakeFakeMacAddressFromUri(Uri uri)
        {
            const string defaultValue = "00";
            string hostName = uri.IsLoopback ? "127.0.0.1" : uri.DnsSafeHost;

            var ipSegments = hostName.Split('.');
            var macAddress = new string[6];
            for (int i = 0; i < 4; i++)
            {
                if (i > ipSegments.Length - 1)
                    macAddress[i] = defaultValue;

                if (!int.TryParse(ipSegments[i], out int value))
                    macAddress[i] = defaultValue; // Fallback value

                macAddress[i] = (value % (byte.MaxValue + 1)).ToString("X2");
            }

            var portHexString = (uri.Port % (ushort.MaxValue + 1)).ToString("X2");

            macAddress[4] = portHexString.Substring(0, 2);

            if (portHexString.Length > 2)
                macAddress[5] = portHexString.Substring(2, 2);
            else
                macAddress[5] = defaultValue;

            return string.Join("-", macAddress);
        }
    }
}

