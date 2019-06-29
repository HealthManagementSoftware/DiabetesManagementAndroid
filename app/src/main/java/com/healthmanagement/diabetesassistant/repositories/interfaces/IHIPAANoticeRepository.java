package com.healthmanagement.diabetesassistant.repositories.interfaces;

import com.healthmanagement.diabetesassistant.models.HIPAAPrivacyNotice;

public interface IHIPAANoticeRepository extends IRepository<HIPAAPrivacyNotice>
{
    HIPAAPrivacyNotice readNewest();

} // interface
