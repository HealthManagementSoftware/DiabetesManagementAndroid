package com.healthmanagement.diabetesassistant.activities;

import android.content.Intent;
import android.os.Bundle;

import com.healthmanagement.diabetesassistant.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.healthmanagement.diabetesassistant.repositories.DbGlucoseEntryRepository;
import com.healthmanagement.diabetesassistant.models.GlucoseEntry;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import java.util.ArrayList;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class ViewGlucoseEntryActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_latest_glucose_entry );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		Button closeButton = findViewById( R.id.close_button );
		closeButton.setOnClickListener( new View.OnClickListener()
										{
											@Override
											public void onClick( View view )
											{
												finish();
											}
										}
		);

		DbGlucoseEntryRepository dbGlucoseEntryRepository
				= new DbGlucoseEntryRepository( getApplicationContext() );

		// GET ALL THE INFO PASSED FROM THE LAST ACTIVITY
		Intent intent = getIntent();
		String glucoseId = intent.getStringExtra( "EntryId" );
		GlucoseEntry glucoseEntry;

		if( DEBUG ) Log.e( LOG_TAG, "EntryId: " + glucoseId );

		if( glucoseId == null || glucoseId.isEmpty() )
		{
			//GlucoseEntry glucoseEntry = patientSingleton.getGlucoseEntries().get(dbGlucoseEntryRepository.readAll().size() - 1);
			ArrayList<GlucoseEntry> glucoseEntries =
					dbGlucoseEntryRepository.readAll();    // Get all from db
			glucoseEntry = glucoseEntries.size() > 0 ? glucoseEntries.get( 0 ) : new GlucoseEntry();    // Get last entry
		}
		else
		{
			glucoseEntry = dbGlucoseEntryRepository.read( glucoseId );
		}

		if( glucoseEntry != null )
		{
			if( DEBUG ) Log.e( LOG_TAG, glucoseEntry.toString() );

			TextView glucoseLevel = findViewById( R.id.glucoseLevelView );
			glucoseLevel.setText( Float.toString( glucoseEntry.getMeasurement() ) );
			TextView whichMeal = findViewById( R.id.whichMealView );
			whichMeal.setText( glucoseEntry.getWhichMeal().toString() );
			TextView beforeAfter = findViewById( R.id.beforeAfterView );
			beforeAfter.setText( glucoseEntry.getBeforeAfter().toString() );
		}

		if( !PatientSingleton.getInstance().hasSignedHIPAANotice() )
			startSignHIPAANoticeActivity();

	} // onCreate


	private void startSignHIPAANoticeActivity()
	{
		Intent intent = new Intent( this, SignHIPAANoticeActivity.class );
		startActivity( intent );

	} // startSignHIPAANoticeActivity

} // class
