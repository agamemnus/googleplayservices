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
 
 private CordovaInterface cordova;
 private CordovaWebView webView;
 private GoogleApiClient mGoogleApiClient;
 private CallbackContext tryConnectCallback = null;
 private Activity activity;
 private Context context;
 private GooglePlayServices googlePlayServices;
 @Override public void initialize (CordovaInterface initCordova, CordovaWebView initWebView) {
  Log.w (LOGTAG, "initialize");
  cordova  = initCordova;
  webView  = initWebView;
  activity = cordova.getActivity();
  context  = activity.getApplicationContext();
  Log.w (LOGTAG, "YYY");
  googlePlayServices = this;
  super.initialize (cordova, webView);
 }
 
 public void onConnectionFailed (ConnectionResult result) {
  Log.w (LOGTAG, "onConnectionFailed");
  if (!result.hasResolution()) return;
  try {
   result.startResolutionForResult (cordova.getActivity(), result.SIGN_IN_REQUIRED);
  } catch (SendIntentException e) {
   // There was an error with the resolution intent. Try again.
   mGoogleApiClient.connect ();
  }
 }
 
 public void onConnected (Bundle connectionHint) {
 Log.w (LOGTAG, "onConnected");
  if (tryConnectCallback != null) {
   String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
   tryConnectCallback.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
   tryConnectCallback = null;
  }
 }
 
 public void onActivityResult (int requestCode, int responseCode, Intent intent) {
  Log.w (LOGTAG, "onActivityResult");
  if (!mGoogleApiClient.isConnecting()) mGoogleApiClient.connect ();
 }
 public void onConnectionSuspended (int cause) {
  Log.w (LOGTAG, "onConnectionSuspended");
  mGoogleApiClient.connect ();
 }
 
 public boolean execute (String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
  Log.w (LOGTAG, "execute");
  if        ("getPlayerId".equals(action)) {
   String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
  } else if ("doInitialize".equals(action)) {
   // Passes the callbackContext to tryConnect ().
   // tryConnect runs the callback with a value of false if Google Play Services isn't available.
   tryConnect (callbackContext);
  }
  return true;
 }
 
 public void tryConnect (CallbackContext callbackContext) {
  Log.w (LOGTAG, "tryConnect");
  if (cordova == null) Log.w (LOGTAG, "null");
  if (webView == null) Log.w (LOGTAG, "null");
  boolean isGpsAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS);
  Log.w (LOGTAG, String.format("isGooglePlayServicesAvailable: %s",  isGpsAvailable ? "true" : "false"));
  if (!isGpsAvailable) {
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, false));
   return;
  }
  tryConnectCallback = callbackContext;
  mGoogleApiClient = new GoogleApiClient.Builder (context)
   .addConnectionCallbacks (googlePlayServices)
   .addOnConnectionFailedListener (googlePlayServices)
   .addApi (Games.API)
   .addScope (Games.SCOPE_GAMES)
   .build ();
  Games.setViewForPopups (mGoogleApiClient, webView);
  mGoogleApiClient.connect ();
 }
}
