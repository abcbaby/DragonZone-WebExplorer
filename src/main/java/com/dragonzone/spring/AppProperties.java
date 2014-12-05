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
    private boolean searchMp3Info;

    @Autowired
    public void setDefaultPathList(@Value("${app.default.paths}") final String strPaths) {
        this.defaultPathList = Arrays.asList(strPaths.split(PATH_SEPARATOR));
    }

    @Autowired
    public void setSearchMp3Info(@Value("${search.mp3.meta}") final String searchMp3Info) {
        this.searchMp3Info = Boolean.valueOf(searchMp3Info);
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

    /**
     * @return the searchMp3Info
     */
    public boolean isSearchMp3Info() {
        return searchMp3Info;
    }
}
