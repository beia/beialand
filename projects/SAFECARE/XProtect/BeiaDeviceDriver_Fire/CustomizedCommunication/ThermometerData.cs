using System;
using Newtonsoft.Json;

namespace Safecare.BeiaDeviceDriver_Fire
{
    public class ThermometerData
    {
        [JsonProperty("Utc_date_time")]
        public DateTime Time { get; set; }
       
        [JsonProperty("Fire_Detection")]
        public double FireDetection { get; set; } 

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
