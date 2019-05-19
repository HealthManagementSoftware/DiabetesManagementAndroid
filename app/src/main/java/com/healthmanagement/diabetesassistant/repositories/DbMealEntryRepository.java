//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbGlucoseEntryRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow MealEntry and MealItem data manipulation in a 	//
// 				SQLite database. 														//
//																						//
//--------------------------------------------------------------------------------------//

package com.healthmanagement.diabetesassistant.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.MealEntry;
import com.healthmanagement.diabetesassistant.models.MealItem;
import com.healthmanagement.diabetesassistant.enums.WhichMeal;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IMealEntryRepository;
import com.healthmanagement.diabetesassistant.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DbMealEntryRepository implements IMealEntryRepository
{
	private ContentResolver contentResolver;
	private Uri uriEntries   = DAContentProvider.MEAL_ENTRIES_URI;
	private Uri uriMealItems = DAContentProvider.MEAL_ITEMS_URI;

	public DbMealEntryRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	/**
	 * Creates a MealEntry in the database, as well as all of the meal items associated
	 * with it.
	 *
	 * @param mealEntry - the MealEntry object
	 */
	@Override
	public void create( MealEntry mealEntry )
	{
		if( mealEntry.getRemoteId().isEmpty() )                        // Create an ID
			mealEntry.setRemoteId( UUID.randomUUID().toString() );

		int totalCarbs = 0;
		for( MealItem mealItem : mealEntry.getMealItems() )            // Calculate total carbohydrates
		{
			// Add each mealEntry's carbs if not empty
			totalCarbs += mealItem.getCarbs() < 1
					? 0
					: mealItem.getCarbs();

		} // for

		mealEntry.setTotalCarbs( totalCarbs );                      // Set the calculated carbs

		// Insert into the database:
		contentResolver.insert( uriEntries, putContentValues( mealEntry ) );

		if( mealEntry.getMealItems() != null && mealEntry.getMealItems().size() > 0 )
		{
			// Create MealItems in the DB if they exist
			for( MealItem mealItem : mealEntry.getMealItems() )
			{
				mealItem.setMealId( mealEntry.getRemoteId() );        // Set to MealEntry Id
				if( mealItem.getRemoteId().isEmpty() )              // Create an ID
					mealItem.setRemoteId( UUID.randomUUID().toString() );
				createMealItem( mealItem );                         // Create in the database

			} // for

		} // if

	} // create


	@Override
	public MealEntry read( String id )
	{
		Cursor cursor = contentResolver.query(
				uriEntries,
				null,
				DB.KEY_REMOTE_ID + "=?",
				new String[]{ id },
				DB.KEY_TIMESTAMP + " ASC" );

		if( cursor != null )                            // First, if we have a MealEntry...
		{
			cursor.moveToFirst();
			MealEntry entry = readFromCursor( cursor );    // ...Load it from the db
			cursor.close();


			Cursor mealItemsCursor = contentResolver.query( uriMealItems,
					null, DB.KEY_MEAL_ID + "=?",
					new String[]{ String.valueOf( id ) },
					DB.KEY_TIMESTAMP + " ASC" );

			if( mealItemsCursor != null )                // Then we check for MealItems
			{
				mealItemsCursor.moveToFirst();

				ArrayList<MealItem> mealItems = new ArrayList<>();

				while( cursor.moveToNext() )
				{
					MealItem mealItem = new MealItem();
					// TODO: Populate the MealItems

				} // while
				//				entry.setMealItems( readMealItemsFromCursor( mealItemsCursor ) );
				mealItemsCursor.close();

			} // if

			return entry;                                // Return the entry we retrieved

		} // if

		return null;

	} // read


	@Override
	public ArrayList<MealEntry> readAll()
	{
		return readAll( null );

	} // readAll


	@Override
	public ArrayList<MealEntry> readAll( String userName )
	{
		ArrayList<MealEntry> mealEntries = new ArrayList<>();

		String selection = userName != null
				? DB.KEY_USERNAME + "=?"
				: null;
		String[] selectionArgs = userName != null
				? new String[]{ userName }
				: null;

		Cursor cursor = contentResolver.query( uriEntries,
				null, selection, selectionArgs,
				DB.KEY_TIMESTAMP + " DESC" );

		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();
			do
			{
				MealEntry mealEntry = readFromCursor( cursor );    // <-- MealItems set here
				mealEntries.add( mealEntry );                    // Add the entry to the ArrayList

			} while( cursor.moveToNext() ); // do...while
			cursor.close();

		} // if

		return mealEntries;

	} // readAll


	@Override
	public MealEntry readFromCursor( Cursor cursor )
	{
		MealEntry entry = new MealEntry();
		entry.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
		entry.setRemoteId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
		entry.setTotalCarbs( cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ENTRY_TOTAL_CARBS ) ) );
		entry.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
		entry.setMealItems( readAllMealItems( entry.getRemoteId() ) );    // Access MealItems by meal id
		entry.setSynced( cursor.getInt( cursor.getColumnIndex( DB.KEY_SYNCED ) ) > 0 );

		String updatedAt = cursor.getString( cursor.getColumnIndex( DB.KEY_UPDATED_AT ) );
		if( !updatedAt.isEmpty() )
			// Convert the updatedAt string to a Date object:
			entry.setUpdatedAt( DateUtilities.convertStringToDate( updatedAt ) );

		String createdAt = cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) );
		if( !createdAt.isEmpty() )
			// Convert the createdAt string to a Date object:
			entry.setCreatedAt( DateUtilities.convertStringToDate( createdAt ) );

		// Retrieve as a long:
		entry.setTimestamp( cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );
		entry.setWhichMeal( WhichMeal.fromInt(
				cursor.getInt( cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) ) ) );

		return entry;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( MealEntry item )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_MEAL_ENTRY_TOTAL_CARBS, item.getTotalCarbs() );
		values.put( DB.KEY_CREATED_AT, item.getCreatedAt().toString() );
		values.put( DB.KEY_UPDATED_AT, item.getUpdatedAt().toString() );
		values.put( DB.KEY_USERNAME, item.getUserName() );
		values.put( DB.KEY_TIMESTAMP, item.getTimestamp() );
		int whichMeal = 0;
		try
		{
			whichMeal = item.getWhichMeal().getValue();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		values.put( DB.KEY_WHICH_MEAL, whichMeal );
		return values;

	} // putContentValues


	@Override
	public void update( int id, MealEntry item )
	{
		item.setUpdatedAt( new Date() );
		contentResolver.update( uriEntries, putContentValues( item ),       // Update the MealEntry
				DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) } );

		if( item.getMealItems() != null && item.getMealItems().size() > 0 ) // If !null
			for( MealItem mealItem : item.getMealItems() )
				updateMealItem( mealItem.getRemoteId(), mealItem );         // Also update mealItems

	} // update


	@Override
	public void delete( MealEntry item )
	{
		delete( item.getId() );

	} // delete


	@Override
	public void delete( int id )
	{
		// Delete the MealItems first:
		contentResolver.delete( uriMealItems, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( id ) } );

		// Finally, delete the MealEntry row itself:
		contentResolver.delete( uriEntries, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // delete


	//----------------------------- MealItem implementation: -----------------------------//


	@Override
	public void createMealItem( MealItem mealItem )
	{
		if( mealItem.getRemoteId().isEmpty() )
			mealItem.setRemoteId( UUID.randomUUID().toString() );

		contentResolver.insert( uriMealItems, putMealItemContentValues( mealItem ) );

	} // createMealItem


	@Override
	public ArrayList<MealItem> readAllMealItems( String mealEntryId )
	{
		ArrayList<MealItem> mealItems = new ArrayList<>();
		Cursor cursor = contentResolver.query( uriMealItems,
				null, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( mealEntryId ) },
				null );

		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();
			do
			{
				MealItem mealItem = readMealItemFromCursor( cursor );
				mealItems.add( mealItem );                // Add the entry to the ArrayList

			} while( cursor.moveToNext() ); // do while
			cursor.close();

		} // if

		return mealItems;

	} // readAllMealItems


	@Override
	public MealItem readMealItemFromCursor( Cursor cursor )
	{
		MealItem mealitem = new MealItem();

		mealitem.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
		mealitem.setRemoteId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
		mealitem.setMealId( cursor.getString( cursor.getColumnIndex( DB.KEY_MEAL_ID ) ) );
		mealitem.setName( cursor.getString( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_NAME ) ) );
		mealitem.setCarbs( cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_CARBS ) ) );
		mealitem.setServings( cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_SERVINGS ) ) );

		return mealitem;

	} // readMealItemFromCursor


	@Override
	public ContentValues putMealItemContentValues( MealItem item )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_MEAL_ID, item.getMealId() );
		values.put( DB.KEY_MEAL_ITEM_NAME, item.getName() );
		values.put( DB.KEY_MEAL_ITEM_CARBS, item.getCarbs() );
		values.put( DB.KEY_MEAL_ITEM_SERVINGS, item.getServings() );
		return values;

	} // putMealItemContentValues


	@Override
	public void updateMealItem( String mealItemId, MealItem mealItem )
	{
		contentResolver.update( uriMealItems, putMealItemContentValues( mealItem ),
				DB.KEY_ID + "=?", new String[]{ String.valueOf( mealItemId ) } );

	} // updateMealItem


	@Override
	public void deleteMealEntryMealItems( String mealEntryId )
	{
		contentResolver.delete( uriMealItems, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( mealEntryId ) } );

	} // deleteMealEntryMealItems


	@Override
	public void deleteMealItem( String mealItemId )
	{
		contentResolver.delete( uriMealItems, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( mealItemId ) } );

	} // deleteMealItem


	@Override
	public void setAllSynced()
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_SYNCED, true );
		contentResolver.update( DAContentProvider.MEAL_ENTRIES_URI,
				values, null, null );

	} // setAllSynced


} // repository