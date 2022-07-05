package com.learn;


import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

public class FlinkSQLDemoApplication {


    public static void main(String...s) {
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                //.inBatchMode()
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);

        tEnv.executeSql("CREATE TABLE transactions (\n" +
                "    account_id  BIGINT,\n" +
                "    amount      BIGINT,\n" +
                "    transaction_time TIMESTAMP(3),\n" +
                "    WATERMARK FOR transaction_time AS transaction_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "    'connector' = 'kafka',\n" +
                "    'topic'     = 'transactions',\n" +
                "    'properties.bootstrap.servers' = 'kafka:19092',\n" +
                "    'scan.startup.mode' = 'earliest-offset',\n" +
                "    'format'    = 'csv'\n" +
                ")");

        tEnv.executeSql("CREATE TABLE spend_report (\n" +
                "    account_id BIGINT,\n" +
                "    amount     BIGINT\n," +
                ") WITH (\n" +
                "  'connector'  = 'kafka',\n" +
                "   'topic'     = 'spend-report',\n" +
                "   'properties.bootstrap.servers' = 'kafka:19092',\n" +
                "   'scan.startup.mode' = 'earliest-offset',\n" +
                "   'format'    = 'csv'\n" +
                ")");

        Table transactions = tEnv.from("transactions");
        transactions.executeInsert("spend_report");
    }
}
