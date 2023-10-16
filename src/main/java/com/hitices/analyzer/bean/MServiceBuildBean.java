package com.hitices.analyzer.bean;

import com.hitices.analyzer.base.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: wangteng
 * @e-mail: Willtynn@outlook.com
 * @date: 2023/10/13 18:53
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MServiceBuildBean {
    private String repo;
    private String serviceName;
    private Version version;
}
