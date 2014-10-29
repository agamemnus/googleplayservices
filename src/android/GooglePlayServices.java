package com.flyingsoftgames.googleplayservices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

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
 
 private CordovaInterface cordova;
 private CordovaWebView webView;
 private GoogleApiClient mGoogleApiClient;
 private CallbackContext tryConnectCallback = null;
 
 @Override public void initialize (CordovaInterface initCordova, CordovaWebView initWebView) {
  super.initialize (cordova, webView);
  cordova = initCordova;
  webView = initWebView;
 }
 
 public void onConnectionFailed (ConnectionResult result) {
  if (!result.hasResolution()) return;
  try {
   result.startResolutionForResult (cordova.getActivity(), result.SIGN_IN_REQUIRED);
  } catch (SendIntentException e) {
   // There was an error with the resolution intent. Try again.
   mGoogleApiClient.connect ();
  }
 }
 
 public void onConnected (Bundle connectionHint) {
  String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
  if (tryConnectCallback != null) {
   tryConnectCallback.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
   tryConnectCallback = null;
  }
 }
 
 public void onActivityResult (int requestCode, int responseCode, Intent intent) {
  if (!mGoogleApiClient.isConnecting()) mGoogleApiClient.connect ();
 }
 public void onConnectionSuspended (int cause) {mGoogleApiClient.connect ();}
 
 public boolean execute (String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
  if        ("getPlayerId".equals(action)) {
   String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
  } else if ("initialize".equals(action)) {
   // Passes the callbackContext to tryConnect ().
   // tryConnect runs the callback with a value of false if Google Play Services isn't available.
   tryConnect (callbackContext);
  }
  return true;
 }
 
 public void tryConnect (CallbackContext callbackContext) {
  boolean isGpsAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(cordova.getActivity()) == ConnectionResult.SUCCESS);
  Log.w (LOGTAG, String.format("isGooglePlayServicesAvailable: %s",  isGpsAvailable ? "true" : "false"));
  if (!isGpsAvailable) {
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, false));
   return;
  }
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
}
