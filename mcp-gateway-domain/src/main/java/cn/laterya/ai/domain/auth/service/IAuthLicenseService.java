package cn.laterya.ai.domain.auth.service;

import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;

public interface IAuthLicenseService {

    boolean checkLicense(LicenseCommandEntity commandEntity);

    /**
     * 鉴权校验，失败时抛出 AppException
     */
    default void checkLicenseOrThrow(LicenseCommandEntity commandEntity) {
        if (!checkLicense(commandEntity)) {
            throw new cn.laterya.ai.types.exception.AppException(
                    cn.laterya.ai.types.enums.McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }
    }

}
