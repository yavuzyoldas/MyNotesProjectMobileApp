package com.applicationmynotes.mynotes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.util.Attributes;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String token;
    private SharedPreferences sharedPref;
    private RequestQueue reqQue;
    private ImageButton addButton;
    final   MainActivity mA  = this;
    private Button logOut;

    /*RecyclerView*/
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<Notes> notes = new ArrayList<>();
    Context context = this;
    /*RecyclerView*/

    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("com.applicationmynotes.mynotes", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", null); // Shared Okuma

        if(token == null){
            Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginScreen);
            return;
        }

        reqQue = Volley.newRequestQueue(this);

        addButton = findViewById(R.id.BtnAddNote);

        logOut = findViewById(R.id.btnLogOut);

        /*RecyclerView*/
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setVisibility(View.VISIBLE);

        customAdapter = new CustomAdapter(notes, context, mA);

        customAdapter.setMode(Attributes.Mode.Single);

       // recyclerView.setAdapter(customAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);

                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();

                token = null;
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendGetNotesRequest();  // kullanıcının notlarını getiren istek
    }
    @Override
    public void onRefresh() {

        sendGetNotesRequest();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        Toast.makeText(context, "Yenilendi", Toast.LENGTH_SHORT).show();
    }
    //region sendGetNotesRequest Request
    public void sendGetNotesRequest(){
        String url = Defines.URL_API + "/MyNotes/get";

        notes.clear();

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
                            JSONArray data = jsonObject.getJSONArray("data");

                            if (statu.matches("400")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
                                loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //activity stack clear
                                startActivity(loginScreen);
                            } else
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject note = data.getJSONObject(i);

                                notes.add(new Notes(
                                        note.getString("id"),
                                        note.getString("title"),
                                        note.getString("content"),
                                        note.getString("date")
                                ));

                                recyclerView.setAdapter(customAdapter);
                            }

                        } catch (JSONException e) {
                            Log.d("Error", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error",  error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                return params;
            }
        };
        reqQue.add(request);
    }
    //endregion

   // region sendUpdateNoteRequest
    public void sendUpdateNoteRequest(final String noteId,final String title, final  String content){
        String url = Defines.URL_API + "/MyNotes/update";

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
                params.put("id",noteId);
                params.put("title",title);
                params.put("content",content);
                params.put("token",token);

                return params;
            }
        };
        reqQue.add(request);
    }
    //endregion


    //region sendDeleteNoteRequest Request
    public void  sendDeleteNoteRequest(final String noteId){

        String url = Defines.URL_API + "/MyNotes/delete";
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
                //token = sharedPref.getString("token",null);
                Map<String , String> params = new HashMap<String, String>();
                params.put("token",token);
                params.put("id",noteId);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                reqQue.add(request);
    }
    //endregion
}
