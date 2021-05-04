package spring.eureka.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.eureka.consumer.feign.DcClient;

import javax.annotation.Resource;

@Service
public class HystriService {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DcClient dcClient;

    @HystrixCommand(fallbackMethod = "fallback")
    public String consumer() {
        return dcClient.consumer();
    }

    public String fallback() {
        return "fallback";
    }

}