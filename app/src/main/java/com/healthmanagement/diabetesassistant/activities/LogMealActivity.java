package com.healthmanagement.diabetesassistant.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.healthmanagement.diabetesassistant.R;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILogMealEntryAction;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.MealEntry;
import com.healthmanagement.diabetesassistant.models.MealItem;
import com.healthmanagement.diabetesassistant.enums.ErrorCode;
import com.healthmanagement.diabetesassistant.enums.WhichMeal;
import com.healthmanagement.diabetesassistant.singletons.PatientSingleton;

import java.util.ArrayList;
import java.util.UUID;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class LogMealActivity extends AppCompatActivity implements View.OnTouchListener
{
	private final String LOG_TAG = getClass().getSimpleName();
	public static final String FOOD_LIST = "com.healthmanagment.diabetesassistant.FOOD_LIST";
	public static final String TAG_LIST = "com.healthmanagment.diabetesassistant.TAG_LIST";
	private static final int REQUEST_CODE = 2521;
	private CoordinatorLayout   coordinatorLayout;          // The base view (for using Snackbar)
	private View                spinner;                    // Shows when submitting
	private View                mealForm;                   // The view to hide when submitting
	private Spinner             whichMeal;                  // To select a meal type
	private ILogMealEntryAction logMealEntryAction;         // The command to log the meal
	private TableLayout         mealItemTable;              // Holds the MealItems on the screen
	private ArrayList<MealItem> mealItems;                  // Iterable ArrayList of MealItems
	private ArrayList<EditText> allServingNameEntries;      // Holds all serving name EditTexts
	private ArrayList<EditText> allCarbEntries;             // Holds all carb entries EditTexts
	private ArrayList<EditText> allServingNumberEntries;    // Holds all serving entries EditTexts
	private ArrayList<TableRow> allTableRows;               // Holds all TableRows
	private String foodName;								// Temporary food name holder until I can think of a better solution
	private String carbs;									// Temporary carbs holder until I can think of a better solution
	//private String selectedTag = "N/A";						// Temporary selection holder until I can think of a better solution
	private int currentTag;									// Temporary tag holder until I can think of a better solution

	// Keep track of the login task to ensure we can cancel it if requested:
	private LogMealTask mAuthTask = null;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_log_meal );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		coordinatorLayout = findViewById( R.id.top );

		// Dependency Injection:
		logMealEntryAction = Dependencies.get( ILogMealEntryAction.class );

		Button viewLatestEntryButton = findViewById( R.id.button_view_latest );
		viewLatestEntryButton.setOnTouchListener( this );   // Add listener to latest entry btn

		Button viewHistoryButton = findViewById( R.id.button_view_history );
		viewHistoryButton.setOnTouchListener( this );   // Add listener to latest entry btn

		Button submitButton = findViewById( R.id.button_save );
		submitButton.setOnTouchListener( this );            // Add listener to save button

		Button addButton = findViewById( R.id.add_meal_item_button );
		addButton.setOnTouchListener( this );

		Button searchButton = findViewById( R.id.search_meal_item_button );
		searchButton.setOnTouchListener( this );            // Add listener to search button

		spinner = findViewById( R.id.save_spinner );
		mealForm = findViewById( R.id.meal_form );
		mealItemTable = findViewById( R.id.meal_item_table );
		whichMeal = findViewById( R.id.whichMeal );

		mealItems = new ArrayList<>();                      // Instantiate the ArrayLists
		allServingNameEntries = new ArrayList<>();
		allCarbEntries = new ArrayList<>();
		allTableRows = new ArrayList<>();
		allServingNumberEntries = new ArrayList<>();

		// Add each EditText of the first entry to the ArrayLists:
		TableRow tableRow = findViewById( R.id.table_row );
		tableRow.setTag( 0 );							 //set the tag of the first tablerow
		allTableRows.add( tableRow );                    // Add the first MealItem to the ArrayList
		// Add the first row of EditTexts:
		allServingNumberEntries.add( (EditText) tableRow.findViewById( R.id.number_servings ) );
		allServingNameEntries.add( (EditText) tableRow.findViewById( R.id.edit_serving_name ) );
		allCarbEntries.add( (EditText) tableRow.findViewById( R.id.edit_carbs ) );

		if( !PatientSingleton.getInstance().hasSignedHIPAANotice() )
			startSignHIPAANoticeActivity();

	} // onCreate


	private void startSignHIPAANoticeActivity()
	{
		Intent intent = new Intent( this, SignHIPAANoticeActivity.class );
		startActivity( intent );

	} // startSignHIPAANoticeActivity


	@Override
	public boolean onTouch( View view, MotionEvent event )
	{
		view.performClick();                                    // Perform default action
		//Log.i( LOG_TAG, "Touch detected: " + view.getId() );

		if( event.getAction() == MotionEvent.ACTION_UP )        // Only handle single event
		{
			switch( view.getId() )
			{
				case R.id.button_view_latest:
					startViewLatestMealEntryActivity();
					break;

				case R.id.button_view_history:
					startViewMealEntryHistoryActivity();
					break;

				case R.id.button_save:
					// Create the MealEntry using the MealItems:
					MealEntry mealEntry =
							getMealEntryFromInputData();        // Get the data from EditTexts
					if( mealEntry != null )                     // Null if all fields not valid
					{
						mAuthTask = new LogMealTask( mealEntry );   // Save it to the database
						mAuthTask.execute();

					} // if
					break;

				case R.id.add_meal_item_button:
					TableRow newRow = createTableRow();         // Adds correct views to a new row
					mealItemTable.addView( newRow );            // Add the row to the layout
					break;

				case R.id.remove_meal_item_button:
					removeParentTableRow( view );               // Remove row containing the button
					resetTags();                                // Allow indexing/performing actions
					break;

				case R.id.search_meal_item_button:
					searchFoodItem( getFoodItem( view ) );
					break;

			} // switch

		} // if

		return false;

	} // onTouch


	/**
	 * createCircleButton - Creates a circle button
	 *
	 * @param buttonId         - The resource id to be used in the onTouch method to handle
	 *                         behavior.
	 * @param stringResourceId - The string resource to set as the text inside the button.
	 * @return a new Button
	 */
	@NonNull
	private Button createCircleButton( int buttonId, int stringResourceId )
	{
		// Convert dp to pixels:
		int pixels = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
				30, getResources().getDisplayMetrics() );
		int buttonMargin = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics() );

		// Create the button:
		Button button = new Button( getApplicationContext() );
		button.setId( buttonId );                            // Set the button's ID

		// Set the background image:
		Drawable buttonImage = getResources().getDrawable( R.drawable.button_round );
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
			button.setBackground( buttonImage );

		// Set the dimensions:
		final TableRow.LayoutParams params = new TableRow.LayoutParams( pixels, pixels );
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 )
			params.setMarginEnd( buttonMargin );
		params.gravity = Gravity.CENTER;
		button.setLayoutParams( params );

		button.setPadding( 2, 2, 2, 2 );
		button.setGravity( Gravity.CENTER );
		button.setRight( buttonMargin );

		// Setup the text:
		button.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
		button.setText( stringResourceId );
		button.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ), Typeface.BOLD );
		button.setTextColor( getResources().getColor( R.color.colorPrimaryDark ) );

		button.setOnTouchListener( this );

		return button;

	} // createCircleButton


	/**
	 * createEditText - Creates a new EditText View in a uniform style.
	 *
	 * @param meal_item_name - Resource id. Used as the EditText hint
	 * @param inputType      - InputType.SOME_TYPE
	 * @return a new EditText
	 */
	@NonNull
	private EditText createEditText( int meal_item_name, int inputType )
	{
		EditText editText =
				new EditText( new ContextThemeWrapper( this, R.style.EditTextCustomHolo ), null, 0 );
		int margin = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
				8, getResources().getDisplayMetrics() );
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT );
		lp.setMargins( 0, 0, margin, 0 );
		editText.setLayoutParams( lp );
		editText.setTextSize( TypedValue.COMPLEX_UNIT_SP, 14 );
		editText.setInputType( inputType );
		editText.setHint( meal_item_name );
		editText.setRight( margin );

		return editText;

	} // createEditText


	/**
	 * Gathers meal data from the inputs on the screen and creates a MealEntry object
	 */
	private MealEntry getMealEntryFromInputData()
	{
		// Create the MealEntry to be returned
		MealEntry mealEntry = new MealEntry();
		// Create a UUID (or Guid)
		mealEntry.setRemoteId( UUID.randomUUID().toString() );
		WhichMeal whichMealValue =
				WhichMeal.valueOf( whichMeal.getSelectedItem().toString().toUpperCase() );
		mealEntry.setWhichMeal( whichMealValue );


		// Create the MealItems based on the number of MealItems on the screen. For each
		// 		carb entry EditText, there will be a serving name and serving number EditText
		int totalCarbs = 0;                                 // Tally up the total carbs
		int numberEntries = allCarbEntries.size();          // In case ArrayList size changes
		for( int index = 0; index < numberEntries; index++ )
		{
			MealItem mealItem = new MealItem();             // Create the MealItem object
			mealItem.setRemoteId( UUID.randomUUID().toString() );
			mealItem.setMealId( mealEntry.getRemoteId() );

			// The food name is required:
			EditText servingNameEditText = allServingNameEntries.get( index );
			String mealName = servingNameEditText.getText().toString();
			if( errorOnEmpty( servingNameEditText ) )       // Set error if empty
				return null;                                // And signal not to save to DB
			mealItem.setName( mealName );                   // ...otherwise, continue

			int carbs = 0;                                  // Default number of carbs
			EditText carbEditText = allCarbEntries.get( index );
			if( errorOnEmpty( carbEditText ) )              // Set error if empty
				return null;                                // And signal not to save to DB
			String txt = carbEditText.getText().toString();
			try
			{
				carbs = Math.round( Float.parseFloat( txt ));
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

			int servings = 1;                               // Default number of servings
			EditText servingNumberEditText = allServingNumberEntries.get( index );
			if( errorOnEmpty( servingNumberEditText ) )     // Set error if empty
				return null;                                // And signal not to save to DB
			txt = servingNumberEditText.getText().toString();
			try
			{
				servings = Integer.parseInt( txt );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			mealItem.setServings( servings );

			mealItem.setCarbs( carbs );
			totalCarbs += ( carbs * servings );

			if( DEBUG ) Log.e( LOG_TAG, mealItem.toString() );

			mealItems.add( mealItem );

		} // for

		mealEntry.setMealItems( mealItems );                // Set the new object to our MealEntry
		mealEntry.setTotalCarbs( totalCarbs );              // The calculated number of carbs

		PatientSingleton patient = PatientSingleton.getInstance();
		mealEntry.setUserName( patient.getUserName() );

		return mealEntry;

	} // getMealEntryFromInputData


	/**
	 * createTableRow - Creates a new TableRow item in a uniform style
	 *
	 * @return a new TableRow object
	 */
	@NonNull
	private TableRow createTableRow()
	{
		// Create the row to be returned:
		TableLayout.LayoutParams layoutParams =
				new TableLayout.LayoutParams( TableLayout.LayoutParams.MATCH_PARENT,
						TableLayout.LayoutParams.WRAP_CONTENT );
		TableRow newRow = new TableRow( getApplicationContext() );
		newRow.setLayoutParams( layoutParams );
		newRow.setGravity( Gravity.CENTER_VERTICAL );
		// Set the tag so that we have an index to manipulate the row later. This will
		// 		be the size *before* adding new item. This allows us to do 0-based indexing:
		newRow.setTag( allServingNameEntries.size() );

		EditText newServingNameEditText =
				createEditText( R.string.meal_item_name, InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE );
		TableRow.LayoutParams layoutParams1 =
				new TableRow.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT );
		layoutParams1.weight = 1;
		newServingNameEditText.setLayoutParams( layoutParams1 );
		EditText newCarbEditText =
				createEditText( R.string.carbs, InputType.TYPE_CLASS_NUMBER );
		EditText newServingNumberEditText =
				createEditText( R.string.servings, InputType.TYPE_CLASS_NUMBER );

		// Create new buttons:
		Button addButton = createCircleButton( R.id.add_meal_item_button, R.string.Plus );
		Button removeButton = createCircleButton( R.id.remove_meal_item_button, R.string.minus );
		Button searchButton = createCircleButton( R.id.search_meal_item_button, R.string.Question_Mark );

		// Add all the EditText view to an ArrayList so that we can iterate
		//		over them get all of the text entered from them later.
		allServingNameEntries.add( newServingNameEditText );
		allCarbEntries.add( newCarbEditText );
		allServingNumberEntries.add( newServingNumberEditText );

		// Add the new row to an ArrayList so that we can reset the Tags each
		//		time we remove a row. Otherwise, it will throw an error if we
		//		try to delete a row whose Tag has already been deleted and not
		//		restored.
		allTableRows.add( newRow );

		// Add all the new views to the row:
		newRow.addView( newServingNameEditText );
		newRow.addView( newCarbEditText );
		newRow.addView( newServingNumberEditText );
		newRow.addView( addButton );
		newRow.addView( removeButton );
		newRow.addView( searchButton );

		return newRow;

	} // createTableRow


	/**
	 * errorOnEmpty - Sets an error message to the EditText if it is empty.
	 *
	 * @param editText - The view to check
	 * @return whether there was an error
	 */
	private boolean errorOnEmpty( EditText editText )
	{
		if( editText.getText().toString().isEmpty() )
		{
			editText.setError( getResources().getString( R.string.error_field_required ) );
			return true;
		}

		return false;

	} // errorOnEmpty


	/**
	 * removeParentTableRow - Removes the row based on the view that was clicked
	 *
	 * @param view - The view that was clicked
	 */
	private void removeParentTableRow( View view )
	{
		TableRow tableRow = (TableRow) view.getParent();    // The button is a direct child of row
		int tag = (int) tableRow.getTag();                    // Get the index
		allServingNameEntries.remove( tag );                // Remove all from this index
		allCarbEntries.remove( tag );
		allServingNumberEntries.remove( tag );
		allTableRows.remove( tag );

		TableLayout tableLayout = (TableLayout) tableRow.getParent();
		tableLayout.removeView( tableRow );                    // Remove this row from the table

	} // removeParentTableRow

	/**
	 * getFoodItem - gets the searched food item name
	 *
	 * @param view - The view that was clicked
	 */
	private String getFoodItem( View view)
	{
		EditText servingEntry;								// Holding the edittext for the food name
		TableRow tableRow = (TableRow) view.getParent();    // The button is a direct child of row
		int tag = (int) tableRow.getTag();
		//Log.i("Tag","Got tag " + tag);
		currentTag = tag;
		servingEntry = allServingNameEntries.get( tag );
		return servingEntry.getText().toString();			// Get the food name
	} // getFoodItem

	/**
	 * resetTags() - Resets all of the tags of the TableRow objects we have created. This allows
	 * us to avoid errors later. If the user removes a row, we might have a higher index
	 * set as a row's tag than we have in the ArrayLists. When we try to remove the row
	 * with that higher index than the number of items in our ArrayLists, we get an error
	 */
	private void resetTags()
	{
		int tag = 0;                                        // Start at index 0
		for( TableRow row : allTableRows )                    // Iterate through all rows
			row.setTag( tag++ );                            // Set the tag and increment the index

	} // resetTags


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

		mealForm.setVisibility( show
				? View.GONE
				: View.VISIBLE );
		mealForm.animate().setDuration( shortAnimTime ).alpha(
				show
						? 0
						: 1 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				mealForm.setVisibility( show
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
	 * Starts ViewMealEntryActivity
	 */
	private void startViewLatestMealEntryActivity()
	{
		Intent intent = new Intent( this, ViewMealEntryActivity.class );
		startActivity( intent );

	} // startViewLatestMealEntryActivity


	/**
	 * Starts ViewMealEntryHistoryActivity
	 */
	private void startViewMealEntryHistoryActivity()
	{
		Intent intent = new Intent( this, ViewMealEntryHistoryActivity.class );
		startActivity( intent );

	} // startViewMealEntryHistoryActivity

	/**
	 * Starts the food item selector
	 */
    private void startSelectFoodItem(ArrayList<String> nbdTags, ArrayList<String> foodNames)
    {
        Intent intent = new Intent( this, ViewFoodListingsActivity.class );
        intent.putExtra( FOOD_LIST, foodNames );
        intent.putExtra( TAG_LIST, nbdTags );
        startActivityForResult( intent , REQUEST_CODE);

    } // startSelectFoodItem

	/**
	 * An AsyncTask used to log the meal on a separate thread
	 */
	public class LogMealTask extends AsyncTask<Void, Void, ErrorCode>
	{
		//		private static final String LOG_TAG = "LogMealTask";
		MealEntry mealEntry;

		LogMealTask( MealEntry mealEntry )
		{
			this.mealEntry = mealEntry;

		} // constructor

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
				// Save the MealEntry and its MealItems
				return logMealEntryAction.logMealEntry( getApplicationContext(), mealEntry );

			}
			catch( Exception e )
			{
				e.printStackTrace();
				return ErrorCode.UNKNOWN;
			}

		} // doInBackground


		@Override
		protected void onPostExecute( final ErrorCode errorCode )
		{
			mAuthTask = null;
			showProgress( false );

			switch( errorCode )
			{
				case NO_ERROR:                                   // 0: No error
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "meal logged" ) );
					setResult( RESULT_OK, returnData );          // Return ok result for activity result
					finish();                                    // Close the activity
					break;

				case UNKNOWN:                                    // 1: Unknown - something went wrong
					Snackbar.make( coordinatorLayout, "Unknown error", Snackbar.LENGTH_LONG )
							.show();
					break;

				default:
					Snackbar.make( coordinatorLayout, "Error", Snackbar.LENGTH_LONG ).show();
					break;
			}

		} // onPostExecute


		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			showProgress( false );

		} // onCancelled

	} // LogMealTask

	private void searchFoodItem(String foodItem)
	{
		String url = "https://api.nal.usda.gov/ndb/search/?format=json&q="+foodItem+"&sort=r&max=25&offset=0&api_key=ZYcqAYZf16n0t0PSXqXzYV8a5GFxZ9WW8UEC36zm";
		RequestQueue queue = Volley.newRequestQueue(this);

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
		{
			public void onResponse(String response) {
				ArrayList<String> nbdTag = new ArrayList<String>();
				ArrayList<String> foodList = new ArrayList<String>();
				String text, name;//, nbdTag;
				int beginning, ending, selected;

				text = response;

				while(text.contains("name")) {
					beginning = text.indexOf("name") + 8;
					ending = text.indexOf("\"", beginning);
					name = text.substring(beginning, ending);
					ending = name.indexOf(", UPC", 0);
					if(ending >= 0)
					{
						name = name.substring(0, ending);
					}//remove UPC tag if there
					name = name.toLowerCase();
					name = name.substring(0, 1).toUpperCase() + name.substring(1);
					foodList.add(name);

					beginning = text.indexOf("ndbno") + 9;
					ending = text.indexOf('"', beginning);
					nbdTag.add(text.substring(beginning, ending));
					text = text.substring(ending);
				}//end while

				startSelectFoodItem(nbdTag, foodList);

				Log.d("Search API Call","Successful search call");
			}
		}, new Response.ErrorListener()
		{
			public void onErrorResponse(VolleyError error) {
				Log.e("Search API Call","Error:"+error.getMessage());
			}
		});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(stringRequest);
	}//end searchFoodItem(String)

	private void findFoodNutrition(String nbdTag)
	{
		String url = "https://api.nal.usda.gov/ndb/V2/reports?";
		RequestQueue queue = Volley.newRequestQueue(this);

		url +="ndbno="+nbdTag+"&type=b&format=json&api_key=ZYcqAYZf16n0t0PSXqXzYV8a5GFxZ9WW8UEC36zm";

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
		{
			public void onResponse(String response) {

				String text;
				int beginning, ending;

				text = response;

				beginning = text.indexOf("Carbohydrate");

				beginning = text.indexOf("value", beginning) + 8;
				ending = text.indexOf('"', beginning);

				carbs = text.substring(beginning, ending);

				EditText servingEntry, carbEntry;					// the needed edit texts
				servingEntry = allServingNameEntries.get( currentTag );    // Get the food name edittext
				carbEntry = allCarbEntries.get( currentTag );		// Get the carb entry edittext
				servingEntry.setText(foodName);						// Set the new food name
				carbEntry.setText(carbs);							// Set the new carb amount

				foodName = "";										//reset food name, carbs, and tag
				carbs = "0";
				currentTag = -1;

				Log.d("NDB API Call","Successful api call");
			}
		}, new Response.ErrorListener()
		{
			public void onErrorResponse(VolleyError error) {
				Log.e("NDB API Call","Error:"+error.getMessage());
			}
		});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(stringRequest);
	}//end searchFoodItem(String)

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			String selectedTag = data.getStringExtra(ViewFoodListingsActivity.RETURN_KEY);
			//Log.i("Result", "Given selection: " + selectedTag);

			if(!(selectedTag.equalsIgnoreCase("N/A")))
			{
				foodName = data.getStringExtra(ViewFoodListingsActivity.RETURN_KEY2);
				findFoodNutrition(selectedTag);
			} // make sure actual tag was given
		} // ensure proper result code
	} // onActivityResult
} // class
