using System;
using System.Collections.Generic;
using VideoOS.Platform.DriverFramework.Data;
using VideoOS.Platform.DriverFramework.Managers;
using VideoOS.Platform.DriverFramework.Utilities;


namespace Safecare.BeiaDeviceDriver_Fan
{
    /// <summary>
    /// This class should implement support for requesting stored data from the device.
    /// TODO: If this is not supported, remove the class.
    /// </summary>
    public class BeiaDeviceDriver_FanPlaybackManager : PlaybackManager
    {
        private readonly object _playbackLockObj = new object();
        private readonly Dictionary<Guid, DateTime> _playbackCursors = new Dictionary<Guid, DateTime>();
        private readonly Dictionary<Guid, int> _sequenceNumbers = new Dictionary<Guid, int>();

        private new BeiaDeviceDriver_FanContainer Container => base.Container as BeiaDeviceDriver_FanContainer;

        public BeiaDeviceDriver_FanPlaybackManager(BeiaDeviceDriver_FanContainer container) : base(container)
        {
            base.MaxParallelDevices = 2;
        }

        public override Guid Create(string deviceId)
        {
            if (deviceId != Constants.Video1.ToString())
            {
                throw new NotSupportedException();
            }
            lock (_playbackLockObj)
            {
                Guid id = Guid.NewGuid();
                _playbackCursors[id] = DateTime.UtcNow;
                _sequenceNumbers[id] = 0;
                return id;
            }
        }

        public override void Destroy(Guid playbackId)
        {
            lock (_playbackLockObj)
            {
                if (_playbackCursors.ContainsKey(playbackId))
                {
                    _playbackCursors.Remove(playbackId);
                    _sequenceNumbers.Remove(playbackId);
                }
            }
        }

        public override ICollection<SequenceEntry> GetSequences(Guid playbackId, SequenceType sequenceType, DateTime dateTime, TimeSpan maxTimeBefore, int maxCountBefore, TimeSpan maxTimeAfter, int maxCountAfter)
        {
            // TODO: make request
            return new List<SequenceEntry>();
        }

        public override bool MoveTo(Guid playbackId, DateTime dateTime, MoveCriteria moveCriteria)
        {
            lock (_playbackLockObj)
            {
                if (!_playbackCursors.ContainsKey(playbackId))
                {
                    throw new KeyNotFoundException(nameof(playbackId));
                }
            }
            DateTime cur = dateTime;

            // TODO: implement below to do proper update of cursor
            switch (moveCriteria)
            {
                case MoveCriteria.After:
                    break;
                case MoveCriteria.AtOrAfter:
                    break;
                case MoveCriteria.AtOrBefore:
                    break;
                case MoveCriteria.Before:
                    break;
            }
            lock (_playbackLockObj)
            {
                _playbackCursors[playbackId] = cur;
            }
            return true;
        }

        public override bool Navigate(Guid playbackId, NavigateCriteria navigateCriteria)
        {
            DateTime cur;
            lock (_playbackLockObj)
            {
                if (!_playbackCursors.TryGetValue(playbackId, out cur))
                {
                    throw new KeyNotFoundException(nameof(playbackId));
                }
            }

            // TODO: implement below to do proper update of cursor
            switch (navigateCriteria)
            {
                case NavigateCriteria.First:
                    break;
                case NavigateCriteria.Last:
                    break;
                case NavigateCriteria.Previous:
                    break;
                case NavigateCriteria.Next:
                    break;
                case NavigateCriteria.PreviousSequence:
                    break;
                case NavigateCriteria.NextSequence:
                    break;
            }
            lock (_playbackLockObj)
            {
                _playbackCursors[playbackId] = cur;
            }
            return true;
        }

        public override PlaybackReadResponse ReadData(Guid playbackId)
        {
            DateTime cur;
            lock (_playbackLockObj)
            {
                if (!_playbackCursors.TryGetValue(playbackId, out cur))
                {
                    throw new KeyNotFoundException(nameof(playbackId));
                }
            }

            try
            {
                // TODO: request real data
                byte[] data = new byte[] { };
                if (data == null)
                {
                    Toolbox.Log.Trace("--- No data returned ");
                    return null;
                }

                // TODO: Change to correct values - potentially different codec.
                VideoHeader jpegHeader = new VideoHeader();
                jpegHeader.CodecType = VideoCodecType.JPEG;
                jpegHeader.SyncFrame = true;

                PlaybackFrame frame = new PlaybackFrame() { Data = data, Header = jpegHeader, AnyMotion = true };
                jpegHeader.SequenceNumber = 0;
                jpegHeader.Length = (ulong)data.Length;
                jpegHeader.TimestampFrame = cur;
                jpegHeader.TimestampSync = cur;
                DateTime prev = cur - TimeSpan.FromSeconds(1);
                DateTime next = cur + TimeSpan.FromSeconds(1);
                return new PlaybackReadResponse()
                {
                    SequenceNumber = _sequenceNumbers[playbackId],
                    Next = next,
                    Previous = prev,
                    Frames = new[] { frame },
                };
            }
            catch (Exception e)
            {
                Toolbox.Log.Trace("{0}: Exception={1}", nameof(ReadData), e.Message + e.StackTrace);
                return null;
            }
        }
    }
}
