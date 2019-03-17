package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.Doctor;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IDoctorRepository;

import java.util.ArrayList;

public class DbDoctorRepository implements IDoctorRepository
{
	private ContentResolver contentResolver;
	private Uri doctorsUri = DAContentProvider.DOCTORS_URI;
	private DbApplicationUserRepository dbApplicationUserRepository;


	public DbDoctorRepository( Context context )
	{
		contentResolver = context.getContentResolver();
		this.dbApplicationUserRepository = new DbApplicationUserRepository( context );

	} // constructor


	@Override
	public boolean exists( String userName )
	{
		Cursor cursor = getDoctorCursor( userName );
		boolean exists = cursor != null && cursor.getCount() > 0;
		if( exists )
		{
			cursor.close();
		}
		return exists;

	} // exists


	@Override
	public boolean create( Doctor item )
	{
		Uri createUri = null;
		if( !exists( item.getUserName() ))
			createUri = contentResolver.insert( doctorsUri, putContentValues( item ) );

		boolean createUser = dbApplicationUserRepository.create( item );	// Create ApplicationUser

		return createUser && createUri != null;

	} // create


	@Override
	public Doctor read( String userName )
	{
		Doctor doctor = new Doctor();					// Get an empty object
		Cursor cursor = getDoctorCursor( userName );	// Get a cursor object
		readFromCursor( doctor, cursor );				// Pass the doctor to the cursor
		// Load the info related to ApplicationUser:
		// TODO: MUST do a join:
//		dbApplicationUserRepository.readFromCursor( doctor, getApplicationUserCursor( userName ) );

		return doctor;

	} // read


	@Override
	public ArrayList<Doctor> readAll()
	{
		ArrayList<Doctor> doctors = new ArrayList<>();

		Cursor cursor =  contentResolver.query( doctorsUri,
				null, null, null, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			while( cursor.moveToNext() )
			{
				Doctor doctor = new Doctor();
				readFromCursor( doctor, cursor );								// Set the Doctor values
				Cursor userCursor = getApplicationUserCursor( doctor.getUserName() );
				dbApplicationUserRepository.readFromCursor( doctor, userCursor );	// Set the User values
				doctors.add( doctor );

			} // while

			cursor.close();
		}

		return doctors;

	} // readAll


	@Override
	public Doctor readFromCursor( Doctor doctor, Cursor cursor )	// Make sure cursor is JOINed
	{
		Doctor dr = new Doctor();

		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();

			dr.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
			dr.setId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
			dr.setDegreeAbbreviation( cursor.getColumnName(
					cursor.getColumnIndex( DB.KEY_DR_DEGREE_ABBREVIATION ) ) );
			dbApplicationUserRepository.readFromCursor( doctor, cursor );

			cursor.close();

		} // if

		return dr;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( Doctor doctor )
	{
		ContentValues values = new ContentValues();

		values.put( DB.KEY_USERNAME, doctor.getUserName() );
		values.put( DB.KEY_DR_DEGREE_ABBREVIATION, doctor.getDegreeAbbreviation() );

		return values;

	} // putContentValues


	@Override
	public void update( String id, Doctor doctor )
	{
		ContentValues values = putContentValues( doctor );
		contentResolver.update( doctorsUri, values, DB.KEY_USERNAME + "=?", new String[]{ id } );
		dbApplicationUserRepository.update( id, doctor );		// Also update in the appUser repo

	} // update


	@Override
	public boolean delete( Doctor doctor )
	{
		dbApplicationUserRepository.delete( doctor.getUserName() );	// Delete appUser info
		contentResolver.delete( doctorsUri,
				DB.KEY_USERNAME + "=?", new String[]{ doctor.getUserName() } );
		return true;

	} // delete


	@Override
	public void delete( String username )
	{
		dbApplicationUserRepository.delete( username );				// Delete appUser info
		contentResolver.delete( doctorsUri, DB.KEY_USERNAME + "=?", new String[]{ username } );

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return dbApplicationUserRepository.getApplicationUserCursor( username );

	} // getApplicationUserCursor


	public Cursor getDoctorCursor( String username )
	{
		return contentResolver.query( DAContentProvider.DOCTOR_USERS_URI, null,
				DB.TABLE_DOCTORS + "." + DB.KEY_USERNAME + "=?", new String[]{ username }, null );

	} // getDoctorCursor

} // repository
