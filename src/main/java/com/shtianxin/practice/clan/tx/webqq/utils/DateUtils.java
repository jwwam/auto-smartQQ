package com.shtianxin.practice.clan.tx.webqq.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils
 * create at 2017/2/8
 * @author chenclannad@gmail.com
 */
public class DateUtils {

    private DateUtils(){}

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");

    /**
     * 转为标准时间格式字符串
     * @param timeMillis 时间
     * @return 时间格式字符串
     */
    public static String convert(long timeMillis){
        Date date = new Date();
        date.setTime(timeMillis);
        return dateFormat.format(date);
    }

}
