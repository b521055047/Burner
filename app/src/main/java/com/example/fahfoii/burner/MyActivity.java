package com.example.fahfoii.burner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MyActivity extends Activity{

    Context context;

    private String[] actionBars;
    private void init() {
        actionBars = new String[]{"Profile","Record","Challenges"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, actionBars);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                switch (i) {
                    case 0: break;
                    case 1: gotoPageRecord(); break;
                    case 2: break;
                    default: break;
                };
                return true;
            }
        };
        getActionBar().setListNavigationCallbacks(adapter, navigationListener);
    }

    private void gotoPageRecord() {
        Intent intent = new Intent(this, Record.class);

        intent.putExtra("isMale",((RadioButton)findViewById(R.id.maleRadioButton)).isChecked());
        intent.putExtra("age",((TextView)findViewById(R.id.myAge)).getText());
        intent.putExtra("height",((TextView)findViewById(R.id.myHeight)).getText());
        intent.putExtra("weight",((TextView)findViewById(R.id.myWeight)).getText());
        startActivity(intent);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        init();

//        context = getApplicationContext();
//        tv = (TextView)findViewById(R.id.velocity);
//        tv2 = (TextView)findViewById(R.id.velocity2);
//        LocationManager locationManager = (LocationManager) this .getSystemService(Context.LOCATION_SERVICE); // Define a listener that responds to location updates
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//
//                tv.setText(String.valueOf(location.getSpeed()));
//                tv2.setText(String.valueOf(location.getSpeed()*3.6));
//                //location.getLatitude();
//                //Toast.makeText(context, "Current speed:" + location.getSpeed(), Toast.LENGTH_SHORT).show();
//            }
//            public void onStatusChanged(String provider, int status, Bundle extras) { }
//            public void onProviderEnabled(String provider) { }
//            public void onProviderDisabled(String provider) { }
//        };
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
