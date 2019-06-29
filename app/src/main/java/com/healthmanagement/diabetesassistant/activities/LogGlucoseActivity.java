package com.healthmanagement.diabetesassistant.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.healthmanagement.diabetesassistant.R;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILogGlucoseEntryAction;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.GlucoseEntry;
import com.healthmanagement.diabetesassistant.enums.BeforeAfter;
import com.healthmanagement.diabetesassistant.enums.ErrorCode;
import com.healthmanagement.diabetesassistant.enums.WhichMeal;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import java.util.Date;

public class LogGlucoseActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();
	private View                   container;               // The base view (for using Snackbar)
	private View                   spinner;                 // Shows when submitting
	private View                   glucoseForm;             // The view to hide when submitting
	private ILogGlucoseEntryAction logGlucoseEntryAction;   // The command to log the glucose
	private LogGlucoseTask mLogGlucoseTask = null;


	@SuppressLint( "ClickableViewAccessibility" )
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
			setContentView( R.layout.activity_log_glucose );
		else
			setContentView( R.layout.activity_log_glucose_compat );

		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		// Return the correct LogGlucoseEntry action (set up in .dependencies.ObjectMap)
		logGlucoseEntryAction = Dependencies.get( ILogGlucoseEntryAction.class );

		Button saveButton = findViewById( R.id.submitGlucose );
		Button historyButton = findViewById( R.id.viewHistory );
		Button viewLatest = findViewById( R.id.viewLatest );
		spinner = findViewById( R.id.save_spinner );
		glucoseForm = findViewById( R.id.glucose_form );
		container = findViewById( R.id.top );

		// Set up the listener for saving the glucose level:
		saveButton.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					mLogGlucoseTask = new LogGlucoseTask();
					mLogGlucoseTask.execute();

					return true;
				}
				return false;
			}
		} );

		// Set up the glucose history listener:
		historyButton.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					Intent intent =
							new Intent( getApplicationContext(), ViewGlucoseHistoryActivity.class );
					startActivity( intent );
					return true;
				}
				return false;
			}
		} );

		// Set up the "view latest" button listener:
		viewLatest.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					startViewLatestGlucoseActivity();
					return true;
				}
				return false;
			}
		} );

		if( !PatientSingleton.getInstance().hasSignedHIPAANotice() )
			startSignHIPAANoticeActivity();

	} // onCreate


	private void startSignHIPAANoticeActivity()
	{
		Intent intent = new Intent( this, SignHIPAANoticeActivity.class );
		startActivity( intent );

	} // startSignHIPAANoticeActivity


	/**
	 * Starts the Activity to view the latest Glucose entry
	 */
	private void startViewLatestGlucoseActivity()
	{
		Intent intent = new Intent( this, ViewGlucoseEntryActivity.class );
		startActivity( intent );

	} // startEditProfileActivity


	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
	private void showProgress( final boolean show )
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime );

		glucoseForm.setVisibility( show
				? View.GONE
				: View.VISIBLE );
		glucoseForm.animate().setDuration( shortAnimTime ).alpha(
				show
						? 0
						: 1 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				glucoseForm.setVisibility( show
						? View.GONE
						: View.VISIBLE );
			}
		} );

		spinner.setVisibility( show
				? View.VISIBLE
				: View.GONE );
		spinner.animate().setDuration( shortAnimTime ).alpha(
				show
						? 1
						: 0 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				spinner.setVisibility( show
						? View.VISIBLE
						: View.GONE );
			}
		} );

	} // showProgress


	/**
	 * An AsyncTask used to log the glucose on a separate thread
	 */
	private class LogGlucoseTask extends AsyncTask<Void, Void, ErrorCode>
	{
		private static final String LOG_TAG = "LogGlucoseTask";

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			showProgress( true );

		} // onPreExecute

		@Override
		protected ErrorCode doInBackground( Void... params )
		{
			try
			{
				GlucoseEntry glucoseEntry = new GlucoseEntry();

				EditText glucoseLevel = findViewById( R.id.glucoseLevel );
				Spinner whichMeal = findViewById( R.id.whichMeal );
				Spinner beforeAfter = findViewById( R.id.beforeAfter );

				glucoseEntry.setMeasurement(
						Float.parseFloat( glucoseLevel.getText().toString() )
				);
				WhichMeal whichMealEnum =
						WhichMeal.valueOf( whichMeal.getSelectedItem().toString().toUpperCase() );
				glucoseEntry.setWhichMeal( whichMealEnum );
				BeforeAfter beforeAfterEnum =
						BeforeAfter.valueOf(
								beforeAfter.getSelectedItem().toString().toUpperCase()
						);
				glucoseEntry.setBeforeAfter( beforeAfterEnum );

				Date date = new Date();
				glucoseEntry.setTimeStamp( date.getTime() );
				glucoseEntry.setCreatedAt( date );
				PatientSingleton patient = PatientSingleton.getInstance();
				glucoseEntry.setUserName( patient.getUserName() );
				// Save the GlucoseEntry and its GlucoseItems
				return logGlucoseEntryAction.logGlucoseEntry(
						getApplicationContext(), glucoseEntry
				);

			}
			catch( Exception e )
			{
				Log.e( LOG_TAG, "Error submitting glucose level! Message: " + e.getMessage() );
				return ErrorCode.UNKNOWN;

			} // try/catch

		} // doInBackground

		@Override
		protected void onPostExecute( final ErrorCode errorCode )
		{
			mLogGlucoseTask = null;
			showProgress( false );                          // Hide the spinner

			switch( errorCode )
			{
				case NO_ERROR:                              // 0:	No error
					Intent returnData = new Intent();       // Create a new intent
					returnData.setData( Uri.parse( "glucose logged" ) ); // Return message
					setResult( RESULT_OK, returnData );     // Return ok result for activity result
					finish();                               // Close the activity
					break;

				case UNKNOWN:                               // 1:	Unknown - something went wrong
					Snackbar.make( container, "Unknown error", Snackbar.LENGTH_LONG ).show();
					break;

				default:                                    // Return a general error
					Snackbar.make( container, "Error", Snackbar.LENGTH_LONG ).show();
					break;

			} // switch

		} // onPostExecute

		@Override
		protected void onCancelled()
		{
			mLogGlucoseTask = null;
			showProgress( false );

		} // onCancelled

	} // LogGlucoseTask

} // class


