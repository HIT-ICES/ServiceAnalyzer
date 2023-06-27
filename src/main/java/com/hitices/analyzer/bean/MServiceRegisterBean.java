package com.hitices.analyzer.bean;

import com.hitices.analyzer.base.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MServiceRegisterBean {
    private String repo;
    private String serviceName;
    private Version version;
    private String callback;
}
