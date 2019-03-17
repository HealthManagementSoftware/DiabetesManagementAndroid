package com.healthmanagement.diabetesassistant.models;

import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.enums.BeforeAfter;
import com.healthmanagement.diabetesassistant.enums.WhichMeal;
import com.healthmanagement.diabetesassistant.models.interfaces.Syncable;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GlucoseEntry implements Syncable
{
	private int              id;
	private String           remoteId;
	private String           userName;
	private boolean          synced;
	private PatientSingleton patient;
	private float            measurement;
	// DEFAULT: mmol/L. May need conversion to display correctly
	//	private GlucoseUnits units;		    // enum	mmol/L or mg/dL
	private BeforeAfter      beforeAfter;   // enum representing before or after a meal
	private WhichMeal        whichMeal;     // enum representing which meal entry taken before/after
	private Date             createdAt;
	private Date             updatedAt;
	private long             timeStamp;


	public GlucoseEntry()
	{
		id = -1;
		remoteId = "";
		patient = PatientSingleton.getInstance();
		userName = patient.getUserName();
		measurement = 0f;
		beforeAfter = BeforeAfter.BEFORE;
		whichMeal = WhichMeal.OTHER;
		createdAt = new Date();
		updatedAt = createdAt;
		timeStamp = createdAt.getTime();

	} // constructor


	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId( String remoteId )
	{
		this.remoteId = remoteId;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp( long timeStamp )
	{
		this.timeStamp = timeStamp;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public boolean isSynced()
	{
		return synced;
	}

	public void setSynced( boolean synced )
	{
		this.synced = synced;
	}

	public float getMeasurement()
	{
		return measurement;
	}

	public void setMeasurement( float measurement )
	{
		this.measurement = measurement;
	}

	public BeforeAfter getBeforeAfter()
	{
		return beforeAfter;
	}

	public void setBeforeAfter( BeforeAfter beforeAfter )
	{
		this.beforeAfter = beforeAfter;
	}

	public WhichMeal getWhichMeal()
	{
		return whichMeal;
	}

	public void setWhichMeal( WhichMeal whichMeal )
	{
		this.whichMeal = whichMeal;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt( Date createdAt )
	{
		this.createdAt = createdAt;
	}

	public long getTimestamp()
	{
		return timeStamp;
	}

	public void setTimestamp( long timeStamp )
	{
		this.timeStamp = timeStamp;
	}

	public PatientSingleton getPatient()
	{
		return patient;
	}

	public void setPatient( PatientSingleton patient )
	{
		this.patient = patient;
	}

	public Date getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt( Date updatedAt )
	{
		this.updatedAt = updatedAt;
	}


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject glucoseEntry = new JSONObject();

		if( !remoteId.isEmpty() )
			glucoseEntry.put( DB.KEY_REMOTE_ID, remoteId );
		if( userName != null && !userName.isEmpty() )
			glucoseEntry.put( DB.KEY_USERNAME, userName );
		if( measurement > 0 )
			glucoseEntry.put( DB.KEY_GLUCOSE_MEASUREMENT, measurement );
		glucoseEntry.put( DB.KEY_GLUCOSE_BEFORE_AFTER, beforeAfter.getValue() );
		glucoseEntry.put( DB.KEY_WHICH_MEAL, whichMeal.getValue() );

		try
		{
			DateFormat df = new SimpleDateFormat( "MM/dd/yyyy HH:mm a", Locale.US );
			glucoseEntry.put( DB.KEY_CREATED_AT, df.format( createdAt ) );
			glucoseEntry.put( DB.KEY_UPDATED_AT, df.format( updatedAt ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		if( timeStamp > 0 )
			glucoseEntry.put( DB.KEY_TIMESTAMP, timeStamp );

		return glucoseEntry;

	} // toJSONObject

} // class
