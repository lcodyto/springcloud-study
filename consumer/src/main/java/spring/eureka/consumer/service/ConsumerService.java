package spring.eureka.consumer.service;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import spring.eureka.consumer.feign.DcClient;
import spring.eureka.consumer.feign.UploadService;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author wpy
 * @create 2018/4/13 9:40
 * @project_name eureka-client
 */

@RestController
public class ConsumerService {

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer")
    public String dc() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/dc";
        System.out.println(url);
        return restTemplate.getForObject(url, String.class);
    }


    @Resource
    private RestTemplate restTemplateRibbon;

    @GetMapping("/consumer-ribbon")
    public String dcRibbon() {
        return restTemplateRibbon.getForObject("http://eureka-client/dc", String.class);
    }


    @Resource
    private DcClient dcClient;

    @GetMapping("/consumer-feign")
    public String dcFeign() {
        return dcClient.consumer();
    }


    @Resource
    private UploadService uploadService;

    @GetMapping("/uploadFile")
    public String uploadFile(){
        ClassPathResource resource = new ClassPathResource("upload.txt");
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, resource.getFilename());

        try (InputStream input = new FileInputStream(resource.getFile()); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multi = new CommonsMultipartFile(fileItem);
        return uploadService.handleFileUpload(multi);
    }

    @Resource
    private HystrixService hystrixService;

    @GetMapping("/consumer-hystrix")
    public String dcHystrix(){
        return hystrixService.hystrix();
    }



}
