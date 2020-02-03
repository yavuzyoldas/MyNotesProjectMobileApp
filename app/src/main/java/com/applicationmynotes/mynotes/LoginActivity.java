package com.applicationmynotes.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private String email;
    private String password;
    private RequestQueue reqQue;
    private SharedPreferences sharedPref;
    private TextView register;

    private CallbackManager callbackManager;
    private com.facebook.login.widget.LoginButton btnLoginFacebook;

    private static String fEmail;
    private static String fName;
    private static String fSurname;
    private static String fId;
    private String key = " ";
    public int x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("com.applicationmynotes.mynotes", Context.MODE_PRIVATE);

        reqQue = Volley.newRequestQueue(this);

        Uri data = this.getIntent().getData();
        if (data != null) {
            this.key = getActivationKey();
            if (!key.matches(" ")) {
                sendInsertUser(key);
            }
        }

        register = findViewById(R.id.btnSign);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ((EditText) findViewById(R.id.txtEmail)).getText().toString();
                password = ((EditText) findViewById(R.id.txtPassword)).getText().toString();
                if (email.matches("") ||
                        password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Lütfen Tüm Alanları Doldurun", Toast.LENGTH_LONG).show();
                } else {
                    sendRequest();
                }
            }
        });

        findViewById(R.id.btnForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnLoginFb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLoginFacebook.performClick();
            }
        });

        btnLoginFacebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();

        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    fEmail = object.getString("email");
                                    fName = object.getString("first_name");
                                    fSurname = object.getString("last_name");
                                    fId = object.getString("id");
                                    if(fEmail.matches("") || fName.matches("") || fSurname.matches("") || fId.matches("") ){
                                        Toast.makeText(getApplicationContext(),"Bir hata meydana geldi!(Parametreler Boş)",Toast.LENGTH_LONG).show();
                                    }else
                                        sendRequestFacebook();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name,email,id");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                Log.d("test", "oncancel");
            }
            @Override
            public void onError(FacebookException exception) {
                Log.d("test", exception.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getActivationKey() {
        String key = " ";

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            key = this.getIntent().getData().getQueryParameter("k");
        } else {
            Toast.makeText(getApplicationContext(), "Bir Hata Meydana Geldi...", Toast.LENGTH_LONG).show();
        }
        return key;
    }

    //region aktivaston
    private void sendInsertUser(final String key) {

        String url = Defines.URL_API + "/Users/insert";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(response);

                    String statu = jsonObject.getString("statu");
                    String message = jsonObject.getString("message");
                    String data = jsonObject.getString("data");

                    if (statu.matches("400")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        ((EditText) findViewById(R.id.txtEmail)).setText(data);
                    }

                } catch (JSONException e) {
                    Log.d("Error.Response", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //token = sharedPref.getString("token",null);
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", key);
                return params;
            }
        };

        reqQue.add(request);
    }

    //endregion
    //region Login Request
    public void sendRequest() {
        String url = Defines.URL_API + "/Login";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String statu = jsonObject.getString("statu");
                            String message = jsonObject.getString("message");
                            if (statu.matches("200")) {
                                String token = jsonObject.getString("token");
                                sharedPref.edit().putString("token", token).apply(); // Shared Yazma
                                Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
                                mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainScreen);
                            } else
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.d("Error.Response", e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Login isteği hatalı", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        reqQue.add(request);
    }
    //endregion

    //region Sign Request
    public void sendRequestFacebook() {
        String url = Defines.URL_API + "/Login/facebookLogin";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject jsonObject = null;
                        try {

                            jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            String statu = jsonObject.getString("statu");
                            String message = jsonObject.getString("message");

                            if (statu.matches("200")) { //kayıtlı bir kullanıcı ve login olacaksa
                                String token = jsonObject.getString("token");
                                sharedPref.edit().putString("token", token).apply();
                                Toast.makeText(LoginActivity.this,message, Toast.LENGTH_SHORT).show();


                                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
                                mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainScreen);

                            }else{
                                Toast.makeText(LoginActivity.this, "Bir hata meydana geldi!", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            Log.d("Error.Response", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", fEmail);
                params.put("id", fId);
                params.put("name", fName);
                params.put("surname", fSurname);
                params.put("type","2");
                return params;
            }
        };
        reqQue.add(request);
    }
    //endregion

}
