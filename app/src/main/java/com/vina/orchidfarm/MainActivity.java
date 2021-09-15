package com.vina.orchidfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ControlDialog.CreateDialogListener {

    WebView webview;
    WebSettings websettings;
    ProgressDialog progressDialog;
    Toolbar title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);

        webview = (WebView)findViewById(R.id.WebView1);
        websettings = webview.getSettings();
        websettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new SSLTolerentWebViewClient());
        webview.loadUrl("https://omahiot.net/api/index.php?sn=2021080001");

        title = findViewById(R.id.topAppBar);
        setSupportActionBar(title);
    }

    public void createDataToServer(final String meja1, final String meja2) {
        if (checkNetworkConnection()) {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_CONTROL_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String resp = jsonObject.getString("success");
                                if (resp.equals("1")) {
                                    Toast.makeText(getApplicationContext(), "Set Threshold Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Set Threshold Failed", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("meja1", meja1);
                    params.put("meja2", meja2);
                    return params;
                }
            };

            VolleySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 2000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionlogout:
                SharedPreferenceManager spm = new SharedPreferenceManager(this);
                spm.clearUsername();
                spm.clearPassword();
                spm.clearSn();
                spm.clearSession();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.actioncontrol:
                openCreateDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openCreateDialog() {
        ControlDialog controlDialog = new ControlDialog();
        controlDialog.show(getSupportFragmentManager(), "Set Threshold");
    }

    @Override
    public void post(String meja1, String meja2) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        createDataToServer(meja1, meja2);
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}