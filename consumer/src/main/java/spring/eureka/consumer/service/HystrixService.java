package spring.eureka.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import spring.eureka.consumer.feign.DcClient;

import javax.annotation.Resource;

/**
 * @author wpy
 * @create 2018/4/13 18:05
 * @project_name springcloud-study
 */
@Service
public class HystrixService {
    @Resource
    private DcClient dcClient;

    @HystrixCommand(fallbackMethod = "fallback")
    public String hystrix(){
        return dcClient.consumer();
    }

    public String fallback() {
        return "fallback";
    }
}
