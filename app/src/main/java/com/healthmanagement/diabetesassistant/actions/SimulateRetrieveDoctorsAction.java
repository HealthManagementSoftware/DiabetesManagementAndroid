package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;

import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveDoctorsAction;
import com.healthmanagement.diabetesassistant.models.Doctor;

import java.util.ArrayList;
import java.util.List;

public class SimulateRetrieveDoctorsAction implements IRetrieveDoctorsAction
{
	@Override
	public List<Doctor> retrieveDoctors( Context context )
	{
		List<Doctor> doctors = new ArrayList<>();

		Doctor drew = new Doctor();
		drew.setEmail( "dr.drew@gmail.com" );
		drew.setFirstName( "Drew" );
		drew.setLastName( "Manley" );
		drew.setDegreeAbbreviation( "MD" );
		doctors.add( drew );

		Doctor phil = new Doctor();
		phil.setEmail( "dr.phil@yahoo.com" );
		phil.setFirstName( "Phillip" );
		phil.setLastName( "Philson" );
		phil.setDegreeAbbreviation( "MD" );
		doctors.add( phil );

		Doctor john = new Doctor();
		john.setEmail( "dr.john@gmail.com" );
		john.setFirstName( "John" );
		john.setLastName( "Johnson" );
		john.setDegreeAbbreviation( "MD" );
		doctors.add( john );

		return doctors;

	} // retrieveDoctors

} // class
