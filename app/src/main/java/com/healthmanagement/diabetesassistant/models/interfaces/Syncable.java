package com.healthmanagement.diabetesassistant.models.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

public interface Syncable
{
	boolean isSynced();
	void setSynced( boolean isSyncable );
	JSONObject toJSONObject() throws JSONException;

} // interface
