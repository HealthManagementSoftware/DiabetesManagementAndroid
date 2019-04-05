package com.healthmanagement.diabetesassistant.activities;

import android.content.Intent;
import android.os.Bundle;

import com.healthmanagement.diabetesassistant.R;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;


public class ViewFoodListingsActivity extends AppCompatActivity implements View.OnTouchListener
{
    private ArrayList<String> nbdTags;
    private String selected = "N/A";
    private String selectedName = "N/A";
    public static final String RETURN_KEY = "com.healthmanagment.diabetesassistant.FoodListingSelection";
    public static final String RETURN_KEY2 = "com.healthmanagment.diabetesassistant.FoodListingName";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_select_food_item);

        Button acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnTouchListener( this );
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnTouchListener( this);
        // Get the list of food items
        Intent intent = getIntent();
        ArrayList<String> foodList = intent.getStringArrayListExtra(LogMealActivity.FOOD_LIST);

        nbdTags = intent.getStringArrayListExtra(LogMealActivity.TAG_LIST);

        ArrayAdapter<String> foodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foodList);
        ListView lvFoodList = findViewById(R.id.foodList);
        lvFoodList.setAdapter(foodAdapter);
        lvFoodList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);   // allow one selection
        lvFoodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set the selected nbd tag and item name
                selected = nbdTags.get( position );
                selectedName = parent.getItemAtPosition( position ).toString();
            } // change selected item to newly selected item
        });

    } // onCreate

    @Override
    public boolean onTouch( View view, MotionEvent event )
    {
        view.performClick();
        if( event.getAction() == MotionEvent.ACTION_UP )        // Only handle single event
        {
            switch( view.getId() )
            {
                case R.id.acceptButton:
                    finish();
                    break;

                case R.id.backButton:
                    selected = "N/A";
                    selectedName = "N/A";
                    finish();
                    break;
            } // switch
        } // if
        return false;
    } // onTouch

    @Override
    public void finish()
    {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra( RETURN_KEY , selected );
        data.putExtra( RETURN_KEY2, selectedName );
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    } // finish
} // class
