package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;
import android.util.Log;

import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAAVersionAction;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;
import com.healthmanagement.diabetesassistant.singletons.WebClientConnectionSingleton;
import com.healthmanagement.diabetesassistant.utils.JsonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class RemoteRetrieveNewestHIPAAVersionAction implements IRetrieveNewestHIPAAVersionAction
{
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public String getNewestVersion( Context context ) throws JSONException
    {
        WebClientConnectionSingleton webConnection =        // Get the connection manager
                WebClientConnectionSingleton.getInstance( context );

        String jsonString = webConnection.sendRetrieveHIPAAVersionRequest( null );

        if( DEBUG ) Log.e( LOG_TAG, "Response: " + jsonString );
        if( !jsonString.isEmpty() )
        {
            JSONObject jsonObject = new JSONObject( jsonString );
            if( jsonObject.has( DB.KEY_VERSION ) )
                return jsonObject.getString( DB.KEY_VERSION );

        } // if

        return null;

    } // getNewestVersion

} // class
