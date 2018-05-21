using System;
using System.ComponentModel;
using Xamarin.Forms.Platform.Android;
using Xamarin.Forms;
[assembly: ExportRenderer(typeof(WebView), typeof(meteotestforecast.Android.WebViewFitRenderer))]
namespace meteotestforecast.Android
{
#pragma warning disable CS0618 // Type or member is obsolete
    public class WebViewFitRenderer : WebViewRenderer
    {
        
        protected override void OnElementPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            if (Control != null)
            {

                Control.Settings.BuiltInZoomControls = true;
                Control.Settings.DisplayZoomControls = false;
                Control.Settings.LoadWithOverviewMode = true;
                Control.Settings.UseWideViewPort = true;
            }
            base.OnElementPropertyChanged(sender, e);
        }
    }
#pragma warning restore CS0618 // Type or member is obsolete
}
