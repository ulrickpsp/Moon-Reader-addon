package com.example.aaldridge.myapplication;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button bStart, bStop;
    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bStart = (Button) findViewById(R.id.b_start);
        bStart.setOnClickListener(this);

        bStop = (Button) findViewById(R.id.b_stop);
        bStop.setOnClickListener(this);

        image = (ImageView) findViewById(R.id.imageView2);

        //Needed to detect properly the coordinates
        DisplayClass.setAutoRotation(this);

        //Set coordinates at start time
        DisplayClass instance = DisplayClass.getInstance();
        instance.updateCoordinates(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.b_start){
            if(android.os.Build.VERSION.SDK_INT == 23) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
            else
            {
                startService(new Intent(this, Recognition.class));
                image.setImageDrawable(getResources().getDrawable(R.drawable.buttongreen));
            }
        }
        else{
            stopService(new Intent(this, Recognition.class));
            image.setImageDrawable(getResources().getDrawable(R.drawable.buttonred));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, Recognition.class));
                    image.setImageDrawable(getResources().getDrawable(R.drawable.buttongreen));
                } else {
                    Toast.makeText(MainActivity.this, "Permission deny to record audio", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private BroadcastReceiver mMessageReceiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain anydata
            Log.d("sohail","onReceive called");
            image.setImageDrawable(getResources().getDrawable(R.drawable.buttonred));
            stopService(new Intent(getApplicationContext(), Recognition.class));
        }
    };
}



