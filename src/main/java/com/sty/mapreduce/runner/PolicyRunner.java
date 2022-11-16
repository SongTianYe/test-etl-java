package com.sty.mapreduce.runner;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.sty.mapreduce.mapper.FileHdfsMapper;
import com.sty.model.CommonYamlLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.sty.constant.EtcConstant.*;

/**
 * @Author: SongTianYe
 * @Description: PolicyRunner
 * @Date: 2022/11/15 15:58
 */
public class PolicyRunner extends Configured implements Tool {

    private static final String TABLE_NAME = "test_csv_import";

    @Override
    public int run(String[] args) throws Exception {
        Dict application = YamlUtil.load(ResourceUtil.getReader("application.yaml", Charset.defaultCharset()));

        Configuration conf = HBaseConfiguration.create();
        conf.set(HBASE_ZOOKEEPER_QUORUM, application.getByPath(HBASE_ZOOKEEPER_QUORUM, String.class));

        // 传递参数序列化存入上下文
        conf.set(CONFIGURATION_YAML_KEY, JSONUtil.toJsonPrettyStr(
                YamlUtil.load(ResourceUtil.getResource("policy.yaml").openStream(), CommonYamlLoader.class)));
        conf.set(CONFIGURATION_YAML_APPLICATION, JSONUtil.toJsonPrettyStr(application));

        Job job = Job.getInstance(conf, this.getClass().getSimpleName());
        Path inPath = new Path(args[0]);
        FileInputFormat.addInputPath(job, inPath);
        job.setMapperClass(FileHdfsMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);
        TableMapReduceUtil.initTableReducerJob(TABLE_NAME, null, job);
        job.setNumReduceTasks(0);

        // 设置mapreduce输出数据格式：到hFile格式文件中
        job.setOutputFormatClass(HFileOutputFormat2.class);

        // 告知往那种表中去写 根据表region信息 划分目录
        HTable table = new HTable(conf, TABLE_NAME);
        HFileOutputFormat2.configureIncrementalLoad(job, table, table.getRegionLocator());
        Path outPutPath = new Path(application.getByPath("hdfs.outPutTempDir", String.class) +
                "policy_" + System.currentTimeMillis());
        FileOutputFormat.setOutputPath(job, outPutPath);

        boolean isSuccess = job.waitForCompletion(true);
        if (!isSuccess) {
            throw new IOException("Job running with error");
        }
        LoadIncrementalHFiles load = new LoadIncrementalHFiles(conf);

        load.doBulkLoad(outPutPath, table);
        return isSuccess ? 0 : 1;
    }


    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new PolicyRunner(), args));
    }
}
