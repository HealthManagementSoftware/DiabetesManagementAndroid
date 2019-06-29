package com.healthmanagement.diabetesassistant.actions.interfaces;

import android.content.Context;

import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;

import org.json.JSONException;

public interface IRetrieveNewestHIPAANoticeAction
{
    HIPAAPrivacyNotice getNotice( Context context ) throws JSONException;

}
