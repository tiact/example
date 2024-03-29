package com.tiact.websocket.config;

import com.tiact.websocket.utils.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    /**
     * 视图控制器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/disk").setViewName("disk");
    }

    /**
     * 静态资源配置
    */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:/data/tomcat/web8080/webapps/s/";
        if(util.chkPlatform()){
            path = "file:D:/data/s/";
        }
        System.out.println("[web] "+path);
        registry.addResourceHandler("/s/**").addResourceLocations(path);
    }


}



