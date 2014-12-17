package com.dragonzone.jsf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public List<Map.Entry<Object, Object>> getSystemPropList() {
        List<Map.Entry<Object, Object>> systemPropList = new ArrayList<>();
        systemPropList.addAll(System.getProperties().entrySet());
        systemPropList.sort(new Comparator<Map.Entry<Object, Object>>() {
            @Override
            public int compare(Map.Entry<Object, Object> obj1, Map.Entry<Object, Object> obj2) {
                String obj1Key = (String) obj1.getKey();
                String obj2Key = (String) obj2.getKey();
                return obj1Key.compareTo(obj2Key);
            }
        });
        return systemPropList;
    }
}
