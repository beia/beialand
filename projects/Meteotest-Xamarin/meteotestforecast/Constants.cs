using System;
namespace meteotestforecast
{
    public static class Constants
    {
        public static readonly City[] locations =
        {
            new City("Bucharest, Beia Consult Office", "http://api.meteotest.ch/beia/v1/forecast?lat=44.3959788&lon=26.1005863"),
            new City("Zanesti", "http://api.meteotest.ch/beia/v1/forecast?location=1"),
            new City("Corbi", "http://api.meteotest.ch/beia/v1/forecast?location=2"),
            new City("Zabala", "http://api.meteotest.ch/beia/v1/forecast?location=3"),
            new City("Caracal", "http://api.meteotest.ch/beia/v1/forecast?location=4"),
            new City("Insuratei", "http://api.meteotest.ch/beia/v1/forecast?location=5"),
            new City("Comlosu Mic", "http://api.meteotest.ch/beia/v1/forecast?location=6"),
            new City("Marghita", "http://api.meteotest.ch/beia/v1/forecast?location=7"),
        };

        public static readonly string APP_NAME = "Beia Meteotest";
    }
}
