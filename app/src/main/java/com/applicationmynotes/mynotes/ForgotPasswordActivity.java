package com.applicationmynotes.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ForgotPasswordActivity extends AppCompatActivity {


    private Button sendMaileForPassword;
    private EditText email_text;
    private  String email;
    private RequestQueue reqQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        reqQue = Volley.newRequestQueue(this);

        sendMaileForPassword = findViewById(R.id.forgotMyPassword);
        email_text = findViewById(R.id.txtForgotPassForEmail);


        sendMaileForPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email   = email_text.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    email_text.setError("Bu alan doldurulmalıdır!");
                    return;
                }
                sendEmailForPassword(email);
            }
        });
    }

    private void sendEmailForPassword(final String email){

        String url = Defines.URL_API + "/Login/forgotPassword";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(response);

                    String statu = jsonObject.getString("statu");
                    String message = jsonObject.getString("message");

                    Log.d("mes",message);


                    if (statu.matches("400")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Log.d("Error.Response", e.getMessage());
                }


            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String , String> params = new HashMap<String, String>();
                params.put("email",email);
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
