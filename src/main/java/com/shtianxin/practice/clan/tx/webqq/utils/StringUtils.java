package com.shtianxin.practice.clan.tx.webqq.utils;

/**
 * StringUtils
 * create at 2017/2/8
 * @author chenclannad@gmail.com
 */
public class StringUtils {

    public static final String EMPTY = "";

    private StringUtils(){}

    public static boolean isBlank(String str){
        return str == null || str.equals(EMPTY) || str.trim().equals(EMPTY);
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

}
