package com.yq.client;


import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "user-service", fallbackFactory = UserServiceFallbackFactory.class)
public interface UserServiceClient {


    @RequestMapping(value="/v1/users/{userId}", method= RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable(value = "userId") String userId);

    @RequestMapping(value="/v1/users/queryById", method= RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUserByQueryParam(@RequestParam("userId") String userId);

    @RequestMapping(value="/v1/users", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String createUser();
}

@Component
@Slf4j
class UserServiceFallbackFactory implements FallbackFactory<UserServiceClient> {
    @Override
    public UserServiceClient create(final Throwable throwable) {
        return new UserServiceClient() {
            @Override
            public String getUser(String userId) {
                log.warn("Fallback reason={}, userId={}", throwable.getMessage(), userId);
                return "SvcCall Error1";
            }

            @Override
            public String getUserByQueryParam(String userId) {
                log.warn("Fallback reason={}, userId={}", throwable.getMessage(), userId);
                return "SvcCall Error2";
            }

            @Override
            public String createUser() {
                log.warn("Fallback reason={}", throwable.getMessage());
                return "SvcCall Error3";
            }
        };
    }

}

