package com.healthmanagement.diabetesassistant.actions.interfaces;

import android.content.Context;

import org.json.JSONException;

public interface IRetrieveNewestHIPAAVersionAction
{
    String getNewestVersion( Context context ) throws JSONException;
}
