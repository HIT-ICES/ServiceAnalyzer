package com.hitices.analyzer.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MPathInfo {

    private String gitUrl;

    private String applicationPath;

    private List<String> controllerListPath;

    private String local;

    @Override
    public String toString() {
        return "MPathInfo{" +
                "gitUrl='" + gitUrl + '\'' +
                ", applicationPath='" + applicationPath + '\'' +
                ", controllerListPath=" + controllerListPath +
                ", local='" + local + '\'' +
                '}';
    }
}
