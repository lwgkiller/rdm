package com.redxun.core.util;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.codec.binary.Base64;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringUtil {

    private static String SPECIAL_REG_EX = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";

    private static String EMAIL_REG_EX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    public static String getArrayString(String[] arr, String split) {
        StringBuffer sb = new StringBuffer();
        for (String a : arr) {
            sb.append(a).append(split);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 获得父路径
     * 
     * @param fullPath
     *            如 0.1.2.3.
     * @return
     */
    public static String getParentPath(String fullPath) {
        String subString = fullPath.substring(0, fullPath.length() - 1);
        int index = subString.lastIndexOf(".");
        if (index != -1) {
            String newStr = subString.substring(0, index + 1);
            return newStr;
        }
        return subString;
    }

    /**
     * 获得数组字符串中的字符集
     * 
     * @param path
     *            如 1.2.3.4.则返回 '1','2','3','4'
     * @return
     */
    public static String getArrCharString(String path) {
        StringBuffer sb = new StringBuffer();
        String[] arr = path.split("[.]");
        for (int i = 0; i < arr.length; i++) {
            if ("0".equals(arr[i]) || "".equals(arr)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("'").append(arr[i]).append("'");
        }
        return sb.toString();
    }

    // public static void main(String[]args) throws UnsupportedEncodingException{
    //
    // String decodeStr =
    // Base64Util.decodeUtf8("PGRpdiBjbGFzcz0ic2hhZG93Qm94Ij4KIDx0YWJsZSBjZWxsc3BhY2luZz0iMSIgY2VsbHBhZGRpbmc9IjAiIGNsYXNzPSJmb3JtLWRldGFpbCBjb2x1bW4tdHdvIHRyYW5zcGFyZW50Ij4KICA8Y2FwdGlvbj4KICAg5qCH6aKYCiAgPC9jYXB0aW9uPgogIDx0Ym9keT4KICAgPHRyIGNsYXNzPSJmaXJzdFJvdyI+CiAgICA8dGggc3R5bGU9IndoaXRlLXNwYWNlOiBub3dyYXA7Ij7kuK3mloc8L3RoPgogICAgPHRkPjxpbnB1dCBuYW1lPSJ6dyIgY2xhc3M9Im1pbmktdGV4dGJveCByeGMiIHBsdWdpbnM9Im1pbmktdGV4dGJveCIgdnR5cGU9Imxlbmd0aDo1MCIgbGFiZWw9IuS4reaWhyIgb252YWxpZGF0aW9uPSIiIGRhdGF0eXBlPSJ2YXJjaGFyIiBsZW5ndGg9IjUwIiBkZWNpbWFsPSIwIiBtaW5udW09IiIgbWF4bnVtPSIiIHZhbGlkcnVsZT0iIiByZXF1aXJlZD0iZmFsc2UiIGVtcHR5dGV4dD0iIiBhbGxvd2lucHV0PSJ0cnVlIiB2YWx1ZT0iIiBmb3JtYXQ9IiIgZnJvbT0iZm9ybWlucHV0IiBzZXF1ZW5jZT0iIiBzY3JpcHRzPSIiIG13aWR0aD0iMCIgd3VuaXQ9InB4IiBtaGVpZ2h0PSIwIiBodW5pdD0iJSIgc3R5bGU9IiI+PC90ZD4KICAgPC90cj4KICA8L3Rib2R5PgogPC90YWJsZT4KPC9kaXY+CjxwPjxicj48L3A+");
    // System.out.print(decodeStr);
    // }

    public static String getCollectionString(Collection<String> cols, String splitChart) {
        StringBuffer sb = new StringBuffer();
        for (String a : cols) {
            sb.append(a).append(splitChart);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 判断某个字符串是否为数字
     * 
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        String chars = "01234567890.";
        for (int i = 0; i < str.length(); i++) {
            String c = str.substring(i, i + 1);
            if (chars.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        boolean rtn = isNumeric("88.03");
        System.out.println(rtn);
    }

    /**
     * 获取指定字符串出现的次数
     * 
     * @param srcText
     *            源字符串
     * @param findText
     *            要查找的字符串
     * @return
     */
    public static int getAppearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * 将字符串首字符小写。
     * 
     * @param newStr
     * @return String
     */
    public static String makeFirstLetterLowerCase(String newStr) {
        return toFirst(newStr, false);
    }

    /**
     * 将字符串首字符大写。
     * 
     * @param newStr
     * @return String
     */
    public static String makeFirstLetterUpperCase(String newStr) {
        return toFirst(newStr, true);
    }

    /**
     * 把带有分隔,符的String转成Set类型的集合
     * 
     * @param includeSplitStr
     *            带有，的字符串，格式如abc,efg
     * @return
     */
    public static Set<String> toSet(String includeSplitStr) {
        return toSet(includeSplitStr, ",");
    }

    /**
     * 把带有指定分隔符的String转成Set类型的集合
     * 
     * @param includeSplitStr
     *            带有，的字符串，格式如abc#efg或abc,efg
     * @param splitChars
     *            如,或#
     * @return
     */
    public static Set<String> toSet(String includeSplitStr, String splitChars) {
        Set<String> sets = new HashSet<String>();
        String[] tmps = includeSplitStr.split(splitChars);
        for (String tmp : tmps) {
            sets.add(tmp);
        }
        return sets;
    }

    /**
     * 转化为List
     * 
     * @param includeSplitStr
     * @return
     */
    public static List<String> toList(String includeSplitStr) {
        return toList(includeSplitStr, ",");
    }

    /**
     * 把带有指定分隔符的String转成Set类型的集合
     * 
     * @param includeSplitStr
     *            带有，的字符串，格式如abc#efg或abc,efg
     * @param splitChars
     *            如,或#
     * @return
     */
    public static List<String> toList(String includeSplitStr, String splitChars) {
        List<String> list = new ArrayList<String>();
        if (includeSplitStr == null) {
            return list;
        }
        String[] tmps = includeSplitStr.split(splitChars);
        for (String tmp : tmps) {
            list.add(tmp);
        }
        return list;
    }

    /**
     * 将汉字转换为全拼
     * 
     * @param src
     * @return
     */
    public static String getPingYin(String src) {

        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();

        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else
                    t4 += Character.toString(t1[i]);
            }
            // //System.out.println(t4);
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }

    /**
     * 返回中文的首字母
     * 
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {

        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    // 将字符串转移为ASCII码
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 使用Base64加密算法加密字符串
     * 
     * @param plainText
     * @return
     */
    public static String encodeStr(String plainText) {
        byte[] b = plainText.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        String s = new String(b);
        return s;
    }

    /**
     * 使用Base64加密
     * 
     * @param encodeStr
     * @return
     */
    public static String decodeStr(String encodeStr) {
        byte[] b = encodeStr.getBytes();
        Base64 base64 = new Base64();
        b = base64.decode(b);
        String s = new String(b);
        return s;
    }

    /**
     * 将人民币金额数字转成中文大写。
     * 
     * @param amount
     * @return
     */
    public static String convertToChineseNumeral(double amount) {
        char[] hunit = {'拾', '佰', '仟'}; // 段内位置表示
        char[] vunit = {'万', '亿'}; // 段名表示
        char[] digit = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'}; // 数字表示
        long midVal = (long)(amount * 100); // 转化成整形
        String valStr = String.valueOf(midVal); // 转化成字符串

        String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
        String rail = valStr.substring(valStr.length() - 2); // 取小数部分

        String prefix = ""; // 整数部分转化的结果
        String suffix = ""; // 小数部分转化的结果
        // 处理小数点后面的数
        if (rail.equals("00")) { // 如果小数部分为0
            suffix = "整";
        } else {
            suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
        }
        // 处理小数点前面的数
        char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
        char zero = '0'; // 标志'0'表示出现过0
        byte zeroSerNum = 0; // 连续出现0的次数
        for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
            int idx = (chDig.length - i - 1) % 4; // 取段内位置
            int vidx = (chDig.length - i - 1) / 4; // 取段位置
            if (chDig[i] == '0') { // 如果当前字符是0
                zeroSerNum++; // 连续0次数递增
                if (zero == '0') { // 标志
                    zero = digit[0];
                } else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
                    prefix += vunit[vidx - 1];
                    zero = '0';
                }
                continue;
            }
            zeroSerNum = 0; // 连续0次数清零
            if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
                prefix += zero;
                zero = '0';
            }
            prefix += digit[chDig[i] - '0']; // 转化该数字表示
            if (idx > 0) {
                prefix += hunit[idx - 1];
            }
            if (idx == 0 && vidx > 0) {
                prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
            }
        }

        if (prefix.length() > 0) {
            prefix += '圆'; // 如果整数部分存在,则有圆的字样
        }
        return prefix + suffix; // 返回正确表示
    }

    /**
     * 格式化带参数的字符串，如 /param/detail.ht?a=${0}&b=${1} 注意字符串的参数从0下标开始，字符串的参数数量和args数组的数量要一致。
     * 
     * @param message
     * @param args
     * @return
     */
    public static String format(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("${" + i + "}", args[i].toString());
        }
        return message;
    }

    /**
     * 格式化如下字符串 http://www.bac.com?a=${a}&b=${b}
     * 
     * @param message
     * @param params
     */
    public static String format(String message, Map<String, Object> params) {
        String result = message;
        if (params == null || params.isEmpty())
            return result;
        Iterator<String> keyIts = params.keySet().iterator();
        while (keyIts.hasNext()) {
            String key = keyIts.next();
            Object value = params.get(key);
            if (value != null) {
                result = result.replace("${" + key + "}", value.toString());
            }
        }
        return result;
    }

    /**
     * 判断指定的内容是否存在
     * 
     * @param content
     *            内容
     * @param begin
     *            开始内容
     * @param end
     *            结束内容
     * @return
     */
    public static boolean isExist(String content, String beginStr, String endStr) {
        final boolean isExist = true;
        // 转成小写
        String lowContent = content.toLowerCase();
        String lowBeginStr = beginStr.toLowerCase();
        String lowEndStr = endStr.toLowerCase();

        int beginIndex = lowContent.indexOf(lowBeginStr);
        int endIndex = lowContent.indexOf(lowEndStr);
        if (beginIndex != -1 && endIndex != -1 && beginIndex < endIndex) {
            return isExist;
        }
        return !isExist;
    }

    /**
     * 对字符串去掉前面的指定字符
     * 
     * @param content
     *            待处理的字符串
     * @param prefix
     *            要去掉前面的指定字符串
     * @return
     */
    public static String trimPrefix(String content, String prefix) {
        if (StringUtil.isEmpty(prefix)){
            return content;
        }
        String resultStr = content;
        while (resultStr.startsWith(prefix)) {
            resultStr = resultStr.substring(prefix.length());
        }
        return resultStr;
    }

    /**
     * 对字符串去掉前面的指定字符
     * 
     * @param content
     *            待处理的字符串
     * @param suffix
     *            要去掉后面的指定字符串
     * @return
     */
    public static String trimSuffix(String content, String suffix) {
        if (StringUtil.isEmpty(suffix)){
            return content;
        }
        String resultStr = content;
        while (resultStr.endsWith(suffix)) {
            resultStr = resultStr.substring(0, resultStr.length() - suffix.length());
        }
        return resultStr;
    }

    /**
     * 删除结尾的字符串。
     * 
     * @param content
     * @param suffix
     * @return
     */
    public static String trimSuffixOnce(String content, String suffix) {
        if (StringUtil.isEmpty(suffix)){
            return content;
        }
        if (!content.endsWith(suffix)){
            return content;
        }
        return content.substring(0, content.length() - suffix.length());
    }

    /**
     * 对字符串的前后均去掉前面的指定字符
     * 
     * @param content
     * @param trimStr
     * @return
     */
    public static String trim(String content, String trimStr) {
        return trimSuffix(trimPrefix(content, trimStr), trimStr);
    }

    /**
     * 判断字符串非空
     *
     * @param object
     * @return
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(nullToString(object));
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null){
            return true;
        }

        if (str.trim().equals("")){
            return true;
        }
        return false;
    }

    /**
     * 为空判断,0做空处理。
     * 
     * <pre>
     *这里判断：
     *1.字符串为NULL
     *2.字符串为空串。
     *3.字符串为0。
     * </pre>
     * 
     * @param tmp
     * @return boolean
     */
    public static boolean isZeroEmpty(String tmp) {
        boolean isEmpty = StringUtil.isEmpty(tmp);
        if (isEmpty){
            return true;
        }
        return "0".equals(tmp);
    }

    /**
     * 非空判断。
     * 
     * @param tmp
     * @return boolean
     */
    public static boolean isNotZeroEmpty(String tmp) {
        return !isZeroEmpty(tmp);
    }

    /**
     * 把字符串的第一个字母转为大写或者小写
     * 
     * @param str
     *            字符串
     * @param isUpper
     *            是否大写
     * @return
     */
    public static String toFirst(String str, boolean isUpper) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        char first = str.charAt(0);
        String firstChar = new String(new char[] {first});
        firstChar = isUpper ? firstChar.toUpperCase() : firstChar.toLowerCase();
        return firstChar + str.substring(1);
    }

    /**
     * 将content中所有{...}的替换为replace参数内容
     * 
     * @param content
     *            待替换的字符串
     * @param replace
     *            替换的字符串
     * @return 替换后的字符串，如content=abc{aa}{bb} ； replace ="ff"，结果就是abcffff
     */
    public static String replaceVariable(String content, String replace) {
        return replaceVariable(content, replace, "\\{(.*?)\\}");
    }

    /**
     * 将content中所有符合regular正则表达式的内容替换为replace参数内容
     * 
     * @param content
     *            待替换的字符串
     * @param replace
     *            替换的字符串
     * @param regular
     *            正则表达式
     * @return 替换后的字符串。 如content=abc{aa}{bb} ； replace ="ff"，regular="\\{(.*?)\\}"；结果就是abcffff
     */
    public static String replaceVariable(String content, String replace, String regular) {
        Pattern regex = Pattern.compile(regular);
        String result = content;
        Matcher regexMatcher = regex.matcher(result);
        while (regexMatcher.find()) {
            String toReplace = regexMatcher.group(0);
            result = result.replace(toReplace, replace);
            regexMatcher = regex.matcher(result);
        }
        return result;
    }

    /**
     * 对传入的字符串（content）进行变量值替换（map） 采用默认的正则表达式：\\{(.*?)\\}
     * 
     * @param content
     *            要处理的字符串
     * @param map
     *            替换参数和值的集合
     * @return 替换后的字符串
     * @throws Exception
     */
    public static String replaceVariableMap(String content, Map<String, Object> map) throws Exception {
        return replaceVariableMap(content, map, "\\{(.*?)\\}");
    }

    /**
     * 
     * @param template
     *            要处理的字符串
     * @param map
     *            替换参数和值的集合
     * @param regular
     *            正则表达式
     * @return 替换后的字符串
     * @throws Exception
     *             如果template的某个
     */
    public static String replaceVariableMap(String template, Map<String, Object> map, String regular) throws Exception {
        Pattern regex = Pattern.compile(regular);
        Matcher regexMatcher = regex.matcher(template);
        while (regexMatcher.find()) {
            String key = regexMatcher.group(1);
            String toReplace = regexMatcher.group(0);
            String value = (String)map.get(key);
            if (value != null) {
                template = template.replace(toReplace, value);
            } else {
                // 取请求中的参数进行替换
                Map<String, Object> requestParams = (Map<String, Object>)map.get("params");
                if (requestParams != null && requestParams.get(key) != null) {
                    value = requestParams.get(key).toString();
                }
                template = template.replace(toReplace, value == null ? "" : value);
            }
        }

        return template;
    }

    /**
     * 根据默认的特殊字符正则表达式去除特殊字符
     * 
     * @param str
     * @return
     */
    public static String removeSpecial(String str) throws PatternSyntaxException {
        return removeByRegEx(str, SPECIAL_REG_EX);
    }

    /**
     * 根据传入的字符串（参数str），通过正则表达式（参数regEx），去掉该表达式匹配的字符。
     * 
     * @param str
     *            待处理的字符串
     * @param regEx
     *            正则表达式
     * @return
     * @throws PatternSyntaxException
     */
    public static String removeByRegEx(String str, String regEx) throws PatternSyntaxException {
        // 清除掉所有特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * String转Byte数组
     * 
     * @param temp
     * @return
     */
    public static byte[] stringToBytes(String str) {
        byte digest[] = new byte[str.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = str.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte)byteValue;
        }

        return digest;
    }

    /**
     * Byte数组转String
     * 
     * @param b
     * @return
     */
    public static String bytesToString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2) {
                plainText = "0" + plainText;
            }
            hexString.append(plainText);
        }
        return hexString.toString();
    }

    /**
     * 字符串 编码转换
     * 
     * @param str
     *            字符串
     * @param from
     *            原來的編碼
     * @param to
     *            轉換后的編碼
     * @return
     */
    public static String encodingString(String str, String from, String to) {
        String result = str;
        try {
            result = new String(str.getBytes(from), to);
        } catch (Exception e) {
            result = str;
        }
        return result;
    }

    /**
     * 将数据库字段名转为DataGrid字段名,如 sys_data_ 转为sysData symbol:间隔符号 isIgnoreFirst：是否忽略第一个单词的首字母转大写
     * 
     * @return
     */
    public static String convertDbFieldToField(String dbField, String symbol, boolean isIgnoreFirst) {
        String result = "";
        if (dbField.startsWith(symbol)) {
            dbField = dbField.substring(1);
        }
        if (dbField.endsWith(symbol)) {
            dbField = dbField.substring(0, dbField.length() - 1);
        }
        String[] arr = dbField.split(symbol);
        for (int i = 0; i < arr.length; i++) {
            String str = arr[i];
            if (isIgnoreFirst && i != 0) {
                char oldChar = str.charAt(0);
                char newChar = (oldChar + "").toUpperCase().charAt(0);
                str = newChar + str.substring(1);
            }
            result += str;
        }
        return result;
    }

    /**
     * 数组合并。
     * 
     * @param vals
     * @param separator
     * @return
     */
    public static String join(String[] vals, String separator) {
        if (BeanUtil.isEmpty(vals))
            return "";
        String val = "";
        for (int i = 0; i < vals.length; i++) {
            if (i == 0) {
                val += vals[i];
            } else {
                val += separator + vals[i];
            }
        }
        return val;
    }

    public static String join(List<String> list, String joinSperator) {
        String rtnString = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                rtnString = list.get(i);
            } else {
                rtnString += joinSperator + list.get(i);
            }
        }
        return rtnString;
    }

    /**
     * 字符串地址编码。
     * 
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String urlEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "utf-8");
    }

    /**
     * 获取随机数
     * 
     * @param length
     *            返回长度
     * @param onlyNumber
     *            是否只包含数字
     * @return
     */
    public static String getRandomString(int length, boolean onlyNumber) {
        String base = "0123456789";
        if (!onlyNumber) {
            base = "abcdefghijklmnopqrstuvwxyz0123456789";
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 将标签内的密文翻译
     * 
     * @param encodeStr
     *            编码的密文 jquery.base64 前端 encodeStr="tagName"+$.base64.btoa(encodeStr,true,'utf8')+"</tagName>"
     * @param tag
     *            标签名 <div>密文<div>中的"div"
     * @return
     */
    public static String decodeBase64(String encodeStr) {
        Pattern regex = Pattern.compile("<div>(.*?)</div>");
        Matcher regexMatcher = regex.matcher(encodeStr);
        if (regexMatcher.find()) {
            encodeStr = regexMatcher.group(1);
        }
        String decodeStr = "";
        try {
            decodeStr = Base64Util.decodeUtf8(encodeStr.trim());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decodeStr;
    }

    /**
     * 获取mac地址。 [[0a-00-27-00-00-12, 78-24-af-c8-54-19, 12-e2-30-5e-bf-53]
     * 
     * @return
     * @throws SocketException
     */
    public static Set<String> getMacAddress() throws SocketException {
        Set<String> set = new HashSet<String>();
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            byte[] mac = ni.getHardwareAddress();

            if (mac != null && mac.length > 0) {
                String str = getLocalMac(mac);
                set.add(str);
            }
        }
        return set;
    }

    private static String getLocalMac(byte[] mac) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 验证邮件是否符合格式。
     * 
     * @param str
     * @return
     */
    public static boolean vaildEmail(String str) {
        return Pattern.matches(EMAIL_REG_EX, str);
    }

    /**
     * 判断传入的字符是否有注入的关键字。
     * 
     * @param str
     * @return
     */
    public static boolean hasSqlInject(String str, boolean needAndOr) {
        if (StringUtil.isEmpty(str))
            return false;
        String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|truncate|char|declare|;|-|+|,";
        if (!needAndOr) {
            inj_str = "'|exec|insert|select|delete|update|count|*|%|truncate|char|declare|;|-|+|,";
        }
        String[] aryStr = inj_str.split("[|]");
        str = str.toLowerCase();
        for (int i = 0; i < aryStr.length; i++) {
            if (str.indexOf(aryStr[i]) >= 0) {
                return true;
            }
        }
        return false;
    }
    /**
     * 将null转为字符串 防止null报错的问题
     */
    public static String nullToString(Object o) {
        return o == null ? "" : o.toString();
    }

}
