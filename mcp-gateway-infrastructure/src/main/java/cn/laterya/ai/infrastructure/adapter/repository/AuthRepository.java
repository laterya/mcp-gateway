package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayAuthPO;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class AuthRepository implements IAuthRepository {

    @Resource private IMcpGatewayAuthDao mcpGatewayAuthDao;
    @Resource private IMcpGatewayDao mcpGatewayDao;

    @Override
    public int queryEffectiveGatewayAuthCount(String gatewayId) { return mcpGatewayAuthDao.queryEffectiveGatewayAuthCount(gatewayId); }

    @Override
    public McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity cmd) {
        McpGatewayAuthPO poReq = new McpGatewayAuthPO(); poReq.setGatewayId(cmd.getGatewayId()); poReq.setApiKey(cmd.getApiKey());
        McpGatewayAuthPO po = mcpGatewayAuthDao.queryByGatewayIdAndApiKey(poReq);
        if (po == null) return null;
        return McpGatewayAuthVO.builder().gatewayId(po.getGatewayId()).apiKey(po.getApiKey()).rateLimit(po.getRateLimit()).expireTime(po.getExpireTime()).status(AuthStatusEnum.AuthConfig.get(po.getStatus())).build();
    }

    @Override
    public void insert(McpGatewayAuthVO vo) {
        mcpGatewayAuthDao.insert(McpGatewayAuthPO.builder().gatewayId(vo.getGatewayId()).apiKey(vo.getApiKey()).rateLimit(vo.getRateLimit()).expireTime(vo.getExpireTime()).status(vo.getStatus().getCode()).build());
    }

    @Override
    public AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId) {
        return AuthStatusEnum.GatewayConfig.get(mcpGatewayDao.queryByGatewayId(gatewayId).getAuth());
    }

    @Override
    public void updateAuth(String gatewayId, Integer rateLimit, LocalDateTime expireTime) {
        McpGatewayAuthPO existing = mcpGatewayAuthDao.queryByGatewayId(gatewayId);
        if (existing == null) { log.warn("鉴权记录不存在 gatewayId:{}", gatewayId); return; }
        existing.setRateLimit(rateLimit);
        if (expireTime != null) existing.setExpireTime(expireTime);
        mcpGatewayAuthDao.updateById(existing);
        log.info("更新鉴权 gatewayId:{} rateLimit:{}", gatewayId, rateLimit);
    }

    @Override
    public void deleteByGatewayId(String gatewayId) {
        mcpGatewayAuthDao.deleteByGatewayId(gatewayId);
        log.info("删除鉴权 gatewayId:{}", gatewayId);
    }
}
