package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;

import com.healthmanagement.diabetesassistant.actions.interfaces.ILogMealEntryAction;
import com.healthmanagement.diabetesassistant.dependencies.Dependencies;
import com.healthmanagement.diabetesassistant.models.MealEntry;
import com.healthmanagement.diabetesassistant.enums.ErrorCode;
import com.healthmanagement.diabetesassistant.repositories.DbMealEntryRepository;
import com.healthmanagement.diabetesassistant.repositories.interfaces.IMealEntryRepository;

public class DbLogMealEntryAction implements ILogMealEntryAction
{
	@Override
	public ErrorCode logMealEntry( Context context, MealEntry mealEntry ) throws InterruptedException
	{
		IMealEntryRepository mealEntryRepository = Dependencies.get( IMealEntryRepository.class );
		mealEntryRepository.create( mealEntry );

		return ErrorCode.NO_ERROR;

	} // logMealEntry

} // class
