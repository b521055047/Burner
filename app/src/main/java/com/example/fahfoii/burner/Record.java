package com.example.fahfoii.burner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by fahfoii on 8/27/14 AD.
 */
public class Record extends Activity{
    private TextView speed_mps,speed_kmph,averageSpeed,calories,distance,maxSpeed_txtvw;
    private Spinner activities;
    private Button startButton,pauseButton;
    private TextView timeValue;
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    private long timeInMilliSec = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isPause = true;
    private long counter;
    private float sumSpeed;
    private float maxSpeed;
    private int selectedActivity;
    private int age;
    private float height,weight;
    private boolean isMale;
    private int gender;
    private float METS;
    private void init() {
        counter = 0;
        sumSpeed = 0;
        maxSpeed = 0;
        selectedActivity = 0;
        Bundle extras = getIntent().getExtras();
        timeValue = (TextView)findViewById(R.id.timeTextview);
        age = Integer.parseInt(extras.getString("age"));
        height = Float.parseFloat(extras.getString("height"));
        weight = Float.parseFloat(extras.getString("weight"));
        isMale = extras.getBoolean("isMale");

        if (isMale) gender = 0;
        else gender = 1;

        Context context = getApplicationContext();
        speed_mps = (TextView)findViewById(R.id.speed);
        speed_kmph = (TextView)findViewById(R.id.speed2);
        averageSpeed = (TextView)findViewById(R.id.avg);
        maxSpeed_txtvw = (TextView)findViewById(R.id.maxSpeed);
        calories = (TextView)findViewById(R.id.cal);
        distance = (TextView)findViewById(R.id.distance);
        startButton = (Button)findViewById(R.id.startButton);
        pauseButton = (Button)findViewById(R.id.pauseButton);


        activities = (Spinner)findViewById(R.id.activities);
        activities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    startButton.setEnabled(false);
                    pauseButton.setEnabled(false);
                    selectedActivity = i;
                    //Toast.makeText(getBaseContext(),String.valueOf(selectedActivity),Toast.LENGTH_SHORT).show();
                }
                else {
                    startButton.setEnabled(true);
                    pauseButton.setEnabled(true);
                    activities.setSelected(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        LocationManager locationManager = (LocationManager) this .getSystemService(Context.LOCATION_SERVICE); // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(isPause != true) {
                    counter++;
                    float smps = location.getSpeed();
                    float skmps = (float)(smps * 3.6);
                    sumSpeed += skmps;
                    float avg = (float)(sumSpeed/counter);
                    speed_mps.setText(String.format("%.02f",smps));
                    speed_kmph.setText(String.format("%.02f",skmps));
                    averageSpeed.setText(String.format("%.02f",avg));

                    String t = timeValue.getText().toString();
                    String [] st = t.split(":");
                    float mins = Float.parseFloat(st[0]);
                    float secs = Float.parseFloat(st[1]);
                    float hours = ((mins+(secs/60))/60);
                    float dst = avg*hours;
                    METS = getMETS(selectedActivity, skmps);
                    float cal = getBurnedCalories(gender,height,weight,age,METS,hours);
                    calories.setText(String.format("%.02f",cal));
                    distance.setText(String.format("%.02f",dst));

                    //Set Max speed
                    if(maxSpeed < skmps)
                        maxSpeed = skmps;
                    maxSpeed_txtvw.setText(String.format("%.02f",maxSpeed));
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        init();
    }

    public void clickStart(View v) {
        if(isPause) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            isPause = false;

            /**** must recode this line.... next time*****/
            activities.setEnabled(false);
        }
    }
    public void clickPause(View v) {
        if(!isPause) {
            timeSwapBuff += timeInMilliSec;
            customHandler.removeCallbacks(updateTimerThread);
            isPause = true;
        }
//        String t = timeValue.getText().toString();
//        String [] st = t.split(":");
//
//        Toast.makeText(getApplication(), "Min :"+st[0]+" Sec :"+st[1]+"\n Cal :"+cal,Toast.LENGTH_SHORT).show();
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliSec = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliSec;

            int secs = (int)(updateTime/1000);
            int mins = secs/60;
            secs = secs % 60;
            int milliSec = (int)(updateTime % 1000);
            timeValue.setText(""+mins+":"+String.format("%02d",secs)+":"+String.format("%03d",milliSec));
            customHandler.postDelayed(this, 0);
        }
    };

    private float getBurnedCalories(int gender, float height, float weight, int age, float MET, float time) {
        float cal = 0f;
        //Male
        if(gender == 0) {
            cal = (float)((((13.75*weight)+(5*height)-(6.76*age)+66)/24)*MET*time);
        }
        else {
            cal = (float)((((9.56*weight)+(1.85*height)-(4.68*age)+665)/24)*MET*time);
        }
        System.out.println(cal);
        return cal;
    }

    private float getMETS(int selectedActivity,float speed) {
        // 1 - walking
        // 2 - running
        // 3 - cycling
        // speed unit is km/h.
        if(selectedActivity == 1) {
            //ignore case uphill or down hill, next time
            if(speed < 3.218688) {
                return 2.0f;
            }
            else if(speed < 4.02336) {
                return 2.5f;
            }
            else if(speed < 4.828032) {
                return 3.0f;
            }
            else if(speed < 5.632704) {
                return 3.3f;
            }
            else if(speed < 6.437376) {
                return 3.8f;
            }
            else if(speed < 7.242048) {
                return 5.0f;
            }
            else if(speed < 8.046720) {
                return 6.3f;
            }
            //Fix rate
            else {
                return 8.0f;
            }
        }
        else if(selectedActivity == 2){
            //ignore case uphill or down hill, next time
            if(speed <= 5.102334) {
                //jogging
                return 4.5f;
            }
            else if(speed > 5.102334 && speed <= 8.046720) {
                return 8.0f;
            }
            else if(speed <= 8.3685888) {
                return 9.0f;
            }
            else if(speed <= 9.656064) {
                return 10.0f;
            }
            else if(speed <= 10.782605) {
                return 11.0f;
            }
            else if(speed <= 11.265408) {
                return 11.5f;
            }
            else if(speed <= 12.07008) {
                return 12.5f;
            }
            else if(speed <= 12.874752) {
                return 13.5f;
            }
            else if(speed <= 13.840358) {
                return 14.0f;
            }
            else if(speed <= 14.484096) {
                return 15.0f;
            }
            else if(speed <= 16.09344) {
                return 16.0f;
            }
            else {
                return 18.0f;
            }

        }
        else if (selectedActivity == 3){
            //ignore case uphill or down hill, next time
            if(speed < 3.218688) {
                return 2.0f;
            }
            else if(speed < 16.09344) {
                return 4.0f;
            }
            else if(speed >= 16.09344 && speed <= 19.151194) {
                return 6.0f;
            }
            else if(speed >= 19.312128 && speed <= 22.369882) {
                return 8.0f;
            }
            else if(speed >= 22.530816 && speed <= 25.58857) {
                return 10.0f;
            }
            else if(speed >= 25.749504 && speed <= 30.577536) {
                return 12.0f;
            }
            else if(speed >= 32.18688 && speed <= 40.342289) {
                return 16.0f;
            }
            //No data
            else {
                return 18.0f;
            }
        }
        else {
            return 0.0f;
        }
    }
}
//Calorie Burn = (BMR / 24) x MET x T
//For males: BMR = (13.75 x WKG) + (5 x HC) - (6.76 x age) + 66
//For females: BMR = (9.56 x WKG) + (1.85 x HC) - (4.68 x age) + 655
//        BMR = Basal Metabolic Rate (over 24 hours)
//        MET = Metabolic Equivalent (for selected activity)
//        T = Activity duration time (in hours)
//        HC = Height (in centimetres)
//        WKG = Weight (in kilograms)