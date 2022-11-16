package com.sty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: SongTianYe
 * @Description: 公用 yaml Loader
 * @Date: 2022/11/15 14:29
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonYamlLoader {

    /**
     * rowKey-handler-param
     */
    private RowKeyHandlerParam rowKey;

    /**
     * key 注意与yaml文件保持一直
     */
    private List<YamlHandlerParam> mapping;
}
