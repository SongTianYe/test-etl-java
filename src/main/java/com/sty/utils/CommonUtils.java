package com.sty.utils;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.sty.model.RowKeyHandlerParam;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @Author: SongTianYe
 * @Description: CommonUtils
 * @Date: 2022/11/15 16:31
 */
public class CommonUtils {

    public static byte[] handlerRowKey(String[] row, RowKeyHandlerParam param) {
        return ObjectUtil.isAllNotEmpty(param.getClassName(), param.getMethodName()) ?
                Bytes.toBytes((String) ClassUtil.invoke(
                        param.getClassName(), param.getMethodName(),
                        new Object[]{param.getIndexInRows()
                                .stream()
                                .map(a -> row[a]).toArray(String[]::new)})) :
                Bytes.toBytes(row[param.getIndexInRows().get(0)]);
    }


}
