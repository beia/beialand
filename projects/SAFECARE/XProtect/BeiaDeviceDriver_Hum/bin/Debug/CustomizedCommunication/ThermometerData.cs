using System;
using Newtonsoft.Json;

namespace Safecare.BeiaDeviceDriver_Temp

{
    public class ThermometerData
    {
        [JsonProperty("Utc_date_time")]
        public DateTime Time { get; set; }

        [JsonProperty("Fan_Power")]
        public double Fanpower { get; set; }


        /*
        [JsonProperty("Temperature_in_degrees")]
         public double TemperatureInDegrees { get; set; }
         
         

         [JsonProperty("Humidity_level")]
         public double HumidityLevel { get; set; }

         [JsonProperty("Glass_Break")]
         public double GlassBreak { get; set; }

         [JsonProperty("Sound_level")]
         public double SoundDetection { get; set; }

         [JsonProperty("Flood_detection")]
         public double FloodDetection { get; set; }

         [JsonProperty("Fan_Power")]
         public double Fanpower { get; set; }
        
         */

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
