package com.example.rodri.musicbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.google.gson.reflect.TypeToken;
import model.GsonRequest;
import model.AudioFormat;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Button postt;
    private final int N = 10;
    private RelativeLayout myLayout;
    private TextView[] myTextViews;
    private Button[] downloadButtons;
    Type listAudios = new TypeToken<List<AudioFormat>>(){}.getType();
    private Context context = this;
    File myDir ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDir = getFilesDir();

        //controles
        postt =  findViewById(R.id.button);
        myLayout = findViewById(R.id.myLayout);
        //request
        requestQueue= Volley.newRequestQueue(MainActivity.this);
        postt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        postt.setEnabled(false);
                        GsonRequest<AudioFormat[]> gsonRequest = new GsonRequest <>(
                                "http://192.168.56.1:3000/audios",//URL
                                AudioFormat[].class  ,//Clase a la que se convertira el JSON
                                null,//encabezado no necesitamos
                                createRequestSuccessListener(),//listener
                                createRequestErrorListener()//listener
                        );
                        requestQueue.add(gsonRequest);
                    }
                });

        final Button nextActivity = new Button(context);
        nextActivity.setText("NEXT");
        RelativeLayout.MarginLayoutParams marginParams;
        marginParams = new RelativeLayout.MarginLayoutParams(400, 400);
        marginParams.setMargins(0, 0, 0, 10);
        RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(marginParams);
        myLayout.addView(nextActivity);
        nextActivity.setLayoutParams(params);
        nextActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ReproductionActivity.class);
                        startActivity(intent);
                    }
                }
        );


    }
    //he cambiado el tipo inside listener a String y en gson request hecho para que la deserializacion sea ahora desde aqui y no desde el constructor
    private Response.Listener<AudioFormat[]> createRequestSuccessListener() {
        return new Response.Listener<AudioFormat[]>() {
            @Override
            public void onResponse(AudioFormat[] response) {
                try {
                    postt.setEnabled(true);
                    int lenght = response.length;
                    System.out.println("LONGGGGGG " + response.length);
                    myTextViews = new TextView[lenght];
                    downloadButtons = new Button[lenght];
                    //el post obtenido del REST se llena en la interfaz
                    for (int i = 0; i < lenght && i < N; i++) {
                        //Creacion y guardado de los text views ( sustituir quizas por string ? solo necesitamos el titulo? )

                        final TextView rowTextView = new TextView(context);
                        rowTextView.setText(response[i].getTitle());
                        RelativeLayout.MarginLayoutParams marginParams;
                        marginParams = new RelativeLayout.MarginLayoutParams(400, 400);
                        marginParams.setMargins(10, 100 * i + 20, 0, 0);
                        RelativeLayout.LayoutParams params;
                        params = new RelativeLayout.LayoutParams(marginParams);
                        myLayout.addView(rowTextView);
                        rowTextView.setLayoutParams(params);
                        myTextViews[i] = rowTextView;
                        System.out.println(myTextViews[i].getId() + " ID Text");



                        RelativeLayout.MarginLayoutParams marginParamsB;
                        marginParamsB = new RelativeLayout.MarginLayoutParams(200, 100);
                        marginParamsB.setMargins(400, 100 * i + 5, 0, 0);
                        RelativeLayout.LayoutParams paramsB;
                        paramsB = new RelativeLayout.LayoutParams(marginParamsB);

                        final int pos = i;
                        downloadButtons[pos] = new Button(getApplicationContext());
                        myLayout.addView(downloadButtons[pos]);
                        downloadButtons[pos].setLayoutParams(paramsB);
                        downloadButtons[pos].setText("Download");
                        downloadButtons[pos].setId(pos);
                        downloadButtons[pos].setOnClickListener(listenerDownloader);
                    /*System.out.println(downloadButtons.length +" <--- longitud");
                    for (int k = 0; k<downloadButtons.length; k++)
                        */
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
    }

    private View.OnClickListener listenerDownloader = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            downloadButtons[view.getId()].setEnabled(false);
            try {

                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here


                    File saveFilePath = new File(myDir ,myTextViews[view.getId()].getText().toString());
                    System.out.println(saveFilePath + " Direccion en la memoria ");

                    //File file = new File(saveFilePath, myTextViews[view.getId()].getText().toString());
                    HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.56.1:3000/audios/" + myTextViews[view.getId()].getText()).openConnection();
                    //int contentLength = 0;

                    //contentLength = connection.getContentLength();

                    InputStream response = connection.getInputStream();
                    BufferedInputStream bufferinstream = new BufferedInputStream(response);

                    ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                    int current = 0;
                    while((current = bufferinstream.read()) != -1){
                        baf.append((byte) current);
                    }

                    FileOutputStream fos = new FileOutputStream(saveFilePath);
                    fos.write(baf.toByteArray());
                    fos.flush();
                    fos.close();
                    System.out.println("finished download");


                    /*
                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                    int bytesRead = -1;
                    byte[] buffer = new byte[response.available()];
                    while ((bytesRead = response.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    response.close();

                    connection.disconnect();
                    */
                }
            } catch (Exception r) {
                r.printStackTrace();
            }
        }
    };

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    protected void onStart() {
        super.onStart();

}   }