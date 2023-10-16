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
    private String method;
    private String info;
    private Double inputSize = null;
    private Double outputSize = null;

    @Override
    public String toString() {
        return "Interface{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", info='" + info + '\'' +
                ", inputSize=" + inputSize +
                ", outputSize=" + outputSize +
                '}';
    }
}
