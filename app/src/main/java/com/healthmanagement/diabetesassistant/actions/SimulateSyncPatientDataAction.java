package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;
import android.util.Log;

import com.healthmanagement.diabetesassistant.actions.interfaces.ISyncPatientDataAction;

import org.json.JSONException;
import org.json.JSONObject;

public class SimulateSyncPatientDataAction implements ISyncPatientDataAction
{
	private String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncPatientData( Context context )
	{
		Log.e( LOG_TAG, "Simulating syncing patient data..." );
		JSONObject patientObject = new JSONObject();

		try
		{
			patientObject.put( "success", true );

		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		return patientObject.toString();

	} // syncPatientData

} // class
