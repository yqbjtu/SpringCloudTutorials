package com.yq.service;


import com.yq.domain.Log;
import com.yq.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Nasir on 12-09-2015.
 */
@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public List<Log> findByLevel(String level) {
        return logRepository.findByLevel(level);
    }

    public Optional<Log> findById(String id) {
        return logRepository.findById(id);
    }

    public Log addOneLog(Log log) {
        return logRepository.save(log);
    }

    public void deleteLog(String id) {
        logRepository.deleteById(id);
    }
}