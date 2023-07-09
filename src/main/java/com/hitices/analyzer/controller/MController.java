package com.hitices.analyzer.controller;

import com.hitices.analyzer.base.Service;
import com.hitices.analyzer.bean.MPathInfo;
import com.hitices.analyzer.bean.MServiceRegisterBean;
import com.hitices.analyzer.common.MResponse;
import com.hitices.analyzer.utils.GetServiceInfo;
import com.hitices.analyzer.utils.GetSourceCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/analyzer")
public class MController {

    private static Logger logger = LogManager.getLogger(MController.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    @PostMapping("/newService")
    public MResponse newService(@RequestBody MServiceRegisterBean registerBean){
        executorService.submit(() -> {
            MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(registerBean.getRepo(), registerBean.getVersion().getPatch());
            Service service = GetServiceInfo.getMservice(registerBean.getVersion().getPatch(), mPathInfo);
            service.setName(registerBean.getServiceName());
            logger.info(service.toString());

            logger.info("Trying to send service infos to server...");
            // TODO: 2023/6/27 回调
        });
        return MResponse.successMResponse();

    }

}
