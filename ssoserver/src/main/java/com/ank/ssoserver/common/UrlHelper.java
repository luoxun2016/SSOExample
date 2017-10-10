package com.ank.ssoserver.common;

public class UrlHelper {

    public static String join(String url, String query){
        if(url.indexOf("?") == -1){
            url = url + "?" + query;
        }else{
            url = url + "&" + query;
        }
        return url;
    }

    public static String getRootUrl(String url){
        int fromIndex = url.indexOf("//");
        if(fromIndex == -1) return null;

        fromIndex = fromIndex + 2;
        int endIndex = url.indexOf("/", fromIndex);
        if(endIndex != -1){
            return url.substring(0, endIndex);
        }else{
            endIndex = url.indexOf("?", fromIndex);
            if(endIndex != -1) {
                return url.substring(0, endIndex);
            }
            return url;
        }
    }

}
