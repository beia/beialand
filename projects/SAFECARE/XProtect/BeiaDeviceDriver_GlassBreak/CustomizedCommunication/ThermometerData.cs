﻿using System;
using Newtonsoft.Json;

namespace Safecare.BeiaDeviceDriver_GlassBreak
{
    public class ThermometerData
    {
        [JsonProperty("Utc_date_time")]
        public DateTime Time { get; set; }

        [JsonProperty("Glass_Break")]
        public double GlassBreak{ get; set; }
        

        public string Serialize()
        {
            return JsonConvert.SerializeObject(this,
                new JsonSerializerSettings
                {
                    DateFormatHandling = DateFormatHandling.IsoDateFormat,
                    Formatting = Formatting.Indented,
                    DefaultValueHandling = DefaultValueHandling.Ignore
                });
        }

        public static ThermometerData Deserialize(string text)
        {
            return JsonConvert.DeserializeObject<ThermometerData>(text);
        }

        public void SetDateTimeIfEmpty()
        {
            if (Time == default)
                Time = DateTime.UtcNow;
        }
    }
}
