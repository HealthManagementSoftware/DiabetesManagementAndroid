package com.healthmanagement.diabetesassistant.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class HIPAAPrivacyNotice
{
    private int id;
    private String remoteId;
    private String title;
    private String noticeText;
    private String version;
    private Date createdAt;
    private Date updatedAt;
    private ArrayList<PatientSignedHIPAANotice> signatures;

    public HIPAAPrivacyNotice()
    {
        remoteId = UUID.randomUUID().toString();
        signatures = new ArrayList<>();

    } // constructor

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemoteId()
    {
        return remoteId;
    }

    public void setRemoteId( String remoteId )
    {
        this.remoteId = remoteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<PatientSignedHIPAANotice> getSignatures() {
        return signatures;
    }

    public void setSignatures(ArrayList<PatientSignedHIPAANotice> signatures) {
        this.signatures = signatures;
    }

} // class
