package com.hitices.analyzer.base;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor

public class Service {
    private String id;
    private String name;
    private String repo;
    private String image;
    private Version version;
    private Map<String, Interface> interfaces;

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", repo='" + repo + '\'' +
                ", image='" + image + '\'' +
                ", version=" + version.toString() +
                ", interfaces=" + interfaces.toString() +
                '}';
    }
}
