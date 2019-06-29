package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IHIPAANoticeRepository;
import com.healthmanagement.diabetesassistant.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DbHIPAAPrivacyNoticeRepository implements IHIPAANoticeRepository
{
    private ContentResolver contentResolver;

    public DbHIPAAPrivacyNoticeRepository( Context context )
    {
        contentResolver = context.getContentResolver();

    } // constructor


    @Override
    public void create( HIPAAPrivacyNotice item )
    {
        if( item.getRemoteId().isEmpty() )                        // Create an ID
            item.setRemoteId( UUID.randomUUID().toString() );
        // Insert into the database:
        contentResolver.insert( DAContentProvider.NOTICES_URI, putContentValues( item ) );

    } // create


    @Override
    public HIPAAPrivacyNotice read( String id )
    {
        Cursor cursor = contentResolver.query(
                DAContentProvider.NOTICES_URI,
                null,
                DB.KEY_REMOTE_ID + "=?",
                new String[]{ id },
                null );

        if( cursor != null && cursor.moveToFirst() )     // First, if we have one...
        {
            HIPAAPrivacyNotice entry = readFromCursor( cursor );    // ...Load it from the db
            cursor.close();

            return entry;                                // Return the entry we retrieved

        } // if

        return null;

    } // read


    @Override
    public ArrayList<HIPAAPrivacyNotice> readAll()
    {
        return readAll( null );

    } // readAll


    @Override
    public ArrayList<HIPAAPrivacyNotice> readAll( String id )
    {
        ArrayList<HIPAAPrivacyNotice> entries = new ArrayList<>();

        Cursor cursor = contentResolver.query( DAContentProvider.NOTICES_URI,
                null, null, null,
                DB.KEY_CREATED_AT + " DESC" );

        if( cursor != null && cursor.getCount() > 0 )
        {
            cursor.moveToFirst();
            do
            {
                HIPAAPrivacyNotice entry = readFromCursor( cursor );    // <-- MealItems set here
                entries.add( entry );                    // Add the entry to the ArrayList

            } while ( cursor.moveToNext() ); // do...while
            cursor.close();

        } // if

        return entries;

    } // readAll


    @Override
    public HIPAAPrivacyNotice readFromCursor( Cursor cursor )
    {
        HIPAAPrivacyNotice entry = new HIPAAPrivacyNotice();
        entry.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
        entry.setRemoteId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
        entry.setTitle( cursor.getString( cursor.getColumnIndex( DB.KEY_TITLE ) ) );
        entry.setNoticeText( cursor.getString( cursor.getColumnIndex( DB.KEY_NOTICE_TEXT ) ) );
        entry.setVersion( cursor.getString( cursor.getColumnIndex( DB.KEY_VERSION ) ) );

        String updatedAt = cursor.getString( cursor.getColumnIndex( DB.KEY_UPDATED_AT ) );
        if( updatedAt != null && !updatedAt.isEmpty() )
            // Convert the updatedAt string to a Date object:
            entry.setUpdatedAt( DateUtilities.convertStringToDate( updatedAt ) );

        String createdAt = cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) );
        if( createdAt != null && !createdAt.isEmpty() )
            // Convert the createdAt string to a Date object:
            entry.setCreatedAt( DateUtilities.convertStringToDate( createdAt ) );

        return entry;

    } // readFromCursor


    @Override
    public ContentValues putContentValues( HIPAAPrivacyNotice item )
    {
        ContentValues values = new ContentValues();
        if( item.getRemoteId() != null )
            values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
        if( item.getTitle() != null )
            values.put( DB.KEY_TITLE, item.getTitle() );
        if( item.getNoticeText() != null )
            values.put( DB.KEY_NOTICE_TEXT, item.getNoticeText() );
        if( item.getVersion() != null )
            values.put( DB.KEY_VERSION, item.getVersion() );
        if( item.getCreatedAt() != null )
            values.put( DB.KEY_CREATED_AT, item.getCreatedAt().toString() );
        if( item.getUpdatedAt() != null )
            values.put( DB.KEY_UPDATED_AT, item.getUpdatedAt().toString() );

        return values;

    } // putContentValues


    @Override
    public void update( int id, HIPAAPrivacyNotice item )
    {
        item.setUpdatedAt( new Date() );
        contentResolver.update( DAContentProvider.NOTICES_URI, putContentValues( item ),
                DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) } );

    } // update


    @Override
    public void delete( HIPAAPrivacyNotice item ) throws Exception
    {
        delete( item.getId() );

    } // delete


    @Override
    public void delete( int id ) throws Exception
    {
        throw new Exception();

    } // delete


    public void delete( String id )
    {
        contentResolver.delete( DAContentProvider.NOTICES_URI, DB.KEY_ID + "=?",
                new String[]{ String.valueOf( id ) } );

    } // delete


    @Override
    public void setAllSynced()
    {
        ContentValues values = new ContentValues();
        values.put( DB.KEY_SYNCED, true );
        contentResolver.update( DAContentProvider.NOTICES_URI,
                values, null, null );

    } // setAllSynced

    @Override
    public HIPAAPrivacyNotice readNewest()
    {
        Cursor cursor = contentResolver.query( DAContentProvider.NOTICES_URI,
                null, null, null,
                DB.KEY_CREATED_AT + " DESC" );

        if( cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() )
        {
            HIPAAPrivacyNotice entry = readFromCursor( cursor );    // <-- MealItems set here
            cursor.close();
            return entry;

        } // if

        return null;

    } // readNewest

} // class

