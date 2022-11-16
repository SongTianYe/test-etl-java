package com.sty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: SongTianYe
 * @Description: RowKeyHanderParam
 * @Date: 2022/11/16 10:59
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RowKeyHandlerParam {

    private List<Integer> indexInRows;
    private String connectStr;
    private String className;
    private String methodName;
}

