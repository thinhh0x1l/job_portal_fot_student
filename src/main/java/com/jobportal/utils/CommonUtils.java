package com.jobportal.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

public class CommonUtils {

    @Getter
    private static String uriv;

    public static String getSiteURL(HttpServletRequest req) {
        String siteURL = req.getRequestURL().toString(); ///http://localhost:8080/account/register
        String s = siteURL.replace(req.getServletPath(), "");
        ///  System.out.println(req.getServletPath());   /account/register
        ///   System.out.println(s);        http://localhost:8080

        return s;
    }
    public static String getServletPath(HttpServletRequest req){
        return req.getServletPath();
    }

    public static void setUriv(String uriv) {
        CommonUtils.uriv = uriv;
    }

    public static boolean checkUriv(String uriv){
        if(uriv == null || uriv.equals(""))
            return false;
        return CommonUtils.uriv.equals(uriv);
    }

}
