package io.kestra.plugin.fmatest;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.exceptions.NotFoundException;

public class InfluxFactory {
    public static char[] token = "testfma-token".toCharArray();
    public static String org = "testfma";
    public static String bucket = "testfma-bucket";

    public static InfluxDBClient influxDBClient() {
        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
        //try{
        //    client.getBucketsApi().findBucketByName(bucket);
        //} catch (NotFoundException e) {
        //    client.getBucketsApi().createBucket(bucket, org);
        //}
        return client;
    }
}
