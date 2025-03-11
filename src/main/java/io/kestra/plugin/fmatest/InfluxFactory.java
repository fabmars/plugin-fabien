package io.kestra.plugin.fmatest;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class InfluxFactory {
    public static char[] token = "testfma-token".toCharArray();
    public static String org = "testfma";
    public static String bucket = "testfma-bucket";

    public static InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
    }
}
