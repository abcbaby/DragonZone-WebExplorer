package com.dragonzone.jsf;

import javax.faces.context.FacesContext;

/**
 * This is used to inject all Spring services
 * THIS NEEDS TO BE IN SET AS @RequestScoped
 * OTHERWISE THE SERVICES WILL BE LOST (NULL) IN 
 * CLUSTERED ENVIRONMENT WHEN REPLICATING
 */
public abstract class BaseControlBean extends BaseBean {
    
    /**
     * This is called on the f:event type="preRenderComponent"
     * Make sure the below line is set on the XHTML page
     * <f:event type="preRenderComponent" listener="#{[YOUR_BEAN].preLoadPage}" />
     */
    public abstract void preLoadPage();

    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
