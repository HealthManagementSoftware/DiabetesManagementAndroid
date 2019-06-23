//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbPatientRepository.java												//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow MealEntry and Patient data manipulation in a 		//
// 				SQLite database. 														//
// 				NOTE: Only the PatientSingleton should access most of the methods, for	//
//				security purposes.														//
//																						//
//--------------------------------------------------------------------------------------//

package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.ApplicationUser;
import com.healthmanagement.diabetesassistant.models.PatientSignedHIPAANotice;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IApplicationUserRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IDoctorRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IExerciseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IGlucoseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IMealEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientSignedHIPAARepository;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DbPatientRepository implements IPatientRepository
{
	private final String LOG_TAG = this.getClass().getName();

	private ContentResolver                             contentResolver;
	private Context                                     context;
	private IDoctorRepository                           doctorRepository;
	private IApplicationUserRepository<ApplicationUser> applicationUserRepository;
	private IGlucoseEntryRepository                     glucoseEntryRepository;
	private IMealEntryRepository                        mealEntryRepository;
	private IExerciseEntryRepository                    exerciseEntryRepository;
	private IPatientSignedHIPAARepository			    patientSignedHIPAARepository;


	public DbPatientRepository( Context context )
	{
		this.context = context;
		contentResolver = context.getContentResolver();
		// Note: these must be instantiated without using Dependencies because this class
		// is instantiated in the Dependencies class.
		doctorRepository = new DbDoctorRepository( context );
		applicationUserRepository = new DbApplicationUserRepository( context );
		glucoseEntryRepository = new DbGlucoseEntryRepository( context );
		mealEntryRepository = new DbMealEntryRepository( context );
		exerciseEntryRepository = new DbExerciseEntryRepository( context );
		patientSignedHIPAARepository = new DbPatientSignedHIPAARepository( context );

	} // constructor


	@Override
	public boolean exists( String userName )
	{
		Cursor cursor = getPatientCursor( userName );
		boolean exists = cursor != null && cursor.getCount() > 0;
		if( exists )
		{
			cursor.close();
		}
		return exists;

	} // exists


	private Cursor getPatientCursor( String userName )
	{
		return contentResolver.query( DAContentProvider.PATIENTS_URI, null,
				DB.KEY_USERNAME + "=?", new String[]{ userName }, null );

	} // getPatientCursor


	@Override
	public boolean delete( PatientSingleton patientSingleton )
	{
		ContentResolver contentResolver = context.getContentResolver();
		int a = contentResolver.delete( DAContentProvider.USERS_URI,
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int b = contentResolver.delete( DAContentProvider.PATIENTS_URI,
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int c = contentResolver.delete( DAContentProvider.DOCTORS_URI, // (For good measure)
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );

		return a > 0 && ( b > 0 || c > 0 );

	} // populate

	@Override
	public void delete( String id )
	{
		applicationUserRepository.delete( id );            // delete from application users
		contentResolver.delete( DAContentProvider.PATIENTS_URI,
				DB.KEY_USERNAME +
						"=?", new String[]{ id } );                    // delete patient entry

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return applicationUserRepository.getApplicationUserCursor( username );

	}


	@Override
	public PatientSingleton getLoggedInUser()
	{
		int loggedIn = 1;        // SQLite stores boolean values as 0=false, 1=true
		Cursor cursor = contentResolver.query( DAContentProvider.USERS_URI, null,
				DB.KEY_USER_LOGGED_IN + "=?",
				new String[]{ String.valueOf( loggedIn ) }, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			PatientSingleton user = readFromCursor( PatientSingleton.getInstance(), cursor );

			cursor.close();

			return user;

		} // if cursor != null

		return null;

	} // getLoggedInUser


	@Override
	public boolean create( PatientSingleton patientSingleton )
	{
		// There be two tables to be updated: "users" and "patients":
		Uri patientUri = null;
		if( !exists( patientSingleton.getUserName() ) )
		{
			//long timestamp = new Date().getTime();
			ContentValues patientValues = new ContentValues();
			if( patientSingleton.getDoctorUserName() != null
					&& !patientSingleton.getDoctorUserName().isEmpty() )
				patientValues.put( DB.KEY_DR_USERNAME, patientSingleton.getDoctorUserName() );
			patientValues.put( DB.KEY_USERNAME, patientSingleton.getUserName() );
			patientUri = contentResolver.insert(
					DAContentProvider.PATIENTS_URI, patientValues );

		} // if

        if( patientSingleton.getPatientSignedHIPAANotice() != null )
            patientSignedHIPAARepository.create( patientSingleton.getPatientSignedHIPAANotice() );

		// Now insert the "users" information:
		boolean userInserted = applicationUserRepository.create( patientSingleton );

		return patientUri != null && userInserted;

	} // create


	@Override
	public PatientSingleton read( String username )
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();
		Cursor patientCursor = contentResolver.query( DAContentProvider.PATIENT_USERS_URI,
				null, DB.KEY_USERNAME + "=?",
				new String[]{ username }, null );
		readFromCursor( patientSingleton, patientCursor );

		return patientSingleton;

	} // read


	@Override
	public ArrayList<PatientSingleton> readAll()
	{
		//		arrayList.add( read( "ArbitraryDefaultUserUsername" ) );

		return new ArrayList<>();

	} // readAll


	@Override
	public PatientSingleton readFromCursor( PatientSingleton patientSingleton, Cursor cursor )
	{
		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();

			// Set values:
			patientSingleton.setDoctorUserName(
					cursor.getString( cursor.getColumnIndex( DB.KEY_DR_USERNAME ) ) );
			patientSingleton.setDoctorId(
					cursor.getString( cursor.getColumnIndex( DB.KEY_DR_ID ) ) );
			patientSingleton.setLoggedIn(
					cursor.getInt( cursor.getColumnIndexOrThrow( DB.KEY_USER_LOGGED_IN ) ) > 0 );
			// Get doctor from repository:
			if( patientSingleton.getDoctorUserName() != null
					&& !patientSingleton.getDoctorUserName().isEmpty() )
				patientSingleton.setDoctor(
						doctorRepository.read( patientSingleton.getDoctorUserName() ) );
			applicationUserRepository.readFromCursor( patientSingleton, cursor );

            cursor.close();

			// Attach the patient's entries:
			// NOTE: This should be done in readFromCursor() to allow for passing
			//		a cursor in directly.
			patientSingleton.setGlucoseEntries( glucoseEntryRepository.readAll(
					patientSingleton.getUserName() ) );
			patientSingleton.setExerciseEntries( exerciseEntryRepository.readAll(
					patientSingleton.getUserName() ) );
			patientSingleton.setMealEntries( mealEntryRepository.readAll(
					patientSingleton.getUserName() ) );

            PatientSignedHIPAANotice signedNotice = patientSignedHIPAARepository.read(
                    patientSingleton.getUserName() );
            if( signedNotice != null )
            {
                patientSingleton.setPatientSignedHIPAANoticeId( signedNotice.getRemoteId() );
                patientSingleton.setPatientSignedHIPAANotice( signedNotice );

            } // if

			return patientSingleton;

		} // if

		return null;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( PatientSingleton patient )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_DR_USERNAME, patient.getDoctorUserName() );
		values.put( DB.KEY_PATIENT_SIGNED_HIPAA_NOTICE_ID,
                patient.getPatientSignedHIPAANoticeId() );
		return values;

	} // putContentValues


	@Override
	public void update( String userName, PatientSingleton patient )
	{
		ContentValues values = putContentValues( patient );
		if( values.size() > 0 )
			contentResolver.update(
					DAContentProvider.PATIENTS_URI,
					putContentValues( patient ),
					DB.KEY_USERNAME + "=?",
					new String[]{ userName } );
		applicationUserRepository.update( userName, patient );
		//		contentResolver.update( DAContentProvider.USERS_URI,
		//				applicationUserRepository.putContentValues( item ),
		//				DB.KEY_USERNAME + "=?", new String[]{ username } );

	} // update


	@Override
	public Cursor getCursorForLoggedInUser()
	{
		try
		{
			return new GetLoggedInUserTask().execute( contentResolver ).get();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		catch( ExecutionException e )
		{
			e.printStackTrace();
		}

		return null;

	} // getCursorForLoggedInUser


	@Override
	public void setAllSynced()
	{
		glucoseEntryRepository.setAllSynced();
		exerciseEntryRepository.setAllSynced();
		mealEntryRepository.setAllSynced();
	}


	/**
	 * Used in getCursorForLoggedInUser to return the logged in user
	 */
	public static class GetLoggedInUserTask extends AsyncTask<ContentResolver, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground( ContentResolver... contentResolvers )
		{
			return contentResolvers[ 0 ].query( DAContentProvider.PATIENT_USERS_URI,
					null, DB.TABLE_USERS + "." + DB.KEY_USER_LOGGED_IN + "=?",
					new String[]{ String.valueOf( 1 ) }, null );

		} // doInBackground

	} // AsyncTask

} // repository
