using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace meteotestforecast
{
    public partial class ContentShow : ContentPage
    {
        public ContentShow(City city)
        {
            InitializeComponent();

            WebView web = contentWebview;
            web.Source = city.Url;
            Title = city.Name;
        }
    }
}
