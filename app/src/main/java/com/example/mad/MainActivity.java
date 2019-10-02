package com.example.mad;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
DBHelper myDB;
MediaPlayer player;
    Button startbutton, aboutbutton, helpbutton, highScoreButton, clearDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startbutton = (Button) findViewById(R.id.button);
        aboutbutton = (Button) findViewById(R.id.btnAbout);
        helpbutton = (Button) findViewById(R.id.btnHelp);
        highScoreButton = (Button)findViewById(R.id.btnHighScore);
        clearDataButton = (Button)findViewById(R.id.btnClearData);

        myDB = new DBHelper(this);


        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                Intent intent = new Intent(getApplicationContext(), LevelUI.class);
                startActivity(intent);
                //stopPlayer();
                finish();


            }
        });
        aboutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
                //stopPlayer();
                finish();
                Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();

            }
        });
        helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                Intent intent = new Intent(getApplicationContext(), Help.class);
                startActivity(intent);
               // stopPlayer();
                finish();
                Toast.makeText(MainActivity.this, "help", Toast.LENGTH_SHORT).show();
            }
        });

        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                getAllInfo();
                //stopPlayer();
            }
        });

        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are your sure Delete !!!").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        stopPlayer();
                             boolean data = myDB.deleteInfo();
                             if (data == true){
                                 Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                             }
                             else{
                                 Toast.makeText(MainActivity.this,"Not Deleted",Toast.LENGTH_SHORT).show();
                             }
                        //finish();
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Alert!!!");
                alertDialog.show();
            }


    });}
    public void getAllInfo() {



        try {

            Cursor cursor1 = myDB.getAllScore();

            if (cursor1.getCount() == 0) {
                Toast.makeText(MainActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                showMassages("Error", "Data not Found!!!");
                return;
            } else {


                Toast.makeText(MainActivity.this, "Data Found", Toast.LENGTH_SHORT).show();

                StringBuffer buffer = new StringBuffer();



                    while (cursor1.moveToNext()) {


                        buffer.append(" " + cursor1.getString(3) + "\n");

                    }

                            cursor1.close();
                            showMassages("SCORE: ", buffer.toString());
                        }



        } catch(Exception e)

        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setCancelable(true);
//            builder.setTitle("Error");
//            builder.setMessage("" + e);
//            builder.show();
        }
    }



    public void showMassages(String title, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void play(View view){
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.click_sound);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }
    public void stop(View view){
        stopPlayer();
    }
    private void stopPlayer(){
        if (player != null){
            player.release();
            player = null;
            //Toast.makeText(this, "Sound stop",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}