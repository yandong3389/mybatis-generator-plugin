package d.util;

import org.apache.commons.lang3.StringUtils;

public class VMUtil {

    /**
     * 将字符串的首字母转换成大写
     * @param str 字符串参数
     * @return String 转换后的字符串
     */
    public static String toFirstUpperClass(String str) {
        StringBuffer buffer = new StringBuffer();
        if (StringUtil.isNotEmpty(str)) {
            buffer.append(StringUtils.capitalize(str));
        }
        return buffer.toString();
    }

    /**
     * 将字符串的首字母转换成小写
     * @param str 字符串参数
     * @return String 转换后的字符串
     */
    public static String toFirstLowerClass(String str) {
        StringBuffer buffer = new StringBuffer();
        if (StringUtil.isNotEmpty(str)) {
            buffer.append(Character.toString(str.charAt(0)).toLowerCase());
            buffer.append(str.substring(1));
        }
        return buffer.toString();
    }
}
