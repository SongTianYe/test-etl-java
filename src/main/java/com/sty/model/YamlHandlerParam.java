package com.sty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongTianYe
 * @Description: YAML-BEAN
 * @Date: 2022/11/14 17:53
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class YamlHandlerParam {

    /**
     * 参数名称
     */
    private String name;

    /**
     * csv、tsv 行中索引
     */
    private Integer indexInRow;

    /**
     * 列族
     */
    private String columnFamily;

    /**
     * 处理器类名 eg: com.cheche.* 可空-不处理
     */
    private String className;

    /**
     * 方法名 可用-不处理
     */
    private String methodName;

}

