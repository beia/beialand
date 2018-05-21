using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Diagnostics;

namespace meteotestforecast
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            Debug.WriteLine("Executing MainPage constructor");
            InitializeComponent();

            // Set image Source
            // TODO: Use a middleware + XML instead
            logoImage.Source = ImageSource.FromResource("meteotestforecast.sigla-beia.png");

            // Add items to the picker
            foreach (var city in Constants.locations)
            {
                cityPicker.Items.Add(city.Name);
            }


        }

        async void OnCityPickerSelectedIndexChanged(object sender, EventArgs e)
        {
            var picker = (Picker)sender;
            int selectedIndex = picker.SelectedIndex;

            if (selectedIndex != -1)
            {
                await DisplayAlert("Selection",
                                   "We found that you selected",
                                   Constants.locations[selectedIndex].Name);
            }

            // Make sure that selecting the same element again will trigger
            // the event.
            cityPicker.SelectedIndex = -1;
        }
    }

}
