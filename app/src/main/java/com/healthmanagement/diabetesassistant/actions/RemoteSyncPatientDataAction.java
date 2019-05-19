package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.healthmanagement.diabetesassistant.actions.interfaces.ISyncPatientDataAction;
import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.enums.ErrorCode;
import com.healthmanagement.diabetesassistant.enums.SyncStatus;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientRepository;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;
import com.healthmanagement.diabetesassistant.singletons.WebClientConnectionSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class RemoteSyncPatientDataAction implements ISyncPatientDataAction
{
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncPatientData( Context context )
	{
		WebClientConnectionSingleton conn = WebClientConnectionSingleton.getInstance( context );
		IPatientRepository patientRepository = Dependencies.get( IPatientRepository.class );
		//		ContentValues values = patientRepository.putContentValues( PatientSingleton.getInstance() );
		String returnString = "";

		try
		{
			// ALWAYS load the latest data:
			Cursor cursor = context.getContentResolver().query(
					DAContentProvider.PATIENT_USERS_URI, null,
					DB.KEY_USER_LOGGED_IN + "=?",
					new String[]{ String.valueOf( 1 ) }, null
			);
			patientRepository.readFromCursor( PatientSingleton.getInstance(), cursor );
			//			if( cursor != null )
			//				cursor.close();

			JSONObject jsonPatient = DEBUG ? PatientSingleton.toJSONObject( SyncStatus.ALL )
					: PatientSingleton.toJSONObject( SyncStatus.UNSYNCED );
			//HashMap<String, String> hashMap = JsonUtilities.toMap( jsonPatient );	// OLD method
			if( DEBUG ) Log.e( LOG_TAG, "Json: " + jsonPatient.toString() );

			if( conn.networkIsAvailable() )
			{
				// Sync all of the un-synced data:
				returnString = conn.sendSyncPatientDataRequest( jsonPatient );
				if( DEBUG ) Log.e( LOG_TAG, "Server returned: " + returnString );

				ErrorCode returnCode = ErrorCode.interpretErrorCode( returnString );
				if( returnCode == ErrorCode.INVALID_LOGIN_TOKEN )
					PatientSingleton.eraseData();    	// Log out
				else if( returnCode == ErrorCode.NO_ERROR && !DEBUG ) // ONLY if not debugging:
					patientRepository.setAllSynced();			// Set all un-synced to now synced

			} // if
			else
				Log.e( LOG_TAG, "NETWORK UNAVAILABLE..." );

		}
		catch( JSONException e )
		{
			Log.e( LOG_TAG, "Error interpreting json." );
			e.printStackTrace();
		}
		catch( Exception e )
		{
			Log.e( LOG_TAG, "Error executing request." );
			e.printStackTrace();
		}

		if( DEBUG ) Log.e( "RemoteSyncPatientData", "Response String: " + returnString );

		return returnString;

	} // syncPatientData

} // class
