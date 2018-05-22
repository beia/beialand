using System;
namespace meteotestforecast
{
    public static class Constants
    {
        public static readonly City[] locations =
        {
            new City("Zanesti", "https://api.meteotest.ch/beia/v1/forecast?location=1"),
            new City("Corbi", "https://api.meteotest.ch/beia/v1/forecast?location=2"),
            new City("Zabala", "https://api.meteotest.ch/beia/v1/forecast?location=3"),
            new City("Caracal", "https://api.meteotest.ch/beia/v1/forecast?location=4"),
            new City("Insuratei", "https://api.meteotest.ch/beia/v1/forecast?location=5"),
            new City("Comlosu Mic", "https://api.meteotest.ch/beia/v1/forecast?location=6"),
            new City("Marghita", "https://api.meteotest.ch/beia/v1/forecast?location=7"),
        };

        public static readonly string APP_NAME = "Beia Meteotest";
    }
}
