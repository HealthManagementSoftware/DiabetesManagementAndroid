package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;
import android.util.Log;

import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.db.DB;
import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;
import com.healthmanagement.diabetesassistant.singletons.WebClientConnectionSingleton;
import com.healthmanagement.diabetesassistant.utils.JsonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import static com.healthmanagement.diabetesassistant.activities.MainActivity.DEBUG;

public class RemoteRetrieveNewestHIPAANoticeAction implements IRetrieveNewestHIPAANoticeAction
{
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public HIPAAPrivacyNotice getNotice( Context context ) throws JSONException
    {
        HIPAAPrivacyNotice notice = new HIPAAPrivacyNotice();
        WebClientConnectionSingleton webConnection =        // Get the connection manager
                WebClientConnectionSingleton.getInstance( context );

        String jsonString = webConnection.sendRetrieveHIPAARequest( null );

        if( DEBUG ) Log.e( LOG_TAG, "Response: " + jsonString );
        if( !jsonString.isEmpty() )
        {
            JSONObject jsonObject = new JSONObject( jsonString );
            if( jsonObject.has( DB.KEY_REMOTE_ID ) )
                notice.setRemoteId( jsonObject.getString( DB.KEY_REMOTE_ID ) );
            if( jsonObject.has( DB.KEY_CREATED_AT ) )
                notice.setCreatedAt( JsonUtilities.dateFromJsonString(
                        jsonObject.getString( DB.KEY_CREATED_AT ) ) );
            if( jsonObject.has( DB.KEY_UPDATED_AT ) )
                notice.setCreatedAt( JsonUtilities.dateFromJsonString(
                        jsonObject.getString( DB.KEY_UPDATED_AT ) ) );
            if( jsonObject.has( DB.KEY_TITLE ) )
                notice.setNoticeText( jsonObject.getString( DB.KEY_TITLE ) );
            if( jsonObject.has( DB.KEY_NOTICE_TEXT ) )
                notice.setNoticeText( jsonObject.getString( DB.KEY_NOTICE_TEXT ) );
            if( jsonObject.has( DB.KEY_VERSION ) )
                notice.setVersion( jsonObject.getString( DB.KEY_VERSION ) );
        }

        return notice;

    } // getNotice

} // class
