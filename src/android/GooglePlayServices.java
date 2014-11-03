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

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
//import android.app.Activity;

import android.os.Bundle;
import org.apache.cordova.*;

import android.util.Log;

public class GooglePlayServices extends CordovaPlugin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
 
 private static final String LOGTAG = "GooglePlayServices";
 
 private CordovaInterface cordova            = null;
 private CordovaWebView   webView            = null;
 private GoogleApiClient  mGoogleApiClient   = null;
 private CallbackContext  tryConnectCallback = null;
 
 @Override public void initialize (CordovaInterface initCordova, CordovaWebView initWebView) {
  cordova  = initCordova;
  webView  = initWebView;
  super.initialize (cordova, webView);
 }
 
 @Override public void onConnectionFailed (ConnectionResult result) {
  if (!result.hasResolution()) {Log.w (LOGTAG, "Error: no resolution. Google Play Services connection failed."); return;}
  try {
   result.startResolutionForResult (cordova.getActivity(), result.SIGN_IN_REQUIRED);
  } catch (SendIntentException e) {
   // There was an error with the resolution intent. Try again.
   mGoogleApiClient.connect ();
  }
 }
 
 @Override public void onConnected (Bundle connectionHint) {
  Games.setViewForPopups (mGoogleApiClient, webView);
  if (tryConnectCallback != null) {
   String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
   tryConnectCallback.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
   tryConnectCallback = null;
  }
 }
 
 @Override public void onActivityResult (int requestCode, int responseCode, Intent intent) {
  if (!mGoogleApiClient.isConnecting()) mGoogleApiClient.connect ();
 }
 @Override public void onConnectionSuspended (int cause) {
  mGoogleApiClient.connect ();
 }
 
 public boolean execute (String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
  if        ("getPlayerId".equals(action)) {
   String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
  } else if ("tryConnect".equals(action)) {
   // Passes the callbackContext to tryConnect ().
   // tryConnect runs the callback with a value of false if Google Play Services isn't available.
   tryConnect (callbackContext);
  }
  return true;
 }
 
 public void tryConnect (CallbackContext callbackContext) {
  boolean isGpsAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(cordova.getActivity()) == ConnectionResult.SUCCESS);
  if (!isGpsAvailable) {
   Log.w (LOGTAG, "Error: Google Play Services is not available.");
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, false));
   return;
  }
  tryConnectCallback = callbackContext;
  mGoogleApiClient = new GoogleApiClient.Builder (cordova.getActivity().getApplicationContext())
   .addConnectionCallbacks (this)
   .addOnConnectionFailedListener (this)
   .addApi (Games.API)
   .addScope (Games.SCOPE_GAMES)
   .build ();
  mGoogleApiClient.connect ();
 }
}
