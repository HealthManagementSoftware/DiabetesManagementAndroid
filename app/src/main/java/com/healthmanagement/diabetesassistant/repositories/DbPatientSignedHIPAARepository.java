package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.PatientSignedHIPAANotice;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IHIPAANoticeRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientSignedHIPAARepository;
import com.healthmanagement.diabetesassistant.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DbPatientSignedHIPAARepository implements IPatientSignedHIPAARepository
{
    private ContentResolver        contentResolver;
    private IHIPAANoticeRepository hipaaNoticeRepository;

    public DbPatientSignedHIPAARepository( Context context )
    {
        contentResolver = context.getContentResolver();
        hipaaNoticeRepository = new DbHIPAAPrivacyNoticeRepository( context );

    } // constructor


    @Override
    public void create( PatientSignedHIPAANotice pSHN )
    {
        if( pSHN.getRemoteId().isEmpty() )                        // Create an ID
            pSHN.setRemoteId( UUID.randomUUID().toString() );

        contentResolver.insert( DAContentProvider.PSHN_URI, putContentValues( pSHN ) );

        if( pSHN.getHIPAAPrivacyNotice() != null &&
                hipaaNoticeRepository.read( pSHN.getHIPAAPrivacyNotice().getRemoteId() ) == null )
            hipaaNoticeRepository.create( pSHN.getHIPAAPrivacyNotice() );

    } // create


    @Override
    public PatientSignedHIPAANotice read( String patientUserName )
    {
        Cursor cursor = contentResolver.query(
                DAContentProvider.PSHN_URI,
                null,
                DB.KEY_PATIENT_USER_NAME + "=?",
                new String[]{ patientUserName },
                null );

        if( cursor != null && cursor.getCount() > 0 )                            // First, if we have a MealEntry...
        {
            cursor.moveToFirst();
            PatientSignedHIPAANotice entry = readFromCursor( cursor );    // ...Load it from the db
            cursor.close();

            entry.setHIPAAPrivacyNotice( hipaaNoticeRepository.read( entry.getNoticeId() ) );

            return entry;

        } // if

        return null;

    } // read


    @Override
    public ArrayList<PatientSignedHIPAANotice> readAll()
    {
        return readAll( null, null );

    } // readAll


    @Override
    public ArrayList<PatientSignedHIPAANotice> readAll( String patientId, String noticeId )
    {
        ArrayList<PatientSignedHIPAANotice> entries = new ArrayList<>();

        String selection = patientId != null && noticeId != null
                ? DB.KEY_PATIENT_ID + "=? AND " + DB.KEY_NOTICE_ID + "=?"
                : null;
        String[] selectionArgs = patientId != null && noticeId != null
                ? new String[]{ patientId, noticeId }
                : null;

        Cursor cursor = contentResolver.query( DAContentProvider.PSHN_URI,
                null, selection, selectionArgs,
                null );

        if( cursor != null && cursor.getCount() > 0 )
        {
            cursor.moveToFirst();
            do
            {
                PatientSignedHIPAANotice patientHipaaNotice = readFromCursor( cursor );
                entries.add( patientHipaaNotice );               // Add the entry to the ArrayList

            } while ( cursor.moveToNext() ); // do...while
            cursor.close();

        } // if

        return entries;

    } // readAll


    @Override
    public PatientSignedHIPAANotice readFromCursor( Cursor cursor )
    {
        PatientSignedHIPAANotice pSHN = new PatientSignedHIPAANotice();
        pSHN.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
        pSHN.setNoticeId( cursor.getString( cursor.getColumnIndex( DB.KEY_NOTICE_ID ) ) );
        pSHN.setPatientId( cursor.getString( cursor.getColumnIndex( DB.KEY_PATIENT_ID ) ) );
        pSHN.setPatientUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_PATIENT_USER_NAME ) ) );

        String updatedAt = cursor.getString( cursor.getColumnIndex( DB.KEY_UPDATED_AT ) );
        if( !updatedAt.isEmpty() )
            // Convert the updatedAt string to a Date object:
            pSHN.setUpdatedAt( DateUtilities.convertStringToDate( updatedAt ) );

        String signedAt = cursor.getString( cursor.getColumnIndex( DB.KEY_SIGNED_AT ) );
        if( !signedAt.isEmpty() )
            // Convert the signedAt string to a Date object:
            pSHN.setSignedAt( DateUtilities.convertStringToDate( signedAt ) );

        return pSHN;

    } // readFromCursor


    @Override
    public ContentValues putContentValues( PatientSignedHIPAANotice item )
    {
        ContentValues values = new ContentValues();
        if( item.getRemoteId() != null && !item.getRemoteId().isEmpty() )
            values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
        if( item.getNoticeId() != null && !item.getNoticeId().isEmpty() )
            values.put( DB.KEY_NOTICE_ID, item.getNoticeId() );
        if( item.getPatientId() != null && !item.getPatientId().isEmpty() )
            values.put( DB.KEY_PATIENT_ID, item.getPatientId() );
        if( item.getPatientUserName() != null && !item.getPatientUserName().isEmpty() )
            values.put( DB.KEY_PATIENT_USER_NAME, item.getPatientUserName() );
        if( item.getSignedAt() != null && item.getSignedAt() != null )
            values.put( DB.KEY_SIGNED_AT, item.getSignedAt().toString() );
        if( item.getUpdatedAt() != null && item.getUpdatedAt() != null )
            values.put( DB.KEY_UPDATED_AT, item.getUpdatedAt().toString() );
        return values;

    } // putContentValues


    @Override
    public void update( String patientUserName, String noticeId, PatientSignedHIPAANotice item )
    {
        item.setUpdatedAt( new Date() );

        contentResolver.update( DAContentProvider.PSHN_URI,
                putContentValues( item ),
                DB.KEY_PATIENT_USER_NAME + "=? AND " + DB.KEY_NOTICE_ID + "=?",
                new String[]{ patientUserName, noticeId } );

    } // update


    @Override
    public void delete( PatientSignedHIPAANotice item )
    {
        delete( item.getPatientId(), item.getNoticeId() );

    } // delete


    @Override
    public void delete( String fistId, String secondId )
    {
        contentResolver.delete( DAContentProvider.PSHN_URI,
                DB.KEY_PATIENT_ID + "=? AND " + DB.KEY_NOTICE_ID + "=?",
                new String[]{ fistId, secondId } );

    } // delete


    @Override
    public void delete( String patientId )
    {
        contentResolver.delete( DAContentProvider.PSHN_URI,
                DB.KEY_PATIENT_ID + "=?",
                new String[]{ patientId } );

    } // delete


    @Override
    public void setAllSynced()
    {
        ContentValues values = new ContentValues();
        values.put( DB.KEY_SYNCED, true );
        contentResolver.update( DAContentProvider.PSHN_URI,
                values, null, null );

    } // setAllSynced


    @Override
    public boolean exists( PatientSignedHIPAANotice notice )
    {
        Cursor cursor = contentResolver.query(
                DAContentProvider.PSHN_URI,
                null,
                DB.KEY_PATIENT_USER_NAME + "=? AND " + DB.KEY_NOTICE_ID + "=?",
                new String[]{ notice.getPatientUserName(), notice.getNoticeId() },
                null );

        return cursor != null && cursor.getCount() > 0;

    } // exists

} // class
