package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
/**
 * Simple to Introduction
 * className: Config
 *
 * @author EricYang
 * @version 2018/8/4 11:06
 */


@Configuration
@EnableRedisHttpSession
public class Config {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
}


