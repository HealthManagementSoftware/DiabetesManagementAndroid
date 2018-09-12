package com.sugarcubes.myglucose.actions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.repositories.DbMealEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.Date;

public class LoginSimulationAction implements ILoginAction
{
	ApplicationUser user;

	public LoginSimulationAction( ApplicationUser user )
	{
		this.user = user;

	} // constructor


	@Override
	public boolean attemptLogin( String username, String password, Context context )
	{
		try {
//			Thread.sleep( 2000 );
			DbPatientRepository patientRepository = new DbPatientRepository( context );
			PatientSingleton patientSingleton = PatientSingleton.getInstance();
//			Cursor cursor = context.getContentResolver().query( MyGlucoseContentProvider.PATIENT_USERS_URI,
//					null, DB.TABLE_USERS + "." + DB.KEY_USER_LOGGED_IN + "=?",
//					new String[]{ String.valueOf( 1 ) }, null );
//			patientRepository.logIn( PatientSingleton.getInstance(), cursor );
			patientSingleton.setEmail( username );
			patientSingleton.setUserName( username );
			patientSingleton.setFirstName( "John" );
			patientSingleton.setLastName( "Doe" );
			patientSingleton.setAddress1( "123 Example Lane" );
			patientSingleton.setAddress2( "Apt. 2" );
			patientSingleton.setCity( "Exampleview" );
			patientSingleton.setState( "TN" );
			patientSingleton.setZip1( 12345 );
			patientSingleton.setZip2( 1234 );
			patientSingleton.setPhoneNumber( "(555) 999-1234" );
			Date date = new Date();
			patientSingleton.setDate( date );
			patientSingleton.setTimestamp( date.getTime() );

			Doctor dr = new Doctor();
			dr.setEmail( "dr.jones@example.com" );
			patientSingleton.setDoctor( dr );

			patientRepository.createLogin( patientSingleton );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return true;

	} // attemptLogin

} // class
