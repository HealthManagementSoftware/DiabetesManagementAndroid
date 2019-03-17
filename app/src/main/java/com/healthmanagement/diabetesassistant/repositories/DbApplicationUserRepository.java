//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbApplicationUserRepository.java										//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow user data manipulation in a SQLite database. 		//
//																						//
//--------------------------------------------------------------------------------------//

package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.ApplicationUser;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IApplicationUserRepository;
import com.healthmanagement.diabetesassistant.utils.DateUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DbApplicationUserRepository implements IApplicationUserRepository<ApplicationUser>
{
	private final String LOG_TAG = getClass().getSimpleName();
	private ContentResolver contentResolver;
	private Uri uri = DAContentProvider.USERS_URI;


	// We need to inject a Context object so that we can get the content resolver
	public DbApplicationUserRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	@Override
	public boolean exists( String userName )
	{
		Cursor cursor = getApplicationUserCursor( userName );
		boolean exists = cursor != null && cursor.getCount() > 0;
		if( exists )
		{
			cursor.close();
		}
		return exists;

	} // exists


	@Override
	public boolean create( ApplicationUser user )
	{
		Uri create = null;
		if( !exists( user.getUserName() ) )
		{
			ContentValues values = putContentValues( user );
			create = contentResolver.insert( uri, values );
			user.setLoggedIn( true );
		}

		return create != null;

	} // create


	@Override
	public ApplicationUser read( String username )
	{
		Cursor cursor = contentResolver.query( uri, null, DB.KEY_USERNAME + "=?",
				new String[]{ username }, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			ApplicationUser newUser = new ApplicationUser();

			ApplicationUser user = readFromCursor( newUser, cursor );

			cursor.close();

			return user;

		} // if cursor != null

		return null;

	} // read


	@Override
	public ArrayList<ApplicationUser> readAll()
	{
		ArrayList<ApplicationUser> allUsers = new ArrayList<>();
		Cursor cursor = contentResolver.query( uri, null, null,
				null, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			while( cursor.moveToNext() )
			{
				ApplicationUser user = new ApplicationUser();
				allUsers.add( readFromCursor( user, cursor ) );
			}

			cursor.close();

		} // if cursor != null

		return allUsers;

	} // readAll


	@Override
	public ApplicationUser readFromCursor( ApplicationUser user, Cursor cursor )
	{
		if( cursor != null && cursor.getCount() > 0 )
		{
			// NOTE: don't moveToFirst()/close() due to it being passed from another repo

			user.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
			user.setEmail( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) ) );
			user.setAddress1( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS1 ) ) );
			user.setAddress2( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS2 ) ) );
			user.setCity( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_CITY ) ) );
			String pattern = "EEE MMM d HH:mm:ss z yyyy";
			SimpleDateFormat formatter = new SimpleDateFormat( pattern, Locale.US );
			try
			{
				if( cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) ) != null )
				{
					Date date = formatter.parse( cursor.getString(
							cursor.getColumnIndex( DB.KEY_CREATED_AT ) ) );
					Log.d( LOG_TAG, "Date found: " + date.toString() );
					user.setCreatedAt( date );
				}
			}
			catch( Exception e )
			{
				Log.e( LOG_TAG, "DATE TO PARSE: " +
						cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) ) );
				e.printStackTrace();
			}
			user.setEmail( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) ) );
			user.setFirstName( cursor.getString(
					cursor.getColumnIndex( DB.KEY_USER_FIRST_NAME ) ) );
			user.setLastName( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_LAST_NAME ) ) );
			user.setPhoneNumber( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_PHONE ) ) );
			user.setState( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_STATE ) ) );
			user.setZip1( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP1 ) ) );
			user.setZip2( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP2 ) ) );
			user.setTimestamp( cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );
			user.setWeight( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_WEIGHT ) ) );
			user.setHeight( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_HEIGHT ) ) );
			user.setLoggedIn( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_LOGGED_IN ) ) > 0 );
			user.setLoginToken( cursor.getString(
					cursor.getColumnIndex( DB.KEY_USER_LOGIN_TOKEN ) ) );
			user.setCreatedAt( DateUtilities.convertStringToDate(
					cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) ) ) );
			user.setUpdatedAt( DateUtilities.convertStringToDate(
					cursor.getString( cursor.getColumnIndex( DB.KEY_UPDATED_AT ) ) ) );
			// TODO crashes:
			//	user.setLoginExpirationTimestamp( cursor.getLong(
			//			cursor.getColumnIndex( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP ) ) );

		} // if

		return user;

	} // readFromCursor


	@NonNull
	@Override
	public ContentValues putContentValues( ApplicationUser user )
	{
		ContentValues values = new ContentValues();
		if( user.getEmail() != null && !user.getEmail().equals( "null" ) &&
				!user.getEmail().isEmpty() )
			values.put( DB.KEY_USER_EMAIL, user.getEmail() );
		if( user.getFirstName() != null && !user.getFirstName().equals( "null" ) &&
				!user.getFirstName().isEmpty() )
			values.put( DB.KEY_USER_FIRST_NAME, user.getFirstName() );
		if( user.getLastName() != null && !user.getLastName().equals( "null" ) &&
				!user.getLastName().isEmpty() )
			values.put( DB.KEY_USER_LAST_NAME, user.getLastName() );
		if( user.getAddress1() != null && !user.getAddress1().equals( "null" ) &&
				!user.getAddress1().isEmpty() )
			values.put( DB.KEY_USER_ADDRESS1, user.getAddress1() );
		if( user.getAddress2() != null && !user.getAddress2().equals( "null" ) &&
				!user.getAddress2().isEmpty() )
			values.put( DB.KEY_USER_ADDRESS2, user.getAddress2() );
		if( user.getCity() != null && !user.getCity().equals( "null" ) &&
				!user.getCity().isEmpty() )
			values.put( DB.KEY_USER_CITY, user.getCity() );
		if( user.getState() != null && !user.getState().equals( "null" ) &&
				!user.getState().isEmpty() )
			values.put( DB.KEY_USER_STATE, user.getState() );
		if( user.getZip1() > 0 )
			values.put( DB.KEY_USER_ZIP1, user.getZip1() );
		if( user.getZip2() > 0 )
			values.put( DB.KEY_USER_ZIP2, user.getZip2() );
		if( user.getPhoneNumber() != null && !user.getPhoneNumber().equals( "null" ) &&
				!user.getPhoneNumber().isEmpty() )
			values.put( DB.KEY_USER_PHONE, user.getPhoneNumber() );
		if( user.getUserName() != null && !user.getUserName().equals( "null" ) &&
				!user.getUserName().isEmpty() )
			values.put( DB.KEY_USERNAME, user.getUserName() );
		if( user.getWeight() == null )
			user.setWeight( "0" );
		values.put( DB.KEY_USER_WEIGHT, user.getWeight() );
		if( user.getHeight() == null )
			user.setHeight( "0" );
		values.put( DB.KEY_USER_HEIGHT, user.getHeight() );
		//		if( user.getTimestamp() > 0 )
		//			values.put( DB.KEY_TIMESTAMP, user.getTimestamp() );
		values.put( DB.KEY_USER_LOGGED_IN, user.isLoggedIn()
				? 1
				: 0 );
		if( user.getLoginToken() != null && !user.getLoginToken().equals( "null" ) &&
				!user.getLoginToken().isEmpty() )
			values.put( DB.KEY_USER_LOGIN_TOKEN, user.getLoginToken() );
		if( user.getLoginExpirationTimestamp() > 0 )
			values.put( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP, user.getLoginExpirationTimestamp() );
		if( user.getCreatedAt() != null )
			values.put( DB.KEY_CREATED_AT, user.getCreatedAt().toString() );
		if( user.getUpdatedAt() != null )
			values.put( DB.KEY_UPDATED_AT, user.getUpdatedAt().toString() );
		return values;

	} // putContentValues


	@Override
	public void update( String id, ApplicationUser user )
	{
		user.setUpdatedAt( new Date() );                    // Set updatedAt to now
		ContentValues values = putContentValues( user );
		contentResolver.update( uri, values, DB.KEY_USERNAME + "=?", new String[]{ id } );

	} // update


	@Override
	public boolean delete( ApplicationUser user )
	{
		int delete = contentResolver.delete( uri,
				DB.KEY_USERNAME + "=?", new String[]{ user.getEmail() } );

		return delete > 0;

	} // delete


	@Override
	public void delete( String email )
	{
		contentResolver.delete( uri, DB.KEY_USERNAME + "=?", new String[]{ email } );

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return contentResolver.query( DAContentProvider.USERS_URI, null,
				DB.KEY_USERNAME + "=?", new String[]{ username }, null );

	} // getApplicationUserCursor

} // repository
