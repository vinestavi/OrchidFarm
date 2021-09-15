package com.vina.orchidfarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etLoginUsername, etLoginPassword;
    MaterialButton btnLogin, btnQR;
    String Username, Password;
    ProgressDialog progressDialog;
    String SN;

    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        etLoginUsername = findViewById(R.id.email_login);
        etLoginPassword = findViewById(R.id.password_login);
        progressDialog = new ProgressDialog(LoginActivity.this);

        btnLogin = findViewById(R.id.btnLogin);
        btnQR = findViewById(R.id.btnQR);

        btnLogin.setOnClickListener(v -> {
            String sUsername = etLoginUsername.getText().toString();
            String sPassword = etLoginPassword.getText().toString();

            CheckLogin(sUsername, sPassword);
        });

        btnQR.setOnClickListener(v -> {
            //initialize intent integrator
            IntentIntegrator intentIntegrator = new IntentIntegrator(LoginActivity.this);
            //Set Prompt Text
            intentIntegrator.setPrompt("For flash use volume up key");
            //set Beep
            intentIntegrator.setBeepEnabled(true);
            //locked orientation
            intentIntegrator.setOrientationLocked(true);
            //set capture activity
            intentIntegrator.setCaptureActivity(Capture.class);
            //initiate scan
            intentIntegrator.initiateScan();
        });

        Username = sharedPreferenceManager.getUsername();
        Password = sharedPreferenceManager.getPassword();
        SN = sharedPreferenceManager.getSn();
        if (!sharedPreferenceManager.getSession()) {
            Toast.makeText(this, "Email & SN are not registered yet", Toast.LENGTH_SHORT).show();
        } else {
            if (Username.equals("smknegeri1bawen@gmail.com") || SN.equals("2021080001")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //Check condition
        if (intentResult.getContents() != null){
            SN = intentResult.getContents();
            Log.d("TAG","SN : " +SN);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Result");
            builder.setMessage("Device Serial Number\n"+intentResult.getContents());

            //love you ---
            builder.setPositiveButton("OK", (dialog, which) -> {
                sharedPreferenceManager.saveString(SharedPreferenceManager.SN, SN);
                sharedPreferenceManager.saveBoolean(SharedPreferenceManager.SESSION, true);
                if (SN.equals("2021080001")) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("SN", SN);
                    startActivity(i);
                    finish();
                }
            });
            //show dialog Interface
            builder.show();
        } else {
            //when result content is null
            //Display Toast
            Toast.makeText(getApplicationContext(), "OOPS...You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    public void CheckLogin(final String username, final String password) {
        if (checkNetworkConnection()) {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_LOGIN_URL,
                    response -> {
//                Log.d(LoginActivity.class.getSimpleName(), "respon :"+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String resp = jsonObject.getString("success");
                            if (resp.equals("1")) {
                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                sharedPreferenceManager.saveString(SharedPreferenceManager.USERNAME, username);
                                sharedPreferenceManager.saveString(SharedPreferenceManager.PASSWORD, password);
                                sharedPreferenceManager.saveBoolean(SharedPreferenceManager.SESSION, true);

                                if (username.equals("smknegeri1bawen@gmail.com")) {
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("username", username);
                                    startActivity(i);
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {

                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            VolleyConnection.getInstance(LoginActivity.this).addToRequestQue(stringRequest);

            new Handler().postDelayed(() -> progressDialog.cancel(), 2000);
        } else {
            Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}