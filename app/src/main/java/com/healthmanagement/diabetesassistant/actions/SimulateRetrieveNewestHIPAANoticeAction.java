package com.healthmanagement.diabetesassistant.actions;

import android.content.Context;

import com.healthmanagement.diabetesassistant.actions.interfaces.IRetrieveNewestHIPAANoticeAction;
import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;

import org.json.JSONException;

import java.util.Date;

public class SimulateRetrieveNewestHIPAANoticeAction implements IRetrieveNewestHIPAANoticeAction
{
    @Override
    public HIPAAPrivacyNotice getNotice( Context context )
    {
        HIPAAPrivacyNotice notice = new HIPAAPrivacyNotice();
        notice.setRemoteId( "TestId" );
        notice.setVersion( "1.0" );
        notice.setNoticeText( "Test HIPAA Notice text..." );
        notice.setUpdatedAt( new Date() );
        notice.setCreatedAt( new Date() );
        return notice;
    }

}
