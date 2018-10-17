package com.sugarcubes.myglucose.activities;

import android.os.Bundle;

import com.sugarcubes.myglucose.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;

import java.util.ArrayList;

public class ViewLatestGlucoseEntryActivity extends AppCompatActivity {

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_latest_glucose_entry );
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

        PatientSingleton patientSingleton = PatientSingleton.getInstance();
        DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository(getApplicationContext());
        //GlucoseEntry glucoseEntry = patientSingleton.getGlucoseEntries().get(dbGlucoseEntryRepository.readAll().size() - 1);
        ArrayList<GlucoseEntry> glucoseEntries = dbGlucoseEntryRepository.readAll();    // Get all from db
        GlucoseEntry glucoseEntry;
        if( glucoseEntries.size() > 0 )
        {
            glucoseEntry = glucoseEntries.get( 0 );    // Get last entry

            TextView glucoseLevel = findViewById(R.id.glucoseLevelView);
            glucoseLevel.setText(Float.toString(glucoseEntry.getMeasurement()));
            TextView whichMeal = findViewById(R.id.whichMealView);
            whichMeal.setText(glucoseEntry.getWhichMeal().toString());
            TextView beforeAfter = findViewById(R.id.beforeAfterView);
            beforeAfter.setText(glucoseEntry.getBeforeAfter().toString());
        }
        else
            glucoseEntry = new GlucoseEntry();

    }
}