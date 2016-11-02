package in.whomeninja.app.barcodescannerforwebsite;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_SITE_URL = "http://blog.whomeninja.in",
            STRING_TO_MATCH_FOR_BARCODE_SCAN = "firebarcodescannerforwebsites=1",
            POST_URL = "http://blog.whomeninja.in/add-android-barcode-scanner-to-your-website/";
    private WebView main_web_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide actionbar on top of activity
        ActionBar action_bar = getSupportActionBar();
        action_bar.hide();

        // load site URL
        openUrl(MAIN_SITE_URL);
    }

    /**
     * function to init a webview and load URL
     * @param url String URL
     */
    public void openUrl(String url){
        main_web_view = (WebView) findViewById(R.id.mainWebView);
        //enable javascript
        main_web_view.getSettings().setJavaScriptEnabled(true);

        // get the activity context
        final Activity activity = this;

        //set client to handle errors and intercept link clicks
        main_web_view.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                String msg = "error : "+description+" Request URL : "+failingUrl;
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // we will interrupt the link here
                if(isURLMatching(url)) {
                    scanNow();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view,url);
            }

        });

        //load the URL
        main_web_view.loadUrl(url);
    }

    /**
     * Function to check if URL match contains our string
     * @param url string URL to compare
     * @return boolean true or false
     */
    protected boolean isURLMatching(String url){
        return url.toLowerCase().contains(STRING_TO_MATCH_FOR_BARCODE_SCAN.toLowerCase());
    }

    /**
     * Initiate the barcode scan
     */
    public void scanNow(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(String.valueOf("Scan Barcode"));
        integrator.setResultDisplayDuration(0);
        integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     * @param requestCode scanned code
     * @param resultCode  result of scanned code
     * @param intent intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String codeContent = scanningResult.getContents();
            String codeFormat = scanningResult.getFormatName();

            //load the URL and Pass the scanned barcode
            openUrl(POST_URL+"?barcode="+codeContent);

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
