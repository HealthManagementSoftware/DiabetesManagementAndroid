package com.healthmanagement.diabetesassistant.repositories.interfaces;

import com.healthmanagement.diabetesassistant.models.PatientSignedHIPAANotice;


public interface IPatientSignedHIPAARepository extends IJoinRepository<PatientSignedHIPAANotice>
{
    boolean exists( PatientSignedHIPAANotice notice );
}
