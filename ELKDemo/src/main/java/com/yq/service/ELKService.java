package com.yq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

/**
 * Simple to Introduction
 * className: ELKService
 *
 * @author EricYang
 * @version 2018/9/4 10:29
 */
@Service
public class ELKService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate ;


}
