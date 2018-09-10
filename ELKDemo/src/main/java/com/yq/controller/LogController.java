package com.yq.controller;

import com.yq.domain.Log;
import com.yq.service.LogService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@RestController
public class LogController {
    @Autowired
    private LogService logService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @ApiOperation(value = "query", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "level", defaultValue = "INFO", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/log/{level}")
    public ResponseEntity<List<Log>> findLogByLevel(@PathVariable("level") String level) {
        List<Log> fetchedMovie = logService.findByLevel(level);
        return ResponseEntity.ok(fetchedMovie);
    }

    @ApiOperation(value = "query by native", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "level", defaultValue = "INFO", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/logByLevelNative/{level}")
    public Page<Log> findLogByLevelNative(@PathVariable("level") String level) {
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery()
//                .withIndices("aaa-2018.09.06")
//                .withTypes("logs")
//                .withPageable(new PageRequest(0,10))
//                .build();
        long nowLong = System.currentTimeMillis();
        long fiveMinsBeforeLong = nowLong - 1000 * 60 * 60 * 2;
        Date fiveMinsBefore = new Date(fiveMinsBeforeLong);

//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("title","Search engines").operator(AND))
//                .build();

//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("title", "spring date elasticsearch")
//                        .operator(AND)
//                        .fuzziness(Fuzziness.ONE)
//                        .prefixLength(3))
//                .build();
//



//        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();   ---不能工作
//        AndFilterBuilder filters = null;
//        filters = new AndFilterBuilder();
//        filters.add(<your filter>);
//        builder.withFilter(filters);
//        builder.build()

//        // Function Score Query  ---不能工作
//        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
//                .add(QueryBuilders.boolQuery().should(matchQuery("level", level)),
//                        ScoreFunctionBuilders.weightFactorFunction(1000))
//                .add(QueryBuilders.boolQuery().should(QueryBuilders.rangeQuery("@timestamp").lte("2018-09-10T04:44:31.364Z").gte("2018-09-10T04:39:31.364Z")),
//                        ScoreFunctionBuilders.weightFactorFunction(100));
//
//        // 创建搜索 DSL 查询
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withPageable(new PageRequest(0, 10))
//                .withQuery(functionScoreQueryBuilder).build();
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                //.withQuery(matchQuery("level",level).operator(AND)) --- 不行
//                .withQuery(matchQuery("level",level)) ---不行
//                //.withQuery(QueryBuilders.rangeQuery("@timestamp").lte("2018-09-10T04:44:31.364Z").gte("2018-09-10T04:39:31.364Z"))
//                .build();

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("aaa-2018.09.10")
                //.withQuery(QueryBuilders.rangeQuery("@timestamp").gte(fiveMinsBefore))
                //.withQuery(QueryBuilders.rangeQuery("@timestamp").lte(new Date()))
                //.withQuery(QueryBuilders.rangeQuery("@timestamp").gte("2018-09-10T04:39:31.364Z")) ---如果大约和小于是and关系必须在一起
                //.withQuery(QueryBuilders.rangeQuery("@timestamp").lte("2018-09-10T04:44:31.364Z"))
                .withFilter(QueryBuilders.matchPhraseQuery("level", level))
                .withQuery(QueryBuilders.rangeQuery("@timestamp").lte("2018-09-10T04:44:31.364Z").gte("2018-09-10T04:39:31.364Z"))
                .withPageable(new PageRequest(0, 10))
                .build();

        Page<Log> sampleEntities =
                elasticsearchTemplate.queryForPage(searchQuery, Log.class);

        return sampleEntities;
    }

    @ApiOperation(value = "query by native", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "all", value = "all", defaultValue = "INFO", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/logByAllNative/{all}")
    public Page<Log> findLogByAllNative(@PathVariable("all") String all) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("aaa-2018.09.10")
                .withQuery(QueryBuilders.matchPhraseQuery("_all", all))
                .withPageable(new PageRequest(0, 10))
                .build();

        Page<Log> sampleEntities =
                elasticsearchTemplate.queryForPage(searchQuery, Log.class);

        return sampleEntities;
    }

    @ApiOperation(value = "query by id", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/logById/{id}")
    public Optional<Log> findLogMfindById(@PathVariable("id") String id) {
        Optional<Log> logOptional = logService.findById(id);
        return logOptional;
    }
}