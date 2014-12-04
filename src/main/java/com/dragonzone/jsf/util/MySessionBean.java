package com.dragonzone.jsf.util;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MySessionBean implements Serializable {
    private static final long serialVersionUID = 6659100794381030210L;
    
    @ManagedProperty("#{securityBean}")
    private SecurityBean securityBean;
    private boolean displayedBanner;

    /**
     * @return the securityBean
     */
    public SecurityBean getSecurityBean() {
        return securityBean;
    }

    /**
     * @param securityBean the securityBean to set
     */
    public void setSecurityBean(SecurityBean securityBean) {
        this.securityBean = securityBean;
    }

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
