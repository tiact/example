package com.tiact.websocket.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

@Component
public class util {

    private static String user = "";

    /**
     * 判断是否Windows平台
     * @return boolean
     */
    public static boolean chkPlatform(){
        Properties props = System.getProperties();
        String osName = props.getProperty("os.name");
        if(osName.contains("Windows")){
            return true;
        }
        return false;
    }

    public static long getFileSize(File file) {
        return file.length();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void flushUser() {
        util.user = "";
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        util.user = user;
    }
}
