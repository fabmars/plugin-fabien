package io.kestra.plugin.fmatest;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class InfluxFactory {
    private static char[] token = "testfma-token".toCharArray();
    private static String org = "testfma";
    private static String bucket = "testfma-bucket";

    public static InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
    }
}
