package com.hitices.analyzer.base;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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
    private List<Interface> interfaces;
    private Resource idleResource = new Resource();
    private Resource desiredResource = new Resource();
    private int desiredCapability = 0;

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", repo='" + repo + '\'' +
                ", image='" + image + '\'' +
                ", version=" + version +
                ", interfaces=" + interfaces +
                '}';
    }
}
