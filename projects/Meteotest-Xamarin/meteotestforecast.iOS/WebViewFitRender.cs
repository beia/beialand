using System;
using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

[assembly: ExportRenderer(typeof(WebView), typeof(meteotestforecast.iOS.WebViewFitRender))]
namespace meteotestforecast.iOS
{
    public class WebViewFitRender : WebViewRenderer 
    {
        protected override void OnElementChanged(VisualElementChangedEventArgs e) {
            base.OnElementChanged(e);
            var view = (UIKit.UIWebView)NativeView;
            view.ScrollView.ScrollEnabled = true;
            view.ScalesPageToFit = true;
        }
    }


}
