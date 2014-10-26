package com.flyingsoftgames.googleplayservices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.Games;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.app.Activity;

import android.os.Bundle;
import org.apache.cordova.*;

public class GooglePlayServices extends CordovaPlugin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
 
 private static final String LOGTAG = "GooglePlayServices";
 
 private GoogleApiClient mGoogleApiClient;
 
 @Override public void initialize (CordovaInterface cordova, CordovaWebView webView) {
  super.initialize (cordova, webView);
  boolean isGpsAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(cordova.getActivity()) == ConnectionResult.SUCCESS);
  Log.w (LOGTAG, String.format("isGooglePlayServicesAvailable: %s",  isGpsAvailable ? "true" : "false"));
  if (!isGpsAvailable) return;
  Activity activity = cordova.getActivity();
  Context context   = activity.getApplicationContext();
  mGoogleApiClient = new GoogleApiClient.Builder (context)
   .addConnectionCallbacks (this)
   .addOnConnectionFailedListener (this)
   .addApi (Games.API)
   .addScope (Games.SCOPE_GAMES)
   .build ();
  mGoogleApiClient.connect ();
 }
 
 public void onConnectionFailed (ConnectionResult result) {
  if (!result.hasResolution()) return;
  Log.d (LOGTAG, result.toString());
  try {
   result.startResolutionForResult (cordova.getActivity(), result.SIGN_IN_REQUIRED);
  } catch (SendIntentException e) {
   // There was an error with the resolution intent. Try again.
   mGoogleApiClient.connect ();
  }
 }
 public void onConnected (Bundle connectionHint) {
  String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
  Log.w (LOGTAG, playerId);
 }
 public void onActivityResult (int requestCode, int responseCode, Intent intent) {
  if (!mGoogleApiClient.isConnecting()) mGoogleApiClient.connect ();
 }
 public void onConnectionSuspended (int cause) {mGoogleApiClient.connect ();}
 
 @Override public boolean execute (String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
  PluginResult result = null;
  String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
  if (action == "getPlayerId") result = new PluginResult(PluginResult.Status.OK, playerId);
  if (result != null) callbackContext.sendPluginResult (result);
  return true;
 }
}