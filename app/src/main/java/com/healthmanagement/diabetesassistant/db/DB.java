package com.healthmanagement.diabetesassistant.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper
{
    // Database Version
    private static final int DATABASE_VERSION = 20;

    // DB Name:
    public static final String DB_NAME = "myglucose";

    // Table names:
    public static final String TABLE_USERS                = "User";
    public static final String TABLE_PATIENTS             = "Patient";
    public static final String TABLE_DOCTORS              = "Doctor";
    public static final String TABLE_GLUCOSE_ENTRIES      = "GlucoseEntry";
    public static final String TABLE_MEAL_ENTRIES         = "MealEntry";
    public static final String TABLE_MEAL_ITEMS           = "MealItem";
    public static final String TABLE_EXERCISE_ENTRIES     = "ExerciseEntry";
    public static final String TABLE_PEDOMETER            = "Pedometer";
    public static final String TABLE_HIPAA_NOTICES        = "HIPAANotice";
    public static final String TABLE_PATIENT_SIGNED_HIPAA = "PatientSignedHIPAA";

    public static final String PATIENT_USERS = "PatientUser";
    public static final String DOCTOR_USERS  = "DoctorUser";

    // Use in ContentProvider to do joins

    // Also add tables here to use in a for loop:
    private String[] tables = {
            TABLE_USERS,
            TABLE_PATIENTS,
            TABLE_DOCTORS,
            TABLE_GLUCOSE_ENTRIES,
            TABLE_MEAL_ENTRIES,
            TABLE_MEAL_ITEMS,
            TABLE_EXERCISE_ENTRIES,
            TABLE_PEDOMETER,
            TABLE_HIPAA_NOTICES,
            TABLE_PATIENT_SIGNED_HIPAA
    };

    // Shared db table keys:
    public static final String KEY_ID                              = "_id";
    public static final String KEY_TIMESTAMP                       = "timestamp";
    public static final String KEY_REMOTE_ID                       = "id";
    public static final String KEY_WHICH_MEAL                      = "whichMeal";
    public static final String KEY_CREATED_AT                      = "createdAt";
    public static final String KEY_UPDATED_AT                      = "updatedAt";
    public static final String KEY_SYNCED                          = "synced";
    // ApplicationUser table keys:
    public static final String KEY_USER_LOGGED_IN                  = "loggedIn";
    public static final String KEY_USER_LOGIN_TOKEN                = "remoteLoginToken";
    public static final String KEY_USER_LOGIN_EXPIRATION_TIMESTAMP = "remoteLoginExpiration";
    public static final String KEY_USERNAME                        = "userName";
    public static final String KEY_USER_FIRST_NAME                 = "firstName";
    public static final String KEY_USER_LAST_NAME                  = "lastName";
    public static final String KEY_USER_TYPE                       = "userType";
    public static final String KEY_USER_EMAIL                      = "email";
    public static final String KEY_USER_ADDRESS1                   = "address1";
    public static final String KEY_USER_ADDRESS2                   = "address2";
    public static final String KEY_USER_CITY                       = "city";
    public static final String KEY_USER_STATE                      = "state";
    public static final String KEY_USER_ZIP1                       = "zip1";
    public static final String KEY_USER_ZIP2                       = "zip2";
    public static final String KEY_USER_PHONE                      = "phoneNumber";
    public static final String KEY_USER_HEIGHT                     = "height";
    public static final String KEY_USER_WEIGHT                     = "weight";
    // Patient table keys:
    public static final String KEY_DOCTOR                          = "doctor";
    public static final String KEY_PATIENT_ID                      = "patientId";
    public static final String KEY_DR_USERNAME                     = "doctorUserName";
    public static final String KEY_GLUCOSE_ENTRIES                 = "glucoseEntries";
    public static final String KEY_MEAL_ENTRIES                    = "mealEntries";
    public static final String KEY_EXERCISE_ENTRIES                = "exerciseEntries";
    // Doctor table keys:
    public static final String KEY_DR_ID                           = "doctorId";
    public static final String KEY_DR_DEGREE_ABBREVIATION          = "degreeAbbreviation";
    // GlucoseEntry table keys:
    public static final String KEY_GLUCOSE_MEASUREMENT             = "measurement";
    public static final String KEY_GLUCOSE_BEFORE_AFTER            = "beforeAfter";
    // MealEntry table keys:
    public static final String KEY_MEAL_ENTRY_TOTAL_CARBS          = "totalCarbs";
    public static final String KEY_MEAL_ITEMS                      = "mealItems";
    // MealItem table keys:
    public static final String KEY_MEAL_ITEM_NAME                  = "name";
    public static final String KEY_MEAL_ID                         = "mealId";
    public static final String KEY_MEAL_ITEM_CARBS                 = "carbs";
    public static final String KEY_MEAL_ITEM_SERVINGS              = "servings";
    // ExerciseEntry table keys:
    public static final String KEY_EXERCISE_MINUTES                = "minutes";
    public static final String KEY_EXERCISE_NAME                   = "name";
    public static final String KEY_EXERCISE_STEPS                  = "steps";
    // Pedometer table:
    //	public static final String KEY_PED_HOUR                        = "hour";
    //	public static final String KEY_PED_DAY                         = "day";
    //	public static final String KEY_PED_MONTH                       = "month";
    //	public static final String KEY_PED_YEAR                        = "year";
    public static final String KEY_PED_COORD_X                     = "xCoord";
    public static final String KEY_PED_COORD_Y                     = "yCoord";
    public static final String KEY_PED_STEP_COUNT                  = "steps";
    // HIPAA Notices table:
    public static final String KEY_TITLE                           = "title";
    public static final String KEY_VERSION                         = "version";
    public static final String KEY_NOTICE_TEXT                     = "noticeText";
    // Patient Signed HIPAA Notices table:
    // Patient:
    public static final String KEY_PATIENT_SIGNED_HIPAA_NOTICE_ID  = "patientSignedHIPAANoticeId";
    public static final String KEY_PATIENT_SIGNED_HIPAA_NOTICE     = "patientSignedHIPAANotice";
    public static final String KEY_PATIENT_USER_NAME               = "patientUserName";
    // Table:
    public static final String KEY_NOTICE_ID                       = "noticeId";
    public static final String KEY_SIGNED_AT                       = "signedAt";


    public DB( Context context )
    {
        super( context, DB_NAME, null, DATABASE_VERSION );
    }

    public DB( Context context, SQLiteDatabase.CursorFactory factory )
    {
        super( context, DB_NAME, factory, DATABASE_VERSION );
    }

    public DB( Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler )
    {
        super( context, DB_NAME, factory, DATABASE_VERSION, errorHandler );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        // ADD NEW TABLES HERE

        // CREATE LOGIN TABLE
        String CREATE_USERS_TABLE = "CREATE TABLE "
                + TABLE_USERS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_USER_EMAIL + " TEXT,"
                + KEY_USER_LOGGED_IN + " INTEGER,"
                + KEY_USER_LOGIN_TOKEN + " TEXT,"
                + KEY_USER_LOGIN_EXPIRATION_TIMESTAMP + " INTEGER,"
                + KEY_USER_TYPE + " TEXT,"
                + KEY_USER_FIRST_NAME + " TEXT,"
                + KEY_USER_LAST_NAME + " TEXT,"
                + KEY_USER_ADDRESS1 + " TEXT,"
                + KEY_USER_ADDRESS2 + " TEXT,"
                + KEY_USER_CITY + " TEXT,"
                + KEY_USER_STATE + " TEXT,"
                + KEY_USER_ZIP1 + " INTEGER,"
                + KEY_USER_ZIP2 + " INTEGER,"
                + KEY_USER_PHONE + " TEXT,"
                + KEY_USER_HEIGHT + " REAL,"
                + KEY_USER_WEIGHT + " REAL,"
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT, "
                + KEY_PATIENT_SIGNED_HIPAA_NOTICE_ID + " TEXT, "
                + KEY_TIMESTAMP + " INTEGER );";    // Retrieve as a *long* value

        // CREATE PATIENTS TABLE
        String CREATE_PATIENTS_TABLE = "CREATE TABLE "
                + TABLE_PATIENTS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_DR_ID + " TEXT, "
                + KEY_DR_USERNAME + " TEXT );";

        // CREATE DOCTORS TABLE
        String CREATE_DOCTORS_TABLE = "CREATE TABLE "
                + TABLE_DOCTORS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_DR_DEGREE_ABBREVIATION + " TEXT );";

        // CREATE GLUCOSE TABLE
        String CREATE_GLUCOSE_ENTRIES_TABLE = "CREATE TABLE "
                + TABLE_GLUCOSE_ENTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_SYNCED + " INTEGER, "
                + KEY_GLUCOSE_MEASUREMENT + " REAL, "    // DEFAULT: mmol/L. May need conversion
                + KEY_GLUCOSE_BEFORE_AFTER + " INTEGER, "
                + KEY_WHICH_MEAL + " INTEGER, "
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT, "
                + KEY_TIMESTAMP + " INTEGER );";            // Retrieve as a *long* value

        // CREATE MEALS TABLE
        String CREATE_MEAL_ENTRIES_TABLE = "CREATE TABLE "
                + TABLE_MEAL_ENTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_SYNCED + " INTEGER, "
                + KEY_MEAL_ENTRY_TOTAL_CARBS + " INTEGER, "
                + KEY_WHICH_MEAL + " INTEGER, "
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT, "
                + KEY_TIMESTAMP + " INTEGER);";    // Retrieve as a *long* value

        // CREATE MEAL ITEMS TABLE
        String CREATE_MEAL_ITEMS_TABLE = "CREATE TABLE "
                + TABLE_MEAL_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_MEAL_ID + " TEXT, "
                + KEY_MEAL_ITEM_NAME + " TEXT, "
                + KEY_MEAL_ITEM_CARBS + " INTEGER, "
                + KEY_MEAL_ITEM_SERVINGS + " INTEGER);";

        // CREATE EXERCISE TABLE
        String CREATE_EXERCISE_ENTRIES_TABLE = "CREATE TABLE "
                + TABLE_EXERCISE_ENTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_SYNCED + " INTEGER, "
                + KEY_EXERCISE_NAME + " TEXT, "
                + KEY_EXERCISE_MINUTES + " INTEGER, "
                + KEY_EXERCISE_STEPS + " INTEGER, "
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT, "
                + KEY_TIMESTAMP + " INTEGER);";    // Retrieve as a *long* value


        // CREATE PEDOMETER TABLE
        String CREATE_PEDOMETER_TABLE = "CREATE TABLE "
                + TABLE_PEDOMETER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_USERNAME + " TEXT,"
                + KEY_SYNCED + " INTEGER, "
                + KEY_PED_COORD_X + " REAL, "
                + KEY_PED_COORD_Y + " REAL,"
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT, "
                + KEY_PED_STEP_COUNT + " INTEGER"
                + " );";

        // CREATE HIPAA TABLE
        String CREATE_HIPAA_TABLE = "CREATE TABLE "
                + TABLE_HIPAA_NOTICES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_TITLE + " TEXT, "
                + KEY_NOTICE_TEXT + " TEXT, "
                + KEY_VERSION + " TEXT, "
                + KEY_CREATED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT );";

        // CREATE PATIENT SIGNED HIPAA TABLE
        String CREATE_PATIENT_SIGNED_HIPAA_TABLE = "CREATE TABLE "
                + TABLE_PATIENT_SIGNED_HIPAA + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REMOTE_ID + " TEXT, "
                + KEY_SYNCED + " INTEGER, "
                + KEY_PATIENT_USER_NAME + " TEXT, "
                + KEY_PATIENT_ID + " TEXT, "
                + KEY_NOTICE_ID + " TEXT, "
                + KEY_SIGNED_AT + " TEXT, "
                + KEY_UPDATED_AT + " TEXT );";


        db.execSQL( CREATE_USERS_TABLE );
        db.execSQL( CREATE_PATIENTS_TABLE );
        db.execSQL( CREATE_DOCTORS_TABLE );
        db.execSQL( CREATE_GLUCOSE_ENTRIES_TABLE );
        db.execSQL( CREATE_MEAL_ENTRIES_TABLE );
        db.execSQL( CREATE_MEAL_ITEMS_TABLE );
        db.execSQL( CREATE_EXERCISE_ENTRIES_TABLE );
        db.execSQL( CREATE_PEDOMETER_TABLE );
        db.execSQL( CREATE_HIPAA_TABLE );
        db.execSQL( CREATE_PATIENT_SIGNED_HIPAA_TABLE );

        // NOTE: When a DB has to hit an index, and then another table, this adds
        // 	another query it has to perform. Since we will only be storing one patient
        // 	and one doctor, this extra query would be more overhead than is required.

        // Create Indexes:
        //		// CREATE PATIENTS ENTRIES INDEX ON user_id
        //		String CREATE_PATIENTS_INDEX = "CREATE INDEX `patients_index` ON " +
        //			TABLE_PATIENTS + "(" + DB.KEY_USER_ID + ");";
        //
        //		// CREATE DOCTORS ENTRIES INDEX ON user_id
        //		String CREATE_DOCTORS_INDEX = "CREATE INDEX `patients_index` ON " +
        //				TABLE_DOCTORS + "(" + DB.KEY_USER_ID + ");";

        // CREATE GLUCOSE ITEMS INDEX ON user_id
        String CREATE_GLUCOSE_ENTRIES_INDEX = "CREATE INDEX `glucose_entries_synced_index` ON " +
                TABLE_GLUCOSE_ENTRIES + "(" + DB.KEY_SYNCED + ");";

        // CREATE EXERCISE ITEMS INDEX ON user_id
        String CREATE_EXERCISE_ENTRIES_INDEX = "CREATE INDEX `exercise_entries_synced_index` ON " +
                TABLE_EXERCISE_ENTRIES + "(" + DB.KEY_SYNCED + ");";

        // CREATE MEAL ENTRIES INDEX ON user_id
        String CREATE_MEAL_ENTRIES_INDEX = "CREATE INDEX `meal_entries_index` ON " +
                TABLE_MEAL_ENTRIES + "(" + DB.KEY_REMOTE_ID + ");";

        String CREATE_MEAL_ENTRIES_SYNCED_INDEX = "CREATE INDEX `meal_entries_synced_index` ON " +
                TABLE_MEAL_ENTRIES + "(" + DB.KEY_SYNCED + ");";

        // CREATE MEAL ITEMS INDEX ON meal_id
        String CREATE_MEAL_ITEMS_INDEX = "CREATE INDEX `meal_items_index` ON " +
                TABLE_MEAL_ITEMS + "(" + DB.KEY_MEAL_ID + ");";

        // CREATE MEAL ITEMS INDEX ON meal_id
        String CREATE_HIPAA_NOTICES_INDEX = "CREATE INDEX `patient_hipaa_index` ON " +
                TABLE_PATIENT_SIGNED_HIPAA + "(" + DB.KEY_PATIENT_ID + ", " + KEY_NOTICE_ID + ");";
        //
        //		db.execSQL( CREATE_PATIENTS_INDEX );
        //		db.execSQL( CREATE_DOCTORS_INDEX );
        db.execSQL( CREATE_GLUCOSE_ENTRIES_INDEX );
        db.execSQL( CREATE_EXERCISE_ENTRIES_INDEX );
        db.execSQL( CREATE_MEAL_ENTRIES_INDEX );
        db.execSQL( CREATE_MEAL_ENTRIES_SYNCED_INDEX );
        db.execSQL( CREATE_MEAL_ITEMS_INDEX );
        db.execSQL( CREATE_HIPAA_NOTICES_INDEX );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
        // Drop all tables if exist
        for ( String table : tables )
        {
            db.execSQL( "DROP TABLE IF EXISTS " + table );
        }

        // Create tables again
        onCreate( db );

    } // onUpgrade

} // class
