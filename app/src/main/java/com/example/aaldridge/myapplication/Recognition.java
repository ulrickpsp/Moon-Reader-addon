package com.example.aaldridge.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaldridge on 09/06/2016.
 */
public class Recognition extends Service implements android.speech.RecognitionListener {

    private SpeechRecognizer speech = null;
    private Intent intent;
    DisplayClass instanceDisplay;
    LocalBroadcastManager broadcaster;

    public Recognition() {
    }


    //============================================================
    //              Allows to init recognising
    //============================================================
    public void startRecognising()
    {
        //This is called only first time. Initializes SpeechRecognizer.
        if(speech == null) {
            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
            sendNotification("Service running", true, 1);
        }

        //Recognizer parameters
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        //Starts listening
        speech.startListening(intent);
    }

    //============================================================
    //                    Finish recognition
    //============================================================
    public void finishRecognising(){

        //Cancel persistent notification (running service), stop recording and send broadcast to stop service
        if(speech != null)
        {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            speech.cancel();
            speech.destroy();
            updateUI();
        }
    }

    //Send broadcast to MainActivity in order to stop service
    private void updateUI() {
        Intent intent = new Intent("RecognitionServiceErrorNotifier");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //==============================================================================
    //    Recognition system methods: Only last two are used but all are necessary
    //==============================================================================
    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onbeginningofspeech");
    }
    public void onBufferReceived(byte[] arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onbufferreceived");
    }
    public void onEndOfSpeech() {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onendofspeech");
    }
    public void onEvent(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onevent");
    }
    public void onPartialResults(Bundle arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onpartialresults");
    }
    public void onReadyForSpeech(Bundle arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onreadyforspeech");
    }
    public void onRmsChanged(float arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onrmschanged");
    }

    //Called everytime a word or sentence is recognized
    public void onResults(Bundle arg0) {

        //Get results
        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        //Check if any result matches known commands
        for (String string : matches){

            //Next page command
            if (string.equalsIgnoreCase(Commands.PÁGINA_SIGUIENTE)){

                List<Integer> coordinates = instanceDisplay.getCoordinates();
                Log.d("log", "Received: " + string);
                Commands.performClick(coordinates.get(2), coordinates.get(3));
                break;
            }

            //Previous page command
            else if(string.equalsIgnoreCase(Commands.PÁGINA_ANTERIOR)){

                List<Integer> coordinates = instanceDisplay.getCoordinates();
                Log.d("log", "Received: " + string);
                Commands.performClick(coordinates.get(0), coordinates.get(1));
                break;
            }

            Log.d("log", "Received: " + string);
        }

        //After performing task, start again
        startRecognising();
    }

    public void onError(int errorCode) {

        //This is not an error per-se. The way this app works allows this.
        if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH))
        {
            Log.d("log", "Nothing recognized");
            startRecognising();
        }

        //On problematic error, stop service and notify user
        else{
            Log.d("log", "Error: " + errorCode);
            sendNotification("Error: " + errorCode, false, 0);
            finishRecognising();
        }
    }


    //===============================================================================
    // This following methods handle the background service called from MainActivity
    //===============================================================================
    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startRecognising();
        instanceDisplay = DisplayClass.getInstance();
        Log.d("log", "Service Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        try {
            finishRecognising();
            Log.d("log", "Service Finished");
        }catch (Exception ex){}
    }


    //Need to detect screen rotation to update coordinates properly
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            instanceDisplay.updateCoordinates(this);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            instanceDisplay.updateCoordinates(this);
        }
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, Boolean persistent, int ID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setOngoing(persistent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID, noBuilder.build()); //0 = ID of notification
    }


}
