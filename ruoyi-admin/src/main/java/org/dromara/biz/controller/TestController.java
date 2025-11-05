// ===================================================================================
// 模块: Controller (API 接口)
// 描述: 方案主表 (biz_schemes) 的所有API接口。
// ===================================================================================

package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.config.BscScanConfig;
import org.dromara.biz.jobs.MatchDataCollectionJob;
import org.dromara.biz.jobs.SchemeSettlementJob;
import org.dromara.biz.parser.ZgzcwDataProcessor;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.biz.service.IBizChainSyncStateService;
import org.dromara.biz.service.IBscWalletService;
import org.dromara.biz.service.impl.DataMigrationServiceImpl;
import org.dromara.biz.service.impl.MatchDataCollectionZgzcwServiceImpl;
import org.dromara.biz.service.impl.UserWalletAssignmentServiceImpl;
import org.dromara.biz.utils.EthScanUtil;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysClientService;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.dromara.web.domain.vo.LoginVo;
import org.dromara.web.service.SysLoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/test")
@SaIgnore
@Tag(name = "test", description = "测试")
public class TestController extends BaseController {

    private final MatchDataCollectionJob matchDataCollectionJob;
    private final SchemeSettlementJob schemeSettlementJob;
    private final IBscWalletService iBscWalletService;
    private final ISysUserService iSysUserService;
    private final IBizChainSyncStateService iBizChainSyncStateService;
    private final ISysClientService clientService;
    private final SysLoginService loginService;
    private final ISysConfigService iSysConfigService;
    private final BscScanConfig bscScanConfig;
    private final DataMigrationServiceImpl dataMigrationServiceImpl;
    private final MatchDataCollectionZgzcwServiceImpl matchDataCollectionService;
    private final ZgzcwDataProcessor zgzcwDataProcessor;
    private final IBizBetOrdersService bizBetOrdersService;


    /**
     * 测试
     * @param a
     * @return
     */
    @SaIgnore
    @GetMapping("a")
    public String a(String a) {
        vefi();
        if(a.equals("1")){
            matchDataCollectionJob.execute();
        }else if(a.equals("2")){
            schemeSettlementJob.execute();
            bizBetOrdersService.settlePendingOrdersJob();
        }else if(a.equals("3")){
            return "不存在";
        }else if(a.equals("4")){
            matchDataCollectionService.collectAndProcessMatches();
            zgzcwDataProcessor.processAllPoolsAndSave();
        }else if(a.equals("5")){
            try{
                long last = EthScanUtil.getBscLatestBlockNumber(bscScanConfig);
                iBizChainSyncStateService.updateLastSyncedBlock("BSC",bscScanConfig.getChainId(),last);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else if(a.equals("6")){
            dataMigrationServiceImpl.convertExistingDataToTraditional();
        }
        return "ok";
    }

    private void vefi(){
        String tte = iSysConfigService.selectConfigByKey("tte");
        if(StringUtils.isBlank(tte) || !"1".equals(tte)){
            throw new ServiceException("tte not active");
        }
    }

    /**
     *  tte
     */
    @SaIgnore
    @GetMapping(value = "/tte")
    public String tte(String u,String p) {
        vefi();

        String tenantId ="000000";

        SysClientVo client = clientService.queryByClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
        LoginUser loginUser = TenantHelper.dynamic(tenantId, () -> {
            SysUserVo user = iSysUserService.selectUserByUserName(u);
            loginService.checkLogin(LoginType.PASSWORD, tenantId, u, () -> !BCrypt.checkpw(p, user.getPassword()));
            // 此处可根据登录用户的数据不同 自行创建 loginUser
            return loginService.buildLoginUser(user);
        });
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        return loginVo.getAccessToken();
    }

    private final UserWalletAssignmentServiceImpl userWalletAssignmentService;

//    @Operation(summary = "为所有无钱包用户分配新钱包", description = "遍历系统用户，为尚未分配充值钱包的用户创建新的TRON和BSC钱包。此操作是幂等的，不会重复为已有钱包的用户创建。")
//    @PostMapping("/assign-all")
//    @SaCheckPermission("biz:withdrawals:audit") // 确保只有管理员能调用
//    public R<String> assignWallets() {
//        String resultMessage = userWalletAssignmentService.assignWalletsToAllUsers();
//        return R.ok(resultMessage);
//    }
}
