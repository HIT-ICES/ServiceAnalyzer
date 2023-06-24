package com.hitices.analyzer.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Interface {
    private String id;
    private String path;
    private Double inputSize;
    private Double outputSize;

    @Override
    public String toString() {
        return "Interface{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", inputSize=" + inputSize +
                ", outputSize=" + outputSize +
                '}';
    }
}
