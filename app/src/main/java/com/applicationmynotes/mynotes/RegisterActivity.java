package com.applicationmynotes.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {


    private RequestQueue reqQue;

    private TextView signIn;
    private Button   register;
    private String email;
    private String name;
    private String surname;
    private String password;
    private String password1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reqQue = Volley.newRequestQueue(this);



        signIn = findViewById(R.id.textViewSignin);
        register = findViewById(R.id.buttonRegister);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.putExtra("registerControl",true);
                startActivity(intent);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email   = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
                name =  ((EditText)findViewById(R.id.editTextName)).getText().toString();
                surname =  ((EditText)findViewById(R.id.editTextSurname)).getText().toString();
                password =  ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                password1 =  ((EditText)findViewById(R.id.editTextConfirmPassword)).getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    ((EditText)findViewById(R.id.editTextEmail)).setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(name))
                {
                    ((EditText)findViewById(R.id.editTextName)).setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(surname))
                {
                    ((EditText)findViewById(R.id.editTextSurname)).setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    ((EditText)findViewById(R.id.editTextPassword)).setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(password1))
                {
                    ((EditText)findViewById(R.id.editTextConfirmPassword)).setError("Bu alan doldurulmalıdır!");
                    return;
                }
                sendRegisterRequest();
            }
        });

    }

    public void sendRegisterRequest(){
        String url = Defines.URL_API + "/Users/insertTemp";

        final  String type = "1";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String statu = jsonObject.getString("statu");
                    String message = jsonObject.getString("message");

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
                System.out.println(error);

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String , String> params = new HashMap<String, String>();
                params.put("type",type);
                params.put("name",name);
                params.put("surname",surname);
                params.put("email",email);
                params.put("password",password);
                params.put("password1",password1);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        reqQue.add(request);

        ((EditText)findViewById(R.id.editTextEmail)).setText(null);
        ((EditText)findViewById(R.id.editTextName)).setText(null);
        ((EditText)findViewById(R.id.editTextSurname)).setText(null);
        ((EditText)findViewById(R.id.editTextPassword)).setText(null);
        ((EditText)findViewById(R.id.editTextConfirmPassword)).setText(null);

    }

















}
