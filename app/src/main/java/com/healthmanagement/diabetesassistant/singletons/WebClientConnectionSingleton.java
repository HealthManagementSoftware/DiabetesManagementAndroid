//--------------------------------------------------------------------------------------//
//																						//
// File Name:	WebClientConnectionSingleton.java										//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		10/08/2018																//
// Purpose:		An aggregate class to hold and use UrlConnection objects.				//
//																						//
//--------------------------------------------------------------------------------------//

package com.healthmanagement.diabetesassistant.singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.healthmanagement.diabetesassistant.activities.SettingsActivity;
import com.healthmanagement.diabetesassistant.urlconnections.UrlConnection;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

/**
 * WebClientConnectionSingleton
 * An aggregate class to hold and use UrlConnection objects and handle all requests to the
 * remote server.
 */
public class WebClientConnectionSingleton
{
    private static final String                       DEFAULT_HOST = "diabetes-dev.azurewebsites.net";
    private static final String                       DEFAULT_PORT = "443";
    private static       WebClientConnectionSingleton webClientConnection;    // The singleton to provide

    private static SharedPreferences sharedPreferences;
    private static String            host;
    private static int               port;
    private static String            protocol;
    private static boolean           use_ssl = true;

    private final static String  LOGIN_STRING             = "/API/AccountApi/Login";
    private final static String  REGISTER_STRING          = "/API/AccountApi/Register";
    private final static String  SYNC_PATIENT_DATA_STRING = "/API/Patient/Sync";
    private final static String  RETRIEVE_DOCTORS_STRING  = "/API/Doctor/List";
    private final static String  RETRIEVE_HIPAA_V_STRING    = "/API/HIPAAPrivacyNotice/ReadNewestVersion";
    private final static String  RETRIEVE_HIPAA_STRING    = "/API/HIPAAPrivacyNotice/ReadNewestNotice";
    private final        Context context;

    private UrlConnection loginConnection;             // The UrlConnections used to
    private UrlConnection registerConnection;          // 		connect to each URL that
    private UrlConnection retrieveDoctorsConnection;   //		may be used throughout the
    private UrlConnection syncPatientDataConnection;   //		application
    private UrlConnection retrieveHIPAAConnection;
    private UrlConnection retrieveHIPAAVersionConnection;
    private String        LOG_TAG = getClass().getSimpleName();

    private WebClientConnectionSingleton( Context context ) throws MalformedURLException
    {
        this.context = context;
        reset();

    } // constructor


    public static WebClientConnectionSingleton getInstance( Context context )
    {
        if( sharedPreferences == null )
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // Create the client *only* if it hasn't been created, the host or port has changed
        if( webClientConnection == null
                || use_ssl != sharedPreferences.getBoolean(
                SettingsActivity.PREF_USE_SSL, true )
                || !host.equals( sharedPreferences.getString(
                SettingsActivity.PREF_HOSTNAME, "localhost" ) )
                || port != Integer.parseInt(
                sharedPreferences.getString( SettingsActivity.PREF_PORT, "8080" ) ) )
        {
            try
            {
                webClientConnection = new WebClientConnectionSingleton( context );
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }

        } // if

        return webClientConnection;             // Otherwise, just return the client

    } // getInstance


    public boolean networkIsAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = cm != null
                ? cm.getActiveNetworkInfo()
                : null;
        return netInfo != null && netInfo.isConnected();

    } // networkIsAvailable


    public void reset() throws MalformedURLException
    {
        if( DEBUG ) Log.e( LOG_TAG, "Web connection reset!" );

        // We first get our user's preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        host = sharedPreferences.getString( SettingsActivity.PREF_HOSTNAME, DEFAULT_HOST );
        port = Integer.parseInt(
                sharedPreferences.getString( SettingsActivity.PREF_PORT, DEFAULT_PORT )
        );
        use_ssl = sharedPreferences.getBoolean( SettingsActivity.PREF_USE_SSL, true );

        protocol = use_ssl
                ? "https://"
                : "http://";

        // Instantiate all of the connections to the server that the app will use:
        String urlString = protocol + host + ":" + port + LOGIN_STRING;
        loginConnection = new UrlConnection( new URL( urlString ), context );

        urlString = protocol + host + ":" + port + REGISTER_STRING;
        registerConnection = new UrlConnection( new URL( urlString ), context );

        urlString = protocol + host + ":" + port + RETRIEVE_DOCTORS_STRING;
        retrieveDoctorsConnection = new UrlConnection( new URL( urlString ), context );

        urlString = protocol + host + ":" + port + RETRIEVE_HIPAA_STRING;
        retrieveHIPAAConnection = new UrlConnection( new URL( urlString ), context );

        urlString = protocol + host + ":" + port + RETRIEVE_HIPAA_V_STRING;
        retrieveHIPAAVersionConnection = new UrlConnection( new URL( urlString ), context );

        urlString = protocol + host + ":" + port + SYNC_PATIENT_DATA_STRING;
        syncPatientDataConnection = new UrlConnection( new URL( urlString ), context );

    } // reset


    public String sendLoginRequest( HashMap<String, String> values )
    {
        return loginConnection.performRequest( values );

    } // getHttpJsonResponse


    public String sendRegisterRequest( HashMap<String, String> values )
    {
        return registerConnection.performRequest( values );

    } // getHttpJsonResponse


    public String sendSyncPatientDataRequest( HashMap<String, String> values )
    {

        //		if( DEBUG ) Log.e( LOG_TAG, "Values: " + values.toString() );
        return syncPatientDataConnection.performRequest( values );

    } // sendSyncExerciseRequest


    public String sendSyncPatientDataRequest( JSONObject values )
    {
        //		if( DEBUG ) Log.e( LOG_TAG, "Values: " + values.toString() );
        return syncPatientDataConnection.performRequest( values );

    } // sendSyncExerciseRequest


    public String sendRetrieveDoctorsRequest( HashMap<String, String> values )
    {
        return retrieveDoctorsConnection.performRequest( values );

    } // sendSyncExerciseRequest


    public String sendRetrieveHIPAARequest( HashMap<String, String> values )
    {
        return retrieveHIPAAConnection.performRequest( values );

    } // sendSyncExerciseRequest


    public String sendRetrieveHIPAAVersionRequest( HashMap<String, String> values )
    {
        return retrieveHIPAAVersionConnection.performRequest( values );

    } // sendSyncExerciseRequest

} // class
