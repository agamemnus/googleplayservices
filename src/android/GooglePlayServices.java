package com.flyingsoftgames.googleplayservices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.Games;
  
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import org.apache.cordova.*;

import java.io.IOException;

import android.util.Log;

public class GooglePlayServices extends CordovaPlugin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
 
 private static final String LOG_TAG = "GooglePlayServices";
 private static final int REQ_SIGN_IN_REQUIRED = 55664;
 
 public static GoogleApiClient mGoogleApiClient   = null;
 public CallbackContext        tryConnectCallback = null;
 public String                 accessToken        = "";
 private int                   connectionAttempts = 0;
 @Override public void onConnectionFailed (ConnectionResult result) {
  String errormessage = result.toString();
  Log.w (LOG_TAG, errormessage);
  connectionAttempts += 1;
  if (!result.hasResolution() || connectionAttempts >= 2) {
   Log.w (LOG_TAG, "Error: no resolution. Google Play Services connection failed.");
   tryConnectCallback.error ("Error: " + errormessage + "."); tryConnectCallback = null;
   return;
  }
  try {
   result.startResolutionForResult (cordova.getActivity(), result.getErrorCode());
  } catch (SendIntentException e) {
   // There was an error with the resolution intent. Try again.
   mGoogleApiClient.connect ();
  }
 }
 
 @Override public void onConnected (Bundle connectionHint) {
  String mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
  new RetrieveTokenTask().execute (mAccountName);
  Games.setViewForPopups (mGoogleApiClient, webView);
 }
 
 public void onActivityResult (int requestCode, int responseCode, Intent intent) {
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
   tryConnect (callbackContext);
  } else if ("getAccessToken".equals(action)) {
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, accessToken));
  }
  return true;
 }
 
 // tryConnect runs the callback with a value of false if Google Play Services isn't available.
 public void tryConnect (CallbackContext callbackContext) {
  boolean isGpsAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(cordova.getActivity()) == ConnectionResult.SUCCESS);
  if (!isGpsAvailable) {
   callbackContext.sendPluginResult (new PluginResult (PluginResult.Status.OK, false));
   return;
  }
  tryConnectCallback = callbackContext;
  mGoogleApiClient = new GoogleApiClient.Builder (cordova.getActivity().getApplicationContext())
   .addConnectionCallbacks (this)
   .addOnConnectionFailedListener (this)
   .addApi (Games.API)
   .addScope (Games.SCOPE_GAMES)
   .addApi(Plus.API)
   .addScope(Plus.SCOPE_PLUS_PROFILE)
   .addScope(Plus.SCOPE_PLUS_LOGIN)
   .build ();
  mGoogleApiClient.connect ();
 }
 
 
 private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
  @Override protected String doInBackground (String... params) {
   String accountName = params[0];
   String scope = "oauth2:" + Scopes.PROFILE + " " + "email";
   Context context = cordova.getActivity().getApplicationContext();
   try {
    accessToken = GoogleAuthUtil.getToken (context, accountName, scope);
   } catch (IOException e) {
    String errormessage = e.getMessage();
    Log.e (LOG_TAG, errormessage);
    if (tryConnectCallback != null) tryConnectCallback.error ("Error: " + errormessage + "."); tryConnectCallback = null;
   } catch (UserRecoverableAuthException e) {
    cordova.getActivity().startActivityForResult (e.getIntent(), REQ_SIGN_IN_REQUIRED);
   } catch (GoogleAuthException e) {
    String errormessage = e.getMessage();
    Log.e (LOG_TAG, errormessage);
    if (tryConnectCallback != null) tryConnectCallback.error ("Error: " + errormessage + "."); tryConnectCallback = null;
   }
   return accessToken;
  }
  
  @Override protected void onPostExecute (String newAccessToken) {
   super.onPostExecute (newAccessToken);
   accessToken = newAccessToken;   
   if (tryConnectCallback != null) {
    String playerId = Games.Players.getCurrentPlayerId (mGoogleApiClient);
    tryConnectCallback.sendPluginResult (new PluginResult (PluginResult.Status.OK, playerId));
    tryConnectCallback = null;
   }
  }
 }
}
