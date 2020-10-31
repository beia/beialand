using System;
using System.Collections.Generic;
using System.Linq;
using VideoOS.Platform.DriverFramework.Definitions;
using VideoOS.Platform.DriverFramework.Exceptions;
using VideoOS.Platform.DriverFramework.Managers;

namespace Safecare.BeiaDeviceDriver
{
    /// <summary>
    /// This class returns information about the hardware including capabilities and settings supported.
    /// TODO: Update it to match what is supported by your hardware.
    /// </summary>
    public class BeiaDeviceDriverConfigurationManager : ConfigurationManager
    {
        private const string _firmware = "BeiaDeviceDriver Firmware";
        private const string _firmwareVersion = "1.0";
        private const string _hardwareName = "BeiaDeviceDriver Hardware";
        private const string _serialNumber = "12345";

        private new BeiaDeviceDriverContainer Container => base.Container as BeiaDeviceDriverContainer;

        public BeiaDeviceDriverConfigurationManager(BeiaDeviceDriverContainer container) : base(container)
        {
        }

        protected override ProductInformation FetchProductInformation()
        {
            if (!Container.ConnectionManager.IsConnected)
            {
                throw new ConnectionLostException("Connection not established");
            }

            var driverInfo = Container.Definition.DriverInfo;
            var product = driverInfo.SupportedProducts.FirstOrDefault();
            //var macAddress = "DE:AD:C0:DE:56:78"; // TODO: Make request to hardware
            var macAddress = Container.ConnectionManager.FakeMacAddress;
            LogUtils.LogDebug("Mac address " + macAddress, "FetchProductInformation");


            return new ProductInformation
            {
                ProductId = product.Id,
                ProductName = product.Name,
                ProductVersion = driverInfo.Version,
                MacAddress = macAddress,
                FirmwareVersion = _firmwareVersion,
                Firmware = _firmware,
                HardwareName = _hardwareName,
                SerialNumber = _serialNumber
            };
        }

        protected override IDictionary<string, string> BuildHardwareSettings()
        {
            return new Dictionary<string, string>
            {
                { Constants.MqttTopic, "odsi/mari-anais" }
            };
        }

        protected override ICollection<ISetupField> BuildFields()
        {
            var fields = new List<ISetupField>
            {
                new StringSetupField
                {
                    Key = Constants.MqttTopic,
                    DisplayName = "MQTT topic",
                    DisplayNameReferenceId = Guid.Empty,
                    IsReadOnly = false,
                    ReferenceId = Constants.MqttTopicRefId
                }
            };

            return fields;
        }

        protected override ICollection<EventDefinition> BuildHardwareEvents()
        {
            var hardwareEvents = new List<EventDefinition>();

            // TODO: Add supported hardware level events

            return hardwareEvents;
        }

        protected override ICollection<DeviceDefinitionBase> BuildDevices()
        {
            var devices = new List<DeviceDefinitionBase>();

            devices.Add(new CameraDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver camera",
                DeviceId = Constants.Video1.ToString(),
                DeviceEvents = BuildDeviceEvents(),
                Settings = new Dictionary<string, string>()
                {
                    // TODO: Add settings supported by the device - also for the other devices below.
                },
                Streams = BuildCameraStreams(),
                // Leave PtzSupport set to null if PTZ is not supported
                PtzSupport = BuildPtzSupport(),
            });

            // TODO: If supported by the hardware, add more camera devices (same for below device types). Also remove the devices not supported.

            devices.Add(new MetadataDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver metadata device",
                DeviceId = Constants.Metadata1.ToString(),
                Streams = BuildMetadataStreams(),
            });

            devices.Add(new MicrophoneDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver microphone",
                DeviceId = Constants.Audio1.ToString(),
                Streams = BuildAudioStream(),
            });

            devices.Add(new OutputDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver output",
                DeviceId = Constants.Output1.ToString(),
                SupportSetState = true,
                SupportTrigger = true,
            });

            devices.Add(new InputDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver input",
                DeviceId = Constants.Input1.ToString(),
            });

            devices.Add(new SpeakerDeviceDefinition()
            {
                DisplayName = "BeiaDeviceDriver speaker",
                DeviceId = Constants.Speaker1.ToString(),
                Streams = BuildSpeakerStream()
            });

            return devices;
        }

        private static ICollection<StreamDefinition> BuildCameraStreams()
        {
            ICollection<StreamDefinition> streams = new List<StreamDefinition>();
            streams.Add(new StreamDefinition()
            {
                DisplayName = "BeiaDeviceDriver video stream",
                ReferenceId = Constants.VideoStream1RefId.ToString(),
                Settings = new Dictionary<string, string>()
                {
                    // TODO: Add settings supported by the stream
                },
                RemotePlaybackSupport = true,
            });

            return streams;
        }

        private static ICollection<StreamDefinition> BuildAudioStream()
        {
            ICollection<StreamDefinition> streams = new List<StreamDefinition>();
            streams.Add(new StreamDefinition()
            {
                DisplayName = "BeiaDeviceDriver audio stream",
                ReferenceId = Constants.AudioStream1RefId.ToString(),
                Settings = new Dictionary<string, string>()
                {
                    // TODO: Add settings supported by the stream
                },
            });
            return streams;
        }

        private static ICollection<StreamDefinition> BuildSpeakerStream()
        {
            ICollection<StreamDefinition> streams = new List<StreamDefinition>();
            streams.Add(new StreamDefinition()
            {
                DisplayName = "BeiaDeviceDriver speaker stream",
                ReferenceId = Constants.SpeakerStream1RefId.ToString(),
                Settings = new Dictionary<string, string>()
                {
                    // TODO: Add settings supported by the stream
                },
            });
            return streams;
        }

        private static ICollection<StreamDefinition> BuildMetadataStreams()
        {
            ICollection<StreamDefinition> streams = new List<StreamDefinition>();
            streams.Add(new StreamDefinition()
            {
                DisplayName = "BeiaDeviceDriver metadata stream",
                ReferenceId = MetadataType.BoundingBoxDisplayId.ToString(), // TODO: Potentially change this to one of the other supported meatadata stream types
                MetadataTypes = new List<MetadataTypeDefinition>()
                {
                    // TODO: Add metadata types
                }
            });
            return streams;
        }

        private static ICollection<EventDefinition> BuildDeviceEvents()
        {
            var deviceEvents = new List<EventDefinition>();

            // TODO: Add events supported by device.
            return deviceEvents;
        }

        private static PtzSupport BuildPtzSupport()
        {
            // TODO: Update below to reflect actual PTZ support.

            PtzMoveSupport moveSupport = new PtzMoveSupport()
            {
                AbsoluteSupport = true,
                AutomaticSupport = false,
                RelativeSupport = true,
                SpeedSupport = true,
                StartSupport = true,
                StopSupport = true,
            };

            PtzMoveSupport moveSupportZoom = new PtzMoveSupport()
            {
                AbsoluteSupport = true,
                AutomaticSupport = false,
                RelativeSupport = true,
                SpeedSupport = false,
                StartSupport = true,
                StopSupport = true,
            };

            PresetSupport presetSupport = new PresetSupport()
            {
                AbsoluteSpeedSupport = false,
                LoadFromDeviceSupport = true,
                QueryAbsolutePositionSupport = true,
                SetPresetSupport = true,
                SpeedSupport = true,
            };

            PtzSupport ptzSupport = new PtzSupport()
            {
                CenterSupport = true,
                DiagonalSupport = true,
                HomeSupport = true,
                RectangleSupport = true,
                PanSupport = moveSupport,
                TiltSupport = moveSupport,
                ZoomSupport = moveSupportZoom,
                PresetSupport = presetSupport,
            };

            return ptzSupport;
        }
    }
}
