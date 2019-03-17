package com.healthmanagement.diabetesassistant.models;

import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.utils.JsonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class Doctor extends ApplicationUser
{
	protected String degreeAbbreviation;

	public Doctor()
	{
		degreeAbbreviation = "MD";                // Default abbreviation
		id = "";
		loginToken = "";
		loginExpirationTimestamp = 0;
		firstName = "";
		lastName = "";
		phoneNumber = "";
		city = "";
		state = "";
		address1 = "";
		address2 = "";
		weight = "";
		height = "";

	} // constructor

	public String getDegreeAbbreviation()
	{
		return degreeAbbreviation;
	}

	public void setDegreeAbbreviation( String degreeAbbreviation )
	{
		this.degreeAbbreviation = degreeAbbreviation;
	}


	public static Doctor fromJSONObject( JSONObject drObj ) throws JSONException
	{
		Doctor doctor =
				new Doctor();                       // Create a new doctor object

		if( drObj != null )
		{
			// Set all of the doctors' attributes from the json objects returned:
			if( drObj.has( DB.KEY_REMOTE_ID ) )
				doctor.setId( drObj.getString( DB.KEY_REMOTE_ID ) );               // Id
			if( drObj.has( DB.KEY_DR_DEGREE_ABBREVIATION ) )
				doctor.setDegreeAbbreviation( drObj.getString( DB.KEY_DR_DEGREE_ABBREVIATION ) );
			if( drObj.has( DB.KEY_USERNAME ) )
				doctor.setUserName( drObj.getString( DB.KEY_USERNAME ) );          // Username
			if( drObj.has( DB.KEY_USER_FIRST_NAME ) )
				doctor.setFirstName( drObj.getString( DB.KEY_USER_FIRST_NAME ) );  // FirstName
			if( drObj.has( DB.KEY_USER_LAST_NAME ) )
				doctor.setLastName( drObj.getString( DB.KEY_USER_LAST_NAME ) );    // LastName
			if( drObj.has( DB.KEY_USER_EMAIL ) )
				doctor.setEmail( drObj.getString( DB.KEY_USER_EMAIL ) );           // Email
			if( drObj.has( DB.KEY_USER_ADDRESS1 ) )
				doctor.setAddress1( drObj.getString( DB.KEY_USER_ADDRESS1 ) );     // Address1
			if( drObj.has( DB.KEY_USER_ADDRESS2 ) )
				doctor.setAddress2( drObj.getString( DB.KEY_USER_ADDRESS2 ) );     // Address2
			if( drObj.has( DB.KEY_USER_CITY ) )
				doctor.setCity( drObj.getString( DB.KEY_USER_CITY ) );             // City
			if( drObj.has( DB.KEY_USER_STATE ) )
				doctor.setState( drObj.getString( DB.KEY_USER_STATE ) );           // State
			if( drObj.has( DB.KEY_USER_ZIP1 ) )
				doctor.setZip1( drObj.getInt( DB.KEY_USER_ZIP1 ) );                // Zip 1
			if( drObj.has( DB.KEY_USER_ZIP2 ) )
				doctor.setZip2( drObj.getInt( DB.KEY_USER_ZIP2 ) );                // Zip 2
			if( drObj.has( DB.KEY_CREATED_AT ) )
				doctor.setCreatedAt( JsonUtilities.dateFromJsonString(
						drObj.getString( DB.KEY_CREATED_AT ) ) );                  // Created At
			if( drObj.has( DB.KEY_UPDATED_AT ) )
				doctor.setCreatedAt( JsonUtilities.dateFromJsonString(
						drObj.getString( DB.KEY_UPDATED_AT ) ) );                  // Updated At
			if( drObj.has( DB.KEY_USER_HEIGHT ) )
				doctor.setHeight( drObj.getString( DB.KEY_USER_HEIGHT ) );         // Height
			if( drObj.has( DB.KEY_USER_WEIGHT ) )
				doctor.setWeight( drObj.getString( DB.KEY_USER_WEIGHT ) );         // Weight
			if( drObj.has( DB.KEY_USER_PHONE ) )
				doctor.setPhoneNumber( drObj.getString( DB.KEY_USER_PHONE ) );     // Phone
			if( drObj.has( DB.KEY_TIMESTAMP ) )
				doctor.setTimestamp( drObj.getLong( DB.KEY_TIMESTAMP ) );          // Timestamp

			doctor.setLoggedIn( false );                                           // Logged In

		} // if

		return doctor;

	} // fromJSONObject


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject doctor = new JSONObject();
		// APPLICATION USER ATTRIBUTES:
		if( !id.isEmpty() )
			doctor.put( DB.KEY_REMOTE_ID, id );                                    // Id
		if( !email.isEmpty() )
			doctor.put( DB.KEY_USER_EMAIL, email );                                // Email
		if( !firstName.isEmpty() )
			doctor.put( DB.KEY_USER_FIRST_NAME, firstName );                       // First Name
		if( !lastName.isEmpty() )
			doctor.put( DB.KEY_USER_LAST_NAME, lastName );                         // Last Name
		if( !userName.isEmpty() )
			doctor.put( DB.KEY_USERNAME, userName );                               // User Name
		if( !address1.isEmpty() )
			doctor.put( DB.KEY_USER_ADDRESS1, address1 );                          // Address 1
		if( !address2.isEmpty() )
			doctor.put( DB.KEY_USER_ADDRESS2, address2 );                          // Address 2
		if( !city.isEmpty() )
			doctor.put( DB.KEY_USER_CITY, city );                                  // City
		if( !state.isEmpty() )
			doctor.put( DB.KEY_USER_STATE, state );                                // State
		if( !phoneNumber.isEmpty() )
			doctor.put( DB.KEY_USER_PHONE, phoneNumber );                          // Phone
		if( height != null && !height.isEmpty() )
			doctor.put( DB.KEY_USER_HEIGHT, height );                              // Height
		if( weight != null && !weight.isEmpty() )
			doctor.put( DB.KEY_USER_WEIGHT, weight );                              // Weight
		if( loginToken != null && !loginToken.isEmpty() )
			doctor.put( DB.KEY_USER_LOGIN_TOKEN, loginToken );                     // Login Token
		if( zip1 > 0 )
			doctor.put( DB.KEY_USER_ZIP1, zip1 );                                  // Zip 1
		if( zip2 > 0 )
			doctor.put( DB.KEY_USER_ZIP1, zip2 );                                  // Zip 2
		if( timestamp > 0 )
			doctor.put( DB.KEY_TIMESTAMP, timestamp );                             // Timestamp
		if( loginExpirationTimestamp > 0 )
			doctor.put( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP, loginExpirationTimestamp );
		if( createdAt != null )
			doctor.put( DB.KEY_CREATED_AT, createdAt.toString() );                 // Created At
		if( updatedAt != null )
			doctor.put( DB.KEY_UPDATED_AT, updatedAt.toString() );                 // Updated At
		// DOCTOR ATTRIBUTES:
		if( degreeAbbreviation != null && !degreeAbbreviation.isEmpty() )
			doctor.put( DB.KEY_DR_DEGREE_ABBREVIATION, degreeAbbreviation );       // Abbreviation

		return doctor;

	}

	@Override
	public String toString()
	{
		try
		{
			return toJSONObject().toString();
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return "";
		}

	} // toString

} // class
