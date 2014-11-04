package com.myapp;

import android.os.Bundle;
import org.apache.cordova.*;

import android.util.Log;

import android.content.Intent;

import com.flyingsoftgames.googleplayservices.GooglePlayServices;

public class MyApp extends CordovaActivity {

 private static final String LOGTAG = "MyApp";
 
 public void onActivityResult (int requestCode, int responseCode, Intent intent) {
  Log.w (LOGTAG, "onActivityResult");
  if (!GooglePlayServices.mGoogleApiClient.isConnecting()) GooglePlayServices.mGoogleApiClient.connect ();
 }
 
 @Override public void onCreate (Bundle savedInstanceState) {
  super.onCreate (savedInstanceState);
  super.init ();
  super.loadUrl(Config.getStartUrl());
 }
}
