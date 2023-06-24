package com.hitices.analyzer.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Version {
    private String major;
    private String minor;
    private String patch;

    @Override
    public String toString() {
        return "Version{" +
                "major='" + major + '\'' +
                ", minor='" + minor + '\'' +
                ", patch='" + patch + '\'' +
                '}';
    }
}
