package com.hitices.analyzer.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: wangteng
 * @e-mail: Willtynn@outlook.com
 * @date: 2023/10/15 10:42
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private int cpu = 0;
    private int ram = 0;
    private int disk = 0;
    private int gpuCore = 0;
    private int gpuMem = 0;
}
