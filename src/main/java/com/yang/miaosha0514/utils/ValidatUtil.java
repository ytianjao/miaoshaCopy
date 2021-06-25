package com.yang.miaosha0514.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }

        Matcher m = mobile_pattern.matcher(mobile);
        return m.matches();
    }
}
