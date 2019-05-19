package com.healthmanagement.diabetesassistant.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.healthmanagement.diabetesassistant.R;
import com.healthmanagement.diabetesassistant.adapters.MealEntryCursorAdapter;
import com.healthmanagement.diabetesassistant.contentproviders.DAContentProvider;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

public class ViewMealEntryHistoryActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor>
{
	int loaderIndex = 43434;            // An arbitrary index
	CursorAdapter viewCursorAdapter;
	Cursor        cursor;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_history_3_columns );

		setTitle( "Meal Entry History" );

		TextView header2 = findViewById( R.id.header2 );
		header2.setText( "Total Carbs" );

		TextView header3 = findViewById( R.id.header1 );
		header3.setText( "Which Meal" );

		TextView header4 = findViewById( R.id.header3 );
		header4.setText( R.string.date );

		// Initialize loader to handle calls to ContentProvider
		getSupportLoaderManager().initLoader( loaderIndex, null, this );

		if( !PatientSingleton.hasSignedHIPAANotice() )
			startSignHIPAANoticeActivity();

	} // onCreate


	private void startSignHIPAANoticeActivity()
	{
		Intent intent = new Intent( this, SignHIPAANoticeActivity.class );
		startActivity( intent );

	} // startSignHIPAANoticeActivity


	private void showListview()
	{
		ListView listView = findViewById( R.id.listView );
		listView.setVisibility( View.VISIBLE );

		// Set the adapter
		if( viewCursorAdapter != null )
			listView.setAdapter( viewCursorAdapter );

		View emptyView = findViewById( R.id.empty_view );
		emptyView.setVisibility( View.GONE );
		View headerView = findViewById( R.id.header );
		headerView.setVisibility( View.VISIBLE );

	} // showListview


	private void showEmpty()
	{
		ListView listView = findViewById( R.id.listView );
		listView.setVisibility( View.GONE );
		View emptyView = findViewById( R.id.empty_view );
		emptyView.setVisibility( View.VISIBLE );
		View headerView = findViewById( R.id.header );
		headerView.setVisibility( View.GONE );

	} // showEmpty


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader( int id, @Nullable Bundle args )
	{
		return new CursorLoader( getApplicationContext(),
				DAContentProvider.MEAL_ENTRIES_URI,
				null, DB.KEY_USERNAME + "=?",
				new String[]{ PatientSingleton.getInstance().getUserName() },
				DB.KEY_UPDATED_AT + " DESC" );

	} // onCreateLoader


	@Override
	public void onLoadFinished( @NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data )
	{
		this.cursor = data;

		viewCursorAdapter = new MealEntryCursorAdapter( getApplicationContext(), cursor );

		if( data.getCount() > 0 )
			showListview();
		else
			showEmpty();

		viewCursorAdapter.swapCursor( cursor );

		if( cursor != null && !cursor.isClosed() )
			synchronized( cursor )
			{
				cursor.notify();
			}

	} // onLoadFinished


	@Override
	public void onLoaderReset( @NonNull android.support.v4.content.Loader<Cursor> loader )
	{
	} // onLoaderReset

} // class
