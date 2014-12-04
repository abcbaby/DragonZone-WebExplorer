package com.dragonzone.jsf.util;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@ManagedBean
@SessionScoped
public class SecurityBean implements Serializable {

    private static final long serialVersionUID = -4879375427600273540L;

    public boolean hasRole(String role) {
        // get security context from thread local
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }

        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (role.equals(auth.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public boolean hasNoRole(String role) {
        return !hasRole(role);
    }

    public boolean hasAnyRoles(String roles) {
        boolean allowed = false;
        String[] allowedRoles = StringUtils.splitByWholeSeparator(roles, ",");

        for (String role : allowedRoles) {
            if (hasRole(role.trim())) {
                allowed = true;
                break;
            }
        }

        return allowed;
    }

    public boolean hasNoRoles(String roles) {
        return !hasAnyRoles(roles);
    }

    public String getLoginUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername(); //get logged in username
    }

    public void logout() throws IOException {
        getSession().invalidate();
    }
    
    public HttpSession getSession() {
        return (HttpSession) getFacesContext().getExternalContext().getSession(true);
    }

    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

}
