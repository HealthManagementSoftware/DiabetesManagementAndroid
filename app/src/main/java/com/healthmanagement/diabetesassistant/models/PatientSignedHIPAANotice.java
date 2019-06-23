package com.healthmanagement.diabetesassistant.models;

import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.interfaces.Syncable;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PatientSignedHIPAANotice implements Syncable
{
    private int              id;
    private String           remoteId;
    private String           patientUserName;
    private String           patientId;
    private PatientSingleton Patient;

    private String             noticeId;
    private HIPAAPrivacyNotice HIPAAPrivacyNotice;

    private Date    signedAt;
    private Date    updatedAt;
    private boolean synced;

    public PatientSignedHIPAANotice()
    {
        this.HIPAAPrivacyNotice = new HIPAAPrivacyNotice();
        remoteId = UUID.randomUUID().toString();
        synced = false;

    } // constructor

    public static PatientSignedHIPAANotice copyFrom( String string ) throws JSONException
    {
        JSONObject jObj = new JSONObject( string );
        PatientSignedHIPAANotice notice = new PatientSignedHIPAANotice();
        if( jObj.has( DB.KEY_REMOTE_ID )
                && !jObj.getString( DB.KEY_REMOTE_ID ).isEmpty()
                && !jObj.getString( DB.KEY_REMOTE_ID ).equals( "null" ) )
            notice.setRemoteId( DB.KEY_REMOTE_ID );
        if( jObj.has( DB.KEY_NOTICE_ID )
                && !jObj.getString( DB.KEY_NOTICE_ID ).isEmpty()
                && !jObj.getString( DB.KEY_NOTICE_ID ).equals( "null" ) )
            notice.setNoticeId( jObj.getString( DB.KEY_NOTICE_ID ) );
        if( jObj.has( DB.KEY_PATIENT_ID )
                && !jObj.getString( DB.KEY_PATIENT_ID ).isEmpty()
                && !jObj.getString( DB.KEY_PATIENT_ID ).equals( "null" ) )
            notice.setPatientId( jObj.getString( DB.KEY_PATIENT_ID ) );
        return notice;

    } // copyFrom


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

    public String getPatientUserName()
    {
        return patientUserName;
    }

    public void setPatientUserName( String patientUserName )
    {
        this.patientUserName = patientUserName;
    }

    public String getPatientId()
    {
        return patientId;
    }

    public void setPatientId( String patientId )
    {
        this.patientId = patientId;
    }

    public PatientSingleton getPatient()
    {
        return Patient;
    }

    public void setPatient( PatientSingleton patient )
    {
        Patient = patient;
    }

    public String getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId( String noticeId )
    {
        this.noticeId = noticeId;
    }

    public HIPAAPrivacyNotice getHIPAAPrivacyNotice()
    {
        return HIPAAPrivacyNotice;
    }

    public void setHIPAAPrivacyNotice( HIPAAPrivacyNotice HIPAAPrivacyNotice )
    {
        this.HIPAAPrivacyNotice = HIPAAPrivacyNotice;
    }

    public Date getSignedAt()
    {
        return signedAt;
    }

    public void setSignedAt( Date signedAt )
    {
        this.signedAt = signedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt( Date updatedAt )
    {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isSynced()
    {
        return synced;
    }

    @Override
    public void setSynced( boolean isSynced )
    {
        synced = isSynced;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject signedHIPAANotice = new JSONObject();

        if( remoteId != null && !remoteId.isEmpty() )
            signedHIPAANotice.put( DB.KEY_REMOTE_ID, remoteId );
        if( patientId != null && !patientId.isEmpty() )
            signedHIPAANotice.put( DB.KEY_PATIENT_ID, patientId );
        if( patientUserName != null && !patientUserName.isEmpty() )
            signedHIPAANotice.put( DB.KEY_PATIENT_USER_NAME, patientUserName );
        if( noticeId != null && !noticeId.isEmpty() )
            signedHIPAANotice.put( DB.KEY_NOTICE_ID, noticeId );
        try
        {
            DateFormat df = new SimpleDateFormat( "MM/dd/yyyy HH:mm a", Locale.US );
            if( signedAt != null )
                signedHIPAANotice.put( DB.KEY_CREATED_AT, df.format( signedAt ) );
            if( updatedAt != null )
                signedHIPAANotice.put( DB.KEY_UPDATED_AT, df.format( updatedAt ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return signedHIPAANotice;

    } // toJSONObject

} // class
