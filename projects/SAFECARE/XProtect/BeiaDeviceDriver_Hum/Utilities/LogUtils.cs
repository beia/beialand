using VideoOS.Platform.DriverFramework.Utilities;

namespace Safecare.BeiaDeviceDriver_Hum
{
    internal class LogUtils
    {
        public static void LogDebug(string text, string where = "")
        {
            Toolbox.Log.LogDebug(where,System.Threading.Thread.CurrentThread.ManagedThreadId + "|" + text);
        }

        public static void LogError(string text, string where)
        {
            Toolbox.Log.LogError(where, System.Threading.Thread.CurrentThread.ManagedThreadId + "|" + text);
        }
    }
}
