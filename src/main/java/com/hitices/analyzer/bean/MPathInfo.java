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

    @Override
    public String toString() {
        return "MPathInfo{" +
                "application_Path='" + applicationPath + '\'' +
                ", controller_ListPath=" + controllerListPath +
                '}';
    }
}
