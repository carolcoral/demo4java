package site.cnkj.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/3/14 13:51
 */
public class StringUtil {

    private static final List EscapeStringList = Arrays.asList(".","|","\\","^");

    /**
     * 自动转义符合条件的字符串
     * @param str
     * @return
     */
    public static String escapeJava(String str) {
        int sz;
        sz = str.length();
        String newStr = new String();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (EscapeStringList.contains(String.valueOf(ch))){
                newStr = newStr + "\\"+String.valueOf(ch);
            }else {
                newStr = newStr + String.valueOf(ch);
            }
        }
        return newStr;
    }

    /**
     * 格式化Exception为String
     * @param e Exception
     * @return e.toString
     */
    public static String formatException(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        str = StringEscapeUtils.escapeJava(str);
        return str;
    }

}
