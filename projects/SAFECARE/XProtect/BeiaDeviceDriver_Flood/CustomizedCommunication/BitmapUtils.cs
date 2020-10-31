using System.Drawing;
using System.Drawing.Imaging;
using System.IO;

namespace Safecare.BeiaDeviceDriver_Flood
{
    public class BitmapUtils
    {
        public static Bitmap ConvertTextToImage(
            string txt,
            string fontName  = "Arial",
            int fontSize = 12,
            int width = 500,
            int Height = 500)
        {
            Bitmap bmp = new Bitmap(width, Height);
            using (Graphics graphics = Graphics.FromImage(bmp))
            {

                Font font = new Font(fontName, fontSize);
                graphics.FillRectangle(new SolidBrush(Color.White), 0, 0, bmp.Width, bmp.Height);
                graphics.DrawString(txt, font, new SolidBrush(Color.Black), 0, 0);
                graphics.Flush();
                font.Dispose();
                graphics.Dispose();
            }

            return bmp;
        }

        public static byte[] BitmapToJpegBytes(Bitmap bitmap)
        {
            var ms = new MemoryStream();
            bitmap.Save(ms, ImageFormat.Jpeg);
            return ms.ToArray();
        }
    }
}
