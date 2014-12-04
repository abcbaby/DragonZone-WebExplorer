package com.dragonzone.spring;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "appProperties")
public class AppProperties {
    private final static String PATH_SEPARATOR = ";"; 
    private List<String> defaultPathList;

    @Autowired
    public void setDefaultPathList(@Value("${app.default.paths}") final String strPaths) {
        this.defaultPathList = Arrays.asList(strPaths.split(PATH_SEPARATOR));
    }

    /**
     * @return the defaultPathList
     */
    public List<String> getDefaultPathList() {
        return defaultPathList;
    }

    /**
     * @param defaultPathList the defaultPathList to set
     */
    public void setDefaultPathList(List<String> defaultPathList) {
        this.defaultPathList = defaultPathList;
    }
}
