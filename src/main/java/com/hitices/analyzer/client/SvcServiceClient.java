package com.hitices.analyzer.client;

import com.hitices.analyzer.base.Service;
import com.hitices.analyzer.bean.MServiceBuildBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "SvcService", url = "http://svc-service:80")
public interface SvcServiceClient {
    @RequestMapping(value = "/service/add", method = RequestMethod.POST)
    public void addService(@RequestBody Service service);
}
