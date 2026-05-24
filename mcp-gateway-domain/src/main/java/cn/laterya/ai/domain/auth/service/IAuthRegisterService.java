package cn.laterya.ai.domain.auth.service;

import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;

public interface IAuthRegisterService {

    String register(RegisterCommandEntity commandEntity);

}
