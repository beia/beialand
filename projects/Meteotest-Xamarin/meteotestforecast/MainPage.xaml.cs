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
            logoImage.Source = ImageSource.FromResource("meteotestforecast.sigla-beia.png");
        }
    }
}
