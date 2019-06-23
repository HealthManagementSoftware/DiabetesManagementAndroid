package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.healthmanagement.diabetesassistant.actions.interfaces.ILoginAction;
import com.healthmanagement.diabetesassistant.activities.SettingsActivity;
import com.healthmanagement.diabetesassistant.enums.ErrorCode;
import com.healthmanagement.diabetesassistant.repositories.DbPatientRepository;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;
import com.healthmanagement.diabetesassistant.singletons.WebClientConnectionSingleton;
import com.healthmanagement.diabetesassistant.urlconnections.UrlConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class RemoteLoginAction implements ILoginAction
{
	private final String LOG_TAG    = getClass().getSimpleName();

	@Override
	public ErrorCode attemptLogin( String username, String password, Context context ) throws
			JSONException
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();

		HashMap<String, String> values = new HashMap<>();   // Create post values
		values.put( "Email", username );                    // Add to http request
		values.put( "Password", password );
		//			values.put( "RememberMe", true );

		if( DEBUG ) Log.i( LOG_TAG,
				"Email: " + username + "; Password: " + password.substring( 0, 2 ) + "..." );

		WebClientConnectionSingleton connection =
				WebClientConnectionSingleton.getInstance( context );
		String stringResponse = connection.sendLoginRequest( values );

		if( DEBUG ) Log.e( LOG_TAG, "Login returned: " + stringResponse );

		JSONObject jsonObject = new JSONObject( stringResponse );

		ErrorCode errorCode = ErrorCode.interpretErrorCode( jsonObject );
		if( errorCode != ErrorCode.NO_ERROR ) return errorCode;

		// Login was successful, so enter into the db:
		DbPatientRepository patientRepository = new DbPatientRepository( context );
		patientSingleton.setLoggedIn( true );
		PatientSingleton.copyFrom( stringResponse );        // Set patient's values from server
		patientRepository.create( patientSingleton );       // ...and insert into db

		return ErrorCode.NO_ERROR;

	} // attemptLogin

} // class
