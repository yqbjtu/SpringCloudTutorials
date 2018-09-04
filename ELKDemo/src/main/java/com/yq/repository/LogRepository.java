package com.yq.repository;

import com.yq.domain.Director;
import com.yq.domain.Log;
import com.yq.domain.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Nasir on 12-09-2015.
 */
@Repository
public interface LogRepository extends ElasticsearchRepository<Log, String> {

    List<Log> findByLevel(String level);

}
