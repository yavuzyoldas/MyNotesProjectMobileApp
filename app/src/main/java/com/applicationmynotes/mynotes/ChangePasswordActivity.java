package com.applicationmynotes.mynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    private RequestQueue reqQue;
    private String key = " ";
    private  String  password;
    private  String  password1;

    private EditText text_password;
    private EditText text_password1;

    private Button  password_change;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        text_password = findViewById(R.id.txtForgotPass);
        text_password1 = findViewById(R.id.txtForgotPass1);
        password_change = findViewById(R.id.changeMyPassword);







        reqQue = Volley.newRequestQueue(this);

        this.key = getActivationKey();

        password_change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                password = text_password.getText().toString();
                password1 = text_password1.getText().toString();

                if(TextUtils.isEmpty(password))
                {
                    text_password.setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(password1))
                {
                    text_password1.setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(password != password1){
                    Toast.makeText(getApplicationContext(),"Şifreleriniz eşleşmedi!",Toast.LENGTH_LONG).show();
                }
                  sendChangeForgottenPassword();
            }
        });
    }


    private String getActivationKey() {
        String key = " ";

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            key = this.getIntent().getData().getQueryParameter("k");
        } else {
            Toast.makeText(getApplicationContext(), "Bir Hata Meydana Geldi...", Toast.LENGTH_LONG).show();
            Intent mainScreen = new Intent(ChangePasswordActivity.this, MainActivity.class);
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);
        }
        return key;
    }

    private void sendChangeForgottenPassword() {

        String url = Defines.URL_API + "/Login/changeForgottenPassword";
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
                params.put("password1",password);
                params.put("password2", password1);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        reqQue.add(request);
    }
}