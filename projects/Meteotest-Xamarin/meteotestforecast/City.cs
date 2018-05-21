using System;
namespace meteotestforecast
{
    public class City
    {
        public string Name { get; set; }
        public string Url { get; set; }

        public City()
        {
            
        }

        public City(String Name, String Url)
        {
            this.Name = Name;
            this.Url = Url;
        }


    }
}
