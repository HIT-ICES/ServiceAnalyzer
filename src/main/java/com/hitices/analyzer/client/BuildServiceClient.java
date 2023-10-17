package com.hitices.analyzer.client;

import com.hitices.analyzer.bean.MServiceBuildBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**s
 * @author: wangteng
 * @e-mail: Willtynn@outlook.com
 * @date: 2023/10/13 18:50
 */
@FeignClient(name = "BuildService", url = "http://build-service:8001")
public interface BuildServiceClient {
    @RequestMapping(value = "/image/build", method = RequestMethod.POST)
    String buildService(@RequestBody MServiceBuildBean serviceBuildBean);
}
