package com.yq.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Simple to Introduction
 * className: Log
 *
 * @author EricYang
 * @version 2018/9/4 12:27
 */



/*
{

    "_index": "xyz-test-2018.09.04",
    "_type": "logs",
    "_id": "AWWiAtkX2zctpjWsu2sQ",
    "_version": 1,
    "_score": 1,
    "_source": {
        "@timestamp": "2015-09-04T00:37:00.256Z",
        "HOSTNAME": "aaaa-center-65",
        "level": "INFO",
        "port": 49744,
        "thread_name": "http-nio-6016-exec-3",
        "level_value": 20000,
        "@version": 1,
        "host": "150.276.13.165",
        "logger_name": "org.x.y.z.ConnectorController",
        "message": "Exit connectorAllCount, count=7",
        "tags": [
            "log4j-test"
        ]
    }

}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "iot-test-2018.09.04", type = "logs", shards = 1, replicas = 0)
public class Log {

    @Id
    private String id;

    private String HOSTNAME;
    private String level;
    private int port;
    private String thread_name;
    private int level_value;
    private String host;
    private String logger_name;
    private String message;

    private String timestamp;


}