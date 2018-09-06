package com.yq.controller;

import com.yq.domain.Log;
import com.yq.domain.Movie;
import com.yq.service.LogService;
import com.yq.service.MovieService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;


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

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("aaa-2018.09.06")
                .withQuery(QueryBuilders.matchPhraseQuery("level", level))
                .withPageable(new PageRequest(0, 10))
                .build();

        Page<Log> sampleEntities =
                elasticsearchTemplate.queryForPage(searchQuery,Log.class);

        return sampleEntities;
    }

    @ApiOperation(value = "query by native", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "all", value = "all", defaultValue = "INFO", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/logByAllNative/{all}")
    public Page<Log> findLogByAllNative(@PathVariable("all") String all) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("aaa-2018.09.06")
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