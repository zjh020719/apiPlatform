package com.xxfs.fsapiclientsdk;


import com.xxfs.fsapiclientsdk.client.FsApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("fsapi.client")
@Data
@ComponentScan
public class FsApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public FsApiClient yuApiClient() {
        return new FsApiClient(accessKey, secretKey);
    }

}
