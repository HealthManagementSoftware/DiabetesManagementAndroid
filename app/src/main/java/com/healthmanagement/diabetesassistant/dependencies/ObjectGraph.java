package com.healthmanagement.diabetesassistant.dependencies;

import android.content.Context;

import com.healthmanagement.diabetesassistant.actions.DbLogExerciseEntryAction;
import com.healthmanagement.diabetesassistant.actions.DbLogGlucoseEntryAction;
import com.healthmanagement.diabetesassistant.actions.DbLogMealEntryAction;
import com.healthmanagement.diabetesassistant.actions.RemoteLoginAction;
import com.healthmanagement.diabetesassistant.actions.RemoteRegisterPatientAction;
import com.healthmanagement.diabetesassistant.actions.RemoteRetrieveDoctorsAction;
import com.healthmanagement.diabetesassistant.actions.RemoteRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.actions.RemoteRetrieveNewestHIPAAVersionAction;
import com.healthmanagement.diabetesassistant.actions.RemoteSyncPatientDataAction;
import com.healthmanagement.diabetesassistant.actions.SimulateRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILogExerciseEntryAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILogGlucoseEntryAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILogMealEntryAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.ILoginAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRegisterPatientAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveDoctorsAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAAVersionAction;
import com.healthmanagement.diabetesassistant.actions.interfaces.ISyncPatientDataAction;
import com.healthmanagement.diabetesassistant.repositories.DbApplicationUserRepository;
import com.healthmanagement.diabetesassistant.repositories.DbDoctorRepository;
import com.healthmanagement.diabetesassistant.repositories.DbExerciseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.DbGlucoseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.DbHIPAAPrivacyNoticeRepository;
import com.healthmanagement.diabetesassistant.repositories.DbMealEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.DbPatientRepository;
import com.healthmanagement.diabetesassistant.repositories.DbPatientSignedHIPAARepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IApplicationUserRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IDoctorRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IExerciseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IGlucoseEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IHIPAANoticeRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientSignedHIPAARepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IMealEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IPatientRepository;

import java.util.HashMap;
import java.util.Map;

// Adapted from:
// https://softwareengineering.stackexchange.com/questions/354465/pure-dependency-injection-how-to-implement-it
class ObjectGraph
{
	private final Map<Class<?>, Object> dependencies = new HashMap<>();    // Holds all dependencies

	ObjectGraph( Context context )    // package-private
	{
		// Log Actions:
		ILogMealEntryAction logMealEntryAction = new DbLogMealEntryAction();
		dependencies.put( ILogMealEntryAction.class, logMealEntryAction );
		ILogExerciseEntryAction logExerciseEntryAction = new DbLogExerciseEntryAction();
		dependencies.put( ILogExerciseEntryAction.class, logExerciseEntryAction );
		ILogGlucoseEntryAction logGlucoseEntryAction = new DbLogGlucoseEntryAction();
		dependencies.put( ILogGlucoseEntryAction.class, logGlucoseEntryAction );

		// Sync Actions:
		ISyncPatientDataAction syncPatientDataAction = new RemoteSyncPatientDataAction();
		dependencies.put( ISyncPatientDataAction.class, syncPatientDataAction );

		// Misc. Remote Actions:
		ILoginAction remoteLoginAction = new RemoteLoginAction();
		dependencies.put( ILoginAction.class, remoteLoginAction );
		IRetrieveDoctorsAction retrieveDoctorsAction = new RemoteRetrieveDoctorsAction(); //RetrieveDoctorsAction();
		dependencies.put( IRetrieveDoctorsAction.class, retrieveDoctorsAction );
		IRegisterPatientAction registerPatientAction = new RemoteRegisterPatientAction(); //RemoteRegisterPatientAction();
		dependencies.put( IRegisterPatientAction.class, registerPatientAction );
		IRetrieveNewestHIPAANoticeAction retrieveNewestHIPAANoticeAction =
				new RemoteRetrieveNewestHIPAANoticeAction();
		dependencies.put( IRetrieveNewestHIPAANoticeAction.class, retrieveNewestHIPAANoticeAction );
		IRetrieveNewestHIPAAVersionAction retrieveNewestHIPAAVersionAction =
				new RemoteRetrieveNewestHIPAAVersionAction();
		dependencies.put( IRetrieveNewestHIPAAVersionAction.class, retrieveNewestHIPAAVersionAction );

		// Repositories:
		// Note: Instantiate in order from leaf nodes to parent nodes.
		IExerciseEntryRepository exerciseEntryRepository = new DbExerciseEntryRepository( context );
		dependencies.put( IExerciseEntryRepository.class, exerciseEntryRepository );
		IGlucoseEntryRepository glucoseEntryRepository = new DbGlucoseEntryRepository( context );
		dependencies.put( IGlucoseEntryRepository.class, glucoseEntryRepository );
		IMealEntryRepository mealEntryRepository = new DbMealEntryRepository( context );
		dependencies.put( IMealEntryRepository.class, mealEntryRepository );
		IHIPAANoticeRepository ihipaaNoticeRepository = new DbHIPAAPrivacyNoticeRepository( context );
		dependencies.put( IHIPAANoticeRepository.class, ihipaaNoticeRepository );
		IPatientSignedHIPAARepository patientSignedHIPAANoticeRepository
				= new DbPatientSignedHIPAARepository( context );
		dependencies.put( IPatientSignedHIPAARepository.class, patientSignedHIPAANoticeRepository );
		IDoctorRepository doctorRepository = new DbDoctorRepository( context );
		dependencies.put( IDoctorRepository.class, doctorRepository );
		IApplicationUserRepository userRepository = new DbApplicationUserRepository( context );
		dependencies.put( IApplicationUserRepository.class, userRepository );
		IPatientRepository patientRepository = new DbPatientRepository( context );
		dependencies.put( IPatientRepository.class, patientRepository );

	} // constructor


	<T> T get( Class<T> model )
	{
		return model.cast( dependencies.get( model ) );

	} // get


	<T> void putMock( Class<T> clazz, T object )
	{
		dependencies.put( clazz, object );

	} // putMock

} // class
