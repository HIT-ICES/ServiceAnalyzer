package com.hitices.analyzer.controller;

import com.google.gson.Gson;
import com.hitices.analyzer.base.Service;
import com.hitices.analyzer.bean.MPathInfo;
import com.hitices.analyzer.bean.MServiceBuildBean;
import com.hitices.analyzer.bean.MServiceRegisterBean;
import com.hitices.analyzer.client.BuildServiceClient;
import com.hitices.analyzer.client.SvcServiceClient;
import com.hitices.analyzer.common.MResponse;
import com.hitices.analyzer.utils.GetServiceInfo;
import com.hitices.analyzer.utils.GetSourceCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/analyzer")
public class MController {

    private static Logger logger = LogManager.getLogger(MController.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Autowired
    private BuildServiceClient buildServiceClient;

    @Autowired
    private SvcServiceClient svcServiceClient;

    @PostMapping("/newService")
    public MResponse newService(@RequestBody MServiceRegisterBean registerBean){
        executorService.submit(() -> {
            logger.info("Starting analysis: Downloading···");
            UUID uuid = UUID.randomUUID();
            MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(registerBean.getRepo(), registerBean.getVersion().getPatch());
            logger.info("Starting analysis: Analysing···");
            Service service = GetServiceInfo.getMservice(registerBean.getVersion().getPatch(), mPathInfo);
            service.setId(uuid.toString());
            service.setName(registerBean.getServiceName());
            GetSourceCode.deleteDir(mPathInfo.getLocal());
            Gson gson = new Gson();
            logger.info(gson.toJson(service));
            logger.info("Starting analysis: Building···");
            String image = buildServiceClient.buildService(new MServiceBuildBean(service.getId(), registerBean.getRepo(),registerBean.getServiceName(),registerBean.getVersion()));
            if (!image.isEmpty()){
                logger.info(image);
                service.setImageUrl(image);
                logger.info("Starting analysis: Registering···");
                svcServiceClient.addService(service);
            }else {
                logger.error("Build failed");
            }
        });
        return MResponse.successMResponse();

    }

}
