package cn.laterya.ai.domain.auth.service;

import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;

public interface IAuthLicenseService {

    boolean checkLicense(LicenseCommandEntity commandEntity);

}
