package com.dragonzone.jsf;

import java.lang.reflect.Field;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.commons.lang3.StringUtils;

@ManagedBean
@ApplicationScoped
public class ApplicationConstants {
    public static final int DISPLAY_FILENAME_LIMIT = 35;
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss a";
    
    public String getVariableValue(String variableName) throws Exception {
        ApplicationConstants thisClass = new ApplicationConstants();
        Field thisField = thisClass.getClass().getField(variableName);
        return String.valueOf(thisField.get(thisClass));
    }

    public String abbreviateFileName(String fileName) {
        return StringUtils.abbreviateMiddle(fileName, "...", DISPLAY_FILENAME_LIMIT);
    }
    
    public boolean isStringEmpty(String value) {
        return StringUtils.isEmpty(value);
    }

    public String getDisplayLength(long lengthInMilliseconds) {
        int seconds = (int) (lengthInMilliseconds / 1000) % 60;

        long minutes = ((lengthInMilliseconds - seconds) / 1000) / 60;

        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
