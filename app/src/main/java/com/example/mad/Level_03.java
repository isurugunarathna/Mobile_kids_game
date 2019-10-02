package com.example.mad;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Level_03 extends AppCompatActivity implements SensorEventListener {

    DBHelper myDB;
    private SensorManager sensorManager;
    private boolean color = false;
    // private View view;
    private long lastUpdate;


     MediaPlayer player;

    private int gameValue = 1;
    Button btnOk, nextBtn, prvBtn;
    FrameLayout r2;
    ImageView iv;
    TextView score,timer;
    FrameLayout.LayoutParams params2;

    ImageView upperImageViews;
    ImageView lowerImageView[] = new ImageView[5];
    private static int iScore = 10;
    private static String iLevel = "3_1";

    CountDownTimer countDownTimer;
    long timeLeftInMilliSeconds = 15000;
    boolean timeRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_03);
        myDB = new DBHelper(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        // gameValue = Integer.parseInt(getIntent().getStringExtra("gameValue"));
        r2 = (FrameLayout) findViewById(R.id.parentLayout);
        iv = new ImageView(this);
        score = (TextView) findViewById(R.id.txtScore);
        score.setText(String.valueOf(iScore));
        btnOk = (Button) findViewById(R.id.btnOk);
        //nextBtn = (Button) findViewById(R.id.btnNext);
       // prvBtn = (Button) findViewById(R.id.btnBack);
        timer=(TextView) findViewById(R.id.txtTimer) ;
        upperImageViews = (ImageView) findViewById(R.id.imgBox);

        lowerImageView[0] = (ImageView) findViewById(R.id.imgNum_20);
        lowerImageView[1] = (ImageView) findViewById(R.id.imgNum_30);
        lowerImageView[2] = (ImageView) findViewById(R.id.imgNum_40);
        lowerImageView[3] = (ImageView) findViewById(R.id.imgNum_50);
        lowerImageView[4] = (ImageView) findViewById(R.id.imgNum_60);

        startTime();

        int upperImages[][] = {
                {R.drawable.box}
        };

        int lowerImages[][] = {
                {R.drawable.n20, R.drawable.n30, R.drawable.n40, R.drawable.n50, R.drawable.n60}
        };


        for (int img = 0; img < upperImages[gameValue - 1].length; img++) {
            //upperImageViews[img].setImageResource(upperImages[gameValue - 1][img]);
            lowerImageView[img].setImageResource(lowerImages[gameValue - 1][img]);
        }
        for (int i = 0; i < lowerImages[gameValue - 1].length; i++) {
            lowerImageView[i].setTag(lowerImages[gameValue - 1][i]);


            lowerImageView[i].setOnTouchListener(new Level_03.MyTouchListener());

        }
        FrameLayout fl1 = (FrameLayout) findViewById(R.id.parentLayout);


        if (fl1 != null) {
            fl1.setOnDragListener(new Level_03.MyDragListener());
            //upperImageViews.setOnDragListener(new MyDragListener());

        }

        FrameLayout flBox = (FrameLayout) findViewById(R.id.boxLayout);
        if (flBox != null) {
            flBox.setOnDragListener(new Level_03.MyDragListener2());

        }

        upperImageViews.setOnDragListener(new Level_03.MyDragListener2());

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
                AlertDialog.Builder builder = new AlertDialog.Builder(Level_03.this);
                builder.setMessage("Do you want to Submit !!!").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        addData();
                        getAllInfo();
                        stopTimer();
                        stopPlayer();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Intent intent = new Intent(getApplicationContext(), Level_03_2.class);
                                intent.putExtra("Score", getiScore());
                                startActivityForResult(intent, 1);
                                finish();
                                iScore = 10;
                            }
                        }, 5000);

                                iScore = 10;
                    }



                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Your Score " + iScore);
                alertDialog.show();
            }
        });

    }
        public void startTime() {
            if (timeRunning) {
                stopTimer();
                // Toast.makeText(Level_04.this, "ansfknak", Toast.LENGTH_SHORT).show();


            } else
                startTimer();
            // Toast.makeText(Level_04.this, "kkkkkk", Toast.LENGTH_SHORT).show();
        }

        public void startTimer() {
            countDownTimer = new CountDownTimer(timeLeftInMilliSeconds, 1000) {
                @Override
                public void onTick(long l) {
                    timeLeftInMilliSeconds = l;
                    updateTimes();
                }

                @Override
                public void onFinish() {
                    Toast.makeText(Level_03.this, "Times gone", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Level_03.this);
                    builder.setMessage("Do you want to Submit !!!").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            addData();
                            //finish();
                        }

                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getApplicationContext(), Level_03_2.class);
                                    startActivity(intent);
                                    dialogInterface.cancel();
                                    //finish();

                                }
                            });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.setTitle("Your Score " + iScore);
                    alertDialog.show();
                }
            }.start();
            timeRunning = true;
        }

        public void stopTimer() {
            countDownTimer.cancel();
            timeRunning = false;
        }

        public void updateTimes() {
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int minutes = (int) timeLeftInMilliSeconds / 60000;
                    int seconds = (int) timeLeftInMilliSeconds % 60000 / 1000;
                    String timeLeftText;

                    timeLeftText = "" + minutes;
                    timeLeftText += ":";
                    if (seconds < 10) timeLeftText += "0";
                    timeLeftText += seconds;

                    timer.setText(timeLeftText);
                }
            });

    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //  Point offset = new Point((int) motionEvent.getX(), (int) motionEvent.getY());

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            else {
                // view.setVisibility(View.VISIBLE);
                return false;
            }
        }
    }


    private final class MyNextTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //  Point offset = new Point((int) motionEvent.getX(), (int) motionEvent.getY());

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            else {
                // view.setVisibility(View.VISIBLE);
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {


        @Override
        public boolean onDrag(View v, DragEvent event) {
            // rl.removeView(iv);

            final int evX = (int) event.getX();
            final int evY = (int) event.getY();


            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ImageView mapView = (ImageView)view;
                    int letrval = (int)view.getTag();
                    Log.e("Log",letrval+"");
                    iv = new ImageView(getApplicationContext());
                    iv.setImageResource(letrval);
                    iv.setTag(letrval);
                    iv.setOnTouchListener(new Level_03.MyNextTouchListener());
                    //iv.setBackgroundColor(Color.YELLOW);
                    params2 = new  FrameLayout.LayoutParams(  mapView.getWidth(), mapView.getHeight());
                    params2.leftMargin = evX-(mapView.getWidth()/2);
                    params2.topMargin = evY-(mapView.getHeight()/2);
                    r2.addView(iv, params2);
                    // Toast.makeText(Level_04.this, "70", Toast.LENGTH_SHORT).show();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    break;

                default:
                    break;
            }


            return true;
        }
    }
    class MyDragListener2 implements View.OnDragListener {


        @Override
        public boolean onDrag(View v, DragEvent event) {
            // rl.removeView(iv);

            final int evX = (int) event.getX();
            final int evY = (int) event.getY();


            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ImageView mapView = (ImageView)view;
                    int letrval = (int)view.getTag();
                    Log.e("Log",letrval+"");
                    iv = new ImageView(getApplicationContext());
                    iv.setImageResource(letrval);
                    iv.setTag(letrval);
                    iv.setOnTouchListener(new Level_03.MyNextTouchListener());
                    // iv.setBackgroundColor(Color.YELLOW);
                    params2 = new  FrameLayout.LayoutParams(  mapView.getWidth(), mapView.getHeight());
                    params2.leftMargin = evX-(mapView.getWidth()/2);
                    params2.topMargin = evY-(mapView.getHeight()/2);
                    r2.addView(iv, params2);

                    if (view.getId() == R.id.imgNum_20) {
                        Toast.makeText(Level_03.this, "20", Toast.LENGTH_SHORT).show();
                        upperImageViews.setImageResource(R.drawable.box);
                        iScore = iScore + 2;
                        // Toast.makeText(Level_04.this, "Success" ,Toast.LENGTH_SHORT).show();
                        // addData();

                    } else if (view.getId() == R.id.imgNum_30) {
                        Toast.makeText(Level_03.this, "30", Toast.LENGTH_SHORT).show();
                        upperImageViews.setImageResource(R.drawable.redbox);
                        iScore = iScore - 2;
                    } else if (view.getId() == R.id.imgNum_40) {
                        Toast.makeText(Level_03.this, "40", Toast.LENGTH_SHORT).show();
                        upperImageViews.setImageResource(R.drawable.box);
                        iScore = iScore + 2;

                    } else if (view.getId() == R.id.imgNum_50) {
                        Toast.makeText(Level_03.this, "50", Toast.LENGTH_SHORT).show();
                        upperImageViews.setImageResource(R.drawable.redbox);
                        iScore = iScore - 2;
                    } else if (view.getId() == R.id.imgNum_60) {
                        Toast.makeText(Level_03.this, "60", Toast.LENGTH_SHORT).show();
                        upperImageViews.setImageResource(R.drawable.box);
                        iScore = iScore + 2;
                    }
                    score.setText(String.valueOf(iScore));

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    break;

                default:
                    break;
            }


            return true;
        }
    }

    public int getiScore(){
        return iScore;
    }
    public void addData() {
        String score = Integer.toString(iScore);
        //String name2 = name;

        boolean data = myDB.addInfo_Score_Table("", "",iLevel,score);
        if (data = true) {
            Toast.makeText(Level_03.this, "Success", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(Level_03.this, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }


    public void getAllInfo() {



        try {
            // Toast.makeText(Level_04.this, "Data Not Found", Toast.LENGTH_SHORT).show();
            Cursor cursor = myDB.getInfo_Score_Table();
            Cursor cursor1 = myDB.getHieghestSocre();

            if (cursor.getCount() == 0) {
                Toast.makeText(Level_03.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                showMassages("Error", "Data not Found!!!");
                return;
            } else {


                Toast.makeText(Level_03.this, "Data Found", Toast.LENGTH_SHORT).show();

                StringBuffer buffer = new StringBuffer();
                StringBuffer buffer1 = new StringBuffer();

                while (cursor.moveToNext()) {
                    buffer.append("Score: " + cursor.getString(4) + "\n");
                    // buffer.append(""+cursor.getString(1)+")\n");

                    //cursor.close();

                }
                cursor.close();
                while (cursor1.moveToNext()) {
                    buffer1.append("Highest Sore: " + cursor1.getString(4));
                    // buffer1.append(""+cursor1.getString(1)+")");
                }
                cursor1.close();
                showMassages("" + buffer1.toString(), buffer.toString());

            }


        } catch(Exception e)

        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("" + e);
            builder.show();
        }
    }



    public void showMassages(String title, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }






    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }
    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 4) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "Reset Success", Toast.LENGTH_SHORT)
                    .show();

            //playAudio("snap.mp3");
            finish();
            startActivity(getIntent());


            color = !color;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
           // Toast.makeText(this, "Sound stop",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}

