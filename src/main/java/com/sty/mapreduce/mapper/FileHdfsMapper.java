package com.sty.mapreduce.mapper;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sty.constant.EtcConstant;
import com.sty.model.CommonYamlLoader;
import com.sty.utils.CommonUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * @Author: SongTianYe
 * @Description: FileHdfsMapper
 * @Date: 2022/11/15 14:48
 */
public class FileHdfsMapper extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

    private CommonYamlLoader commonYamlLoader;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        commonYamlLoader = JSONUtil.toBean(context.getConfiguration().get(EtcConstant.CONFIGURATION_YAML_KEY),
                CommonYamlLoader.class);

        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //csv、tsv
        String[] row = StrUtil.splitToArray(value.toString(), "\t");
        // 处理rowKey
        byte[] rowKey = CommonUtils.handlerRowKey(row, commonYamlLoader.getRowKey());
        //初始化put对象
        Put put = new Put(rowKey);
        // 添加addColumn
        commonYamlLoader.getMapping().forEach(
                yamlHandlerParam -> put.addColumn(Bytes.toBytes(yamlHandlerParam.getColumnFamily()),
                        Bytes.toBytes(yamlHandlerParam.getName()),
                        ObjectUtil.isAllNotEmpty(yamlHandlerParam.getClassName(),
                                yamlHandlerParam.getMethodName()) ?
                                Bytes.toBytes((String) ClassUtil.invoke(
                                        yamlHandlerParam.getClassName(), yamlHandlerParam.getMethodName(),
                                        new Object[]{row[yamlHandlerParam.getIndexInRow()]})) :
                                Bytes.toBytes(row[yamlHandlerParam.getIndexInRow()])
                )
        );
        context.write(new ImmutableBytesWritable(rowKey), put);
    }
}
