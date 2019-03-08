//--------------------------------------------------------------------------------------//
//																						//
// File Name:	SettingsActivity.java													//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/07/2018																//
// Purpose:		A class to handle saving the user's *app* preferences between uses.		//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.services.PedometerService;
import com.sugarcubes.myglucose.services.SyncService;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;

import java.net.MalformedURLException;
import java.util.List;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

/**
 * A {@link PreferenceActivity} that saves the user's preferences between uses. This is also
 * used for developers during development to provide a hostname and port to connect to when
 * connecting to the server.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{
	public static final String PREF_HOSTNAME          = "hostname";
	public static final String PREF_PORT              = "port";
	public static final String PREF_USE_SSL           = "protocol";
	public static final String PREF_GLUCOSE_UNITS     = "glucose_units";
	public static final String PREF_TRACK_STEPS       = "track_steps";
	public static final String PREF_SHOW_NOTIFICATION = "show_notification";
	private final       String LOG_TAG                = getClass().getSimpleName();


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setupActionBar();

		// Added:
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace( android.R.id.content,
				new PrefsFragment() ).commit();

	} // onCreate


	// Create a PreferenceFragment to display as the top level, since our application
	// 		won't require a lot of different categories of settings:
	public static class PrefsFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );

			// Load the preferences from an XML resource
			addPreferencesFromResource( R.xml.pref_general );

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue( findPreference( PREF_HOSTNAME ) );
			bindPreferenceSummaryToValue( findPreference( PREF_PORT ) );
			bindPreferenceSummaryToValue( findPreference( PREF_GLUCOSE_UNITS ) );

			// Only set the listener to watch for value changes.
			findPreference( PREF_GLUCOSE_UNITS )
					.setOnPreferenceChangeListener( sBindPreferenceSummaryToValueListener );
			findPreference( PREF_SHOW_NOTIFICATION )
					.setOnPreferenceChangeListener( sBindPreferenceSummaryToValueListener );

		} // onCreate


		@Override
		public void onViewCreated( View view, @Nullable Bundle savedInstanceState )
		{
			super.onViewCreated( view, savedInstanceState );

			if( !DEBUG )
			{
				PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(
						getResources().getString(R.string.diabetes_preference_screen ));
				PreferenceCategory devPreferences = (PreferenceCategory)
						findPreference(getResources().getString(R.string.dev_preferences));
				preferenceScreen.removePreference(devPreferences);
			}

		} // onViewCreated

	} // PrefsFragment


	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
			= new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange( Preference preference, Object value )
		{
			String stringValue = value.toString();

			if( preference instanceof ListPreference )
			{
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue( stringValue );

				// Set the summary to reflect the new value.
				preference.setSummary(
						index >= 0
								? listPreference.getEntries()[ index ]
								: null );

			}
			else if( preference instanceof RingtonePreference )
			{
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if( TextUtils.isEmpty( stringValue ) )
				{
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary( R.string.pref_ringtone_silent );

				}
				else
				{
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse( stringValue ) );

					if( ringtone == null )
					{
						// Clear the summary if there was a lookup error.
						preference.setSummary( null );
					}
					else
					{
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle( preference.getContext() );
						preference.setSummary( name );
					}
				}

			}
			else
			{
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary( stringValue );
			}

			return true;

		}
	}; // sBindPreferenceSummaryToValueListener


	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		super.onOptionsItemSelected( item );

		switch( item.getItemId() )
		{
			case android.R.id.home:
				restartWebClient();
				finish();
				break;
		}
		return true;

	} // onOptionsItemSelected


	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		restartWebClient();

	} // onBackPressed


	private void restartWebClient()
	{
		new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					WebClientConnectionSingleton.getInstance( getApplicationContext() ).reset();

					// Restart all of the services (Can't make this static):
					//MainActivity.restartServices( getApplicationContext() );
					//restartServices();	// MainActivity won't bind to services...
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}

			}
		}.run();

		//		if( DEBUG ) Log.e( LOG_TAG, "Track Steps: "
		//				+ MainActivity.getPreferenceBoolean( getApplicationContext(),
		//				SettingsActivity.PREF_TRACK_STEPS )
		//				+ "; Show notification: "
		//				+ MainActivity.getPreferenceBoolean( getApplicationContext(),
		//				SettingsActivity.PREF_SHOW_NOTIFICATION ) );

	} // restartWebClient


	/**
	 * Restarts all of the currently running services
	 */
	private void restartServices()
	{
		// Restart the sync service
		stopService( new Intent( getApplicationContext(), SyncService.class ) );
		startService( new Intent( getApplicationContext(), SyncService.class ) );

		Intent pedometerIntent = new Intent( getApplicationContext(), PedometerService.class );
		pedometerIntent.setAction( PedometerService.ACTION_START );

		// Restart the pedometer service
		if( MainActivity.serviceIsRunning( getApplicationContext(), PedometerService.class ) )
			stopService( pedometerIntent );

		if( MainActivity.trackSteps( getApplicationContext() ) )
		{
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
				startForegroundService( pedometerIntent );
			else
				startService( pedometerIntent );

		} // if

	} // restartServices


	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet( Context context )
	{
		return ( context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK ) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;

	} // isXLargeTablet


	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue( Preference preference )
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener( sBindPreferenceSummaryToValueListener );

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange( preference,
				PreferenceManager
						.getDefaultSharedPreferences( preference.getContext() )
						.getString( preference.getKey(), "" ) );

	} // bindPreferenceSummaryToValue


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		if( actionBar != null )
		{
			// Show the Up button in the action bar.
			actionBar.setDisplayHomeAsUpEnabled( true );
		}

	} // setupActionBar


	@Override
	public boolean onMenuItemSelected( int featureId, MenuItem item )
	{
		int id = item.getItemId();
		if( id == android.R.id.home )
		{
			if( !super.onMenuItemSelected( featureId, item ) )
			{
				NavUtils.navigateUpFromSameTask( this );
			}
			return true;
		}
		return super.onMenuItemSelected( featureId, item );

	} // onMenuItemSelected


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onIsMultiPane()
	{
		return isXLargeTablet( this );

	} // onIsMultiPane


	//	/**
	//	 * {@inheritDoc}
	//	 */
	//	@Override
	//	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	//	public void onBuildHeaders( List<Header> target )
	//	{
	//		loadHeadersFromResource( R.xml.pref_headers, target );
	//
	//	} // onBuildHeaders


	/**
	 * This method stops fragment injection in malicious applications.
	 * Make sure to deny any unknown fragments here.
	 */
	protected boolean isValidFragment( String fragmentName )
	{
		return PreferenceFragment.class.getName().equals( fragmentName );

	} // isValidFragment

} // class
