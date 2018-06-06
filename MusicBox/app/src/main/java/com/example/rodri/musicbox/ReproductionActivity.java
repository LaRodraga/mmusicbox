package com.example.rodri.musicbox;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.media.SoundPool;
import java.io.File;
import java.io.IOException;



public class ReproductionActivity extends AppCompatActivity {

    File directory;
    File[] files;
    RelativeLayout repLay;
    static int MAX_NUMBER_STREAMS = 4;
    final SoundPool mySoundPool = new SoundPool(
            MAX_NUMBER_STREAMS ,
            AudioManager.STREAM_MUSIC,
            0
    );

    int[] audioIds = new int[MAX_NUMBER_STREAMS ]; //if we'd like to add more stream just change number on constructor and here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduction);
        Intent intent = getIntent();
        System.out.println("holi");

        directory = getFilesDir();
        repLay = findViewById(R.id.repLayout);
        files = directory.listFiles();
        for(int i = 0 ; i<files.length ; i++){
            System.out.println("file : " + files[i].getName());

            final TextView Title = new TextView(this);
            Title.setText(files[i].getName());
            RelativeLayout.MarginLayoutParams marginParams;
            marginParams = new RelativeLayout.MarginLayoutParams(400, 400);
            marginParams.setMargins(10, 100 * i + 20, 0, 0);
            RelativeLayout.LayoutParams params;
            params = new RelativeLayout.LayoutParams(marginParams);
            repLay.addView(Title);
            Title.setLayoutParams(params);


            RelativeLayout.MarginLayoutParams marginParamsB;
            marginParamsB = new RelativeLayout.MarginLayoutParams(200, 100);
            marginParamsB.setMargins(400, 100 * i + 5, 0, 0);
            RelativeLayout.LayoutParams paramsB;
            paramsB = new RelativeLayout.LayoutParams(marginParamsB);

            final int pos = i;
            final Button button = new Button(getApplicationContext());
            repLay.addView(button);
            button.setLayoutParams(paramsB);
            button.setText("PLAY");
            button.setId(pos);
            button.setOnClickListener(listenerReproducer);

            mySoundPool.setOnLoadCompleteListener(onLoadComplete);
            audioIds[i] = mySoundPool.load(directory + File.separator + files[i], 1);
        }
    }


    public View.OnClickListener listenerReproducer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mySoundPool.play(audioIds[view.getId()],50,50,1,10,1);
            //mySoundPool.resume(audioIds[view.getId()]);
        }
    };

    public SoundPool.OnLoadCompleteListener onLoadComplete = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int i, int i1) {
            {
                System.out.println("carga completa");
            }
        }
    };
}





