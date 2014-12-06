package com.dragonzone.jsf.util;

import com.dragonzone.jsf.BaseBean;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MySessionBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 6659100794381030210L;
    
    private boolean displayedBanner;

    /**
     * @return the displayedBanner
     */
    public boolean isDisplayedBanner() {
        return displayedBanner;
    }

    /**
     * @param displayedBanner the displayedBanner to set
     */
    public void setDisplayedBanner(boolean displayedBanner) {
        this.displayedBanner = displayedBanner;
    }

}
