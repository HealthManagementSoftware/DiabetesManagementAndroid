package com.healthmanagement.diabetesassistant.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healthmanagement.diabetesassistant.R;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAAVersionAction;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;
import com.healthmanagement.diabetesassistant.models.PatientSignedHIPAANotice;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IHIPAANoticeRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientSignedHIPAARepository;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;
import com.healthmanagement.diabetesassistant.enums.*;

import java.util.Date;
import java.util.UUID;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class SignHIPAANoticeActivity extends AppCompatActivity implements View.OnTouchListener
{
    private IRetrieveNewestHIPAANoticeAction  retrieveNewestHIPAANoticeAction;
    private IRetrieveNewestHIPAAVersionAction retrieveNewestHIPAAVersionAction;
    private IHIPAANoticeRepository            hipaaNoticeRepository;
    private IPatientSignedHIPAARepository     patientSignedHIPAARepository;
    private HIPAAPrivacyNotice                hipaaPrivacyNotice;

    private LinearLayout              agreeForm;
    private ProgressBar               spinner;
    private RetrieveNewestHIPAANotice mRetrieveNoticeTask;
    private SignHIPAANotice           mSignNoticeTask;
    private View                      container;
    private TextView                  noticeText;

    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_hipaanotice );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        retrieveNewestHIPAANoticeAction =
                Dependencies.get( IRetrieveNewestHIPAANoticeAction.class );
        retrieveNewestHIPAAVersionAction =
                Dependencies.get( IRetrieveNewestHIPAAVersionAction.class );
        hipaaNoticeRepository = Dependencies.get( IHIPAANoticeRepository.class );
        patientSignedHIPAARepository =
                Dependencies.get( IPatientSignedHIPAARepository.class );

        agreeForm = findViewById( R.id.agree_form );
        spinner = findViewById( R.id.agree_spinner );
        container = findViewById( R.id.top );
        noticeText = findViewById( R.id.txt_notice );

        mRetrieveNoticeTask = new RetrieveNewestHIPAANotice();
        mRetrieveNoticeTask.execute();

        Button btnIAgree = findViewById( R.id.btn_i_agree );
        btnIAgree.setOnTouchListener( this );

        Button btnCancel = findViewById( R.id.btn_cancel );
        btnCancel.setOnTouchListener( this );

    } // onCreate


    @Override
    public boolean onTouch( View view, MotionEvent event )
    {
        view.performClick();                                // Perform default action
        //Log.i( LOG_TAG, "Touch detected: " + view.getId() );

        if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
        {
            if( !PatientSingleton.getInstance().isLoggedIn() )
                startLoginActivity();

            switch ( view.getId() )
            {
                case R.id.btn_i_agree:                                // Glucose button tap
                    if( DEBUG ) Log.d( LOG_TAG, "I Agree button tapped" );
                    mSignNoticeTask = new SignHIPAANotice();
                    mSignNoticeTask.execute();
                    break;

                case R.id.btn_cancel:
                    finish();
                    break;
            }
        }

        return false;

    } // onTouch


    private void startLoginActivity()
    {
        Intent intent = new Intent( this, LoginActivity.class );
        startActivity( intent );    // Redirect to the Login Activity
        finish();

    } // startLoginActivity


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

        agreeForm.setVisibility( show
                ? View.GONE
                : View.VISIBLE );
        agreeForm.animate().setDuration( shortAnimTime ).alpha(
                show
                        ? 0
                        : 1 ).setListener( new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd( Animator animation )
            {
                agreeForm.setVisibility( show
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
    private class SignHIPAANotice extends AsyncTask<Void, Void, Integer>
    {
        private static final String LOG_TAG = "SignHIPAANotice";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            showProgress( true );

        } // onPreExecute

        @Override
        protected Integer doInBackground( Void... params )
        {
            try
            {
                PatientSingleton pt = PatientSingleton.getInstance();

                PatientSignedHIPAANotice pSHN = new PatientSignedHIPAANotice();
                pSHN.setPatientId( pt.getId() );
                pSHN.setPatientUserName( pt.getUserName() );
                pSHN.setPatient( pt );
                pSHN.setNoticeId( hipaaPrivacyNotice.getRemoteId() );
                pSHN.setHIPAAPrivacyNotice( hipaaPrivacyNotice );
                pSHN.setSignedAt( new Date() );
                pSHN.setUpdatedAt( pSHN.getSignedAt() );


                if( !patientSignedHIPAARepository.exists( pSHN ) )
                {
                    // create
                    patientSignedHIPAARepository.create( pSHN );
                }
                else if ( pSHN.getHIPAAPrivacyNotice() != null &&
                        pt.getPatientSignedHIPAANotice() != null &&
                        pt.getPatientSignedHIPAANotice().getHIPAAPrivacyNotice() != null &&
                        !pSHN.getHIPAAPrivacyNotice().getVersion().equals(
                                pt.getPatientSignedHIPAANotice().getHIPAAPrivacyNotice().getVersion() ) )
                {
                    // update
                    pt.setPatientSignedHIPAANoticeId( pSHN.getRemoteId() );
                    patientSignedHIPAARepository.update( pt.getUserName(),
                            pt.getPatientSignedHIPAANoticeId(), pSHN  );
                }

                // set patient's signed HIPAA notice
                pt.setPatientSignedHIPAANoticeId( pSHN.getRemoteId() );
                pt.setPatientSignedHIPAANotice( pSHN );

                return 0;
            }
            catch ( Exception e )
            {
                Log.e( LOG_TAG, "Error! Message: " + e.getMessage() + "\nStack Trace: \n" );
                for ( StackTraceElement stack : e.getStackTrace() )
                    Log.e( "\t", "\t" + stack.toString() );
            }

            return 1;

        } // doInBackground

        @Override
        protected void onPostExecute( Integer errorCode )
        {
            mSignNoticeTask = null;
            showProgress( false );                          // Hide the spinner

            switch ( errorCode )
            {
                case 0:                                     // 0:	No error
                    Intent returnData = new Intent();       // Create a new intent
                    returnData.setData( Uri.parse( "glucose logged" ) ); // Return message
                    setResult( RESULT_OK, returnData );     // Return ok result for activity result
                    finish();                               // Close the activity
                    break;
                case 1:                                     // 1:	Unknown - something went wrong
                    Snackbar.make( container, "Unknown error", Snackbar.LENGTH_LONG ).show();
                    break;
                default:                                    // Return a general error
                    Snackbar.make( container, "Error", Snackbar.LENGTH_LONG ).show();
                    break;
            }

        } // onPostExecute

        @Override
        protected void onCancelled()
        {
            mSignNoticeTask = null;
            showProgress( false );

        } // onCancelled

    } // SignHipaaNotice


    /**
     * An AsyncTask used to retrieve the latest HIPAA notice from the server
     */
    private class RetrieveNewestHIPAANotice extends AsyncTask<Void, Void, ErrorCode>
    {
        private static final String LOG_TAG = "RetrieveHIPAANotice";

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
                hipaaPrivacyNotice = hipaaNoticeRepository.readNewest();
                String remoteNoticeVersion =
                        retrieveNewestHIPAAVersionAction.getNewestVersion( getApplicationContext() );
                if( remoteNoticeVersion != null )
                {
                    HIPAAPrivacyNotice remoteNotice =
                            retrieveNewestHIPAANoticeAction.getNotice( getApplicationContext() );
                    if( hipaaPrivacyNotice == null )
                    {
                        hipaaNoticeRepository.create( remoteNotice );
                        hipaaPrivacyNotice = remoteNotice;
                    }
                    else if( !hipaaPrivacyNotice.getVersion().equals( remoteNoticeVersion ) )
                    {
                        hipaaNoticeRepository.update( hipaaPrivacyNotice.getId(), remoteNotice );
                        hipaaPrivacyNotice = remoteNotice;
                    }
                    // else don't change the local HIPAA notice
                }
                return ErrorCode.NO_ERROR;
            }
            catch ( Exception e )
            {
                Log.e( LOG_TAG, "Error! Message: " + e.getMessage() + "\nStack Trace: \n" );
                for ( StackTraceElement stack : e.getStackTrace() )
                    Log.e( "\t", "\t" + stack.toString() );
            }

            return ErrorCode.UNKNOWN;

        } // doInBackground

        @Override
        protected void onPostExecute( ErrorCode errorCode )
        {
            mSignNoticeTask = null;
            showProgress( false );                          // Hide the spinner

            switch ( errorCode )
            {
                case NO_ERROR:                                     // 0:	No error
                    mSignNoticeTask = null;
                    showProgress( false );
                    if( hipaaPrivacyNotice != null )
                        noticeText.setText( hipaaPrivacyNotice.getNoticeText() );
                    return;
                case ITEM_ALREADY_EXISTS:                                     // 1:	Unknown - something went wrong
                    Snackbar.make( container, "Unknown error", Snackbar.LENGTH_LONG ).show();
                    break;
                default:                                    // Return a general error
                    Snackbar.make( container, "Error", Snackbar.LENGTH_LONG ).show();
                    break;
            }

        } // onPostExecute

        @Override
        protected void onCancelled()
        {
            mSignNoticeTask = null;
            showProgress( false );

        } // onCancelled

    } // SignHipaaNotice

} // class
