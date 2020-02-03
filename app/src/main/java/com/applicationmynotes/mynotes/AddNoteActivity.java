package com.applicationmynotes.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public  class AddNoteActivity extends AppCompatActivity  {


    Button btnSave;
    EditText edtNoteHeader;
    EditText edtNoteDetail;

    private String title;
    private String content;
    private  String token;
    private SharedPreferences sharedPref;

    private RequestQueue reqQue;
    private ImageView imageBack;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        btnSave = findViewById(R.id.btnSaveNote);
        edtNoteHeader =  findViewById(R.id.editText_lesson_note);
        edtNoteDetail =  findViewById(R.id.editText_description_note);
        imageBack = findViewById(R.id.textViewBack);

        sharedPref = getSharedPreferences("com.applicationmynotes.mynotes", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", null); // Shared Okuma

        reqQue = Volley.newRequestQueue(this);


        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                title   = edtNoteHeader.getText().toString();
                content = edtNoteDetail.getText().toString();
                if(title.isEmpty())
                {
                    edtNoteHeader.setError("Bu alan boş bırakılamaz!");
                    return;
                }
                if(content.isEmpty())
                {
                    edtNoteDetail.setError("Bu alan boş bırakılamaz!");
                    return;
                }
                sendAddNoteRequest(title,content);
                edtNoteHeader.setText(null);
                edtNoteDetail.setText(null);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNoteActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    public void sendAddNoteRequest(final String title , final String content){
        String url = Defines.URL_API + "/MyNotes/insert";

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
            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String , String> params = new HashMap<String, String>();
                params.put("title",title);
                params.put("content",content);
                params.put("token",token);

                return params;
            }
        };
        reqQue.add(request);
    }

}
