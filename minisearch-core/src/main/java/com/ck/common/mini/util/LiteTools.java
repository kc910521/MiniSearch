package com.ck.common.mini.util;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.CharType;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author caikun
 * @Description
 * 工具合集
 *
 * @Date 下午2:18 20-4-28
 **/
public final class LiteTools {


    public final static boolean match(String pattern, String originStr) {
        return Pattern.matches(pattern, originStr);
    }

    /**
     * 比对conditions到data
     *
     * @param conditions
     * @param data
     * @param <Obj>
     * @return
     */
    public final static <Obj> boolean objectConditionMatch(Obj conditions, Obj data) {
        if (conditions == null) {
            return true;
        }
        if (conditions instanceof String && data instanceof String) {
            return true;
        }
        Field[] conditionFields = listAllFields(conditions);
        for (int i = 0; i < conditionFields.length; i++) {
            Field cField = conditionFields[i];
            cField.setAccessible(true);
            try {
                Object condVal = cField.get(conditions);
                if (condVal != null && !condVal.equals(cField.get(data))) {
                    return false;
                }

            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 列出obj所有字段
     *
     * @param obj 目标
     * @return
     */
    private final static Field[] listAllFields(Object obj) {
        List<Field> fields = new ArrayList<>();
        Class tempClass = obj.getClass();
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 字符串入Q
     *
     * @param keywords
     * @return
     */
    public final static Queue beQueue(String keywords) {
        char[] chars = keywords.toCharArray();
        Queue<Character> cq = new LinkedList<>();
        for (int i = 0; i < chars.length; i++) {
            cq.offer(chars[i]);
        }
        return cq;
    }

    /**
     * 拼音处理
     *
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder output = new StringBuilder("");
        if (inputString != null && inputString.length() > 0
                && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {
                    if (isBigChar(input[i])) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        if (temp != null && temp.length > 0) {
                            output.append(temp[0]);
                        }
                    } else {
                        output.append(Character.toString(input[i]));
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                throw new RuntimeException(e);
            }
        } else {
            return "*";
        }
        return output.toString();
    }

    /**
     * 是否为大型字，暂时判断中文
     *
     * @param c
     * @return
     */
    private static boolean isBigChar(char c) {
        return Character.toString(c).matches(
                "[\\u4E00-\\u9FA5]+");
    }

    /**
     * 返回待组合的串
     *
     * @param originWords
     * @return
     */
    private static ArrayList<String> splitOriginWords(String originWords) {
        ArrayList<String> res = new ArrayList<>();
        // 策略，0-空格，1,数字，2字母，3其他字符，4-bigchar
        CharType ls = CharType.BLANK;
        CharType ts = CharType.BLANK;
        StringBuilder sbCache = new StringBuilder();
        for (char c : originWords.toCharArray()) {
            if (isBigChar(c)) {
                ts = CharType.BIG_CHAR;
            } else if (Character.isSpaceChar(c)) {
                ts = CharType.BLANK;
            } else if (Character.isDigit(c)) {
                ts = CharType.DIGIT;
            } else if (Character.isAlphabetic(c)) {
                ts = CharType.ALP;
            } else {
                ts = CharType.OTHER;
            }
            if (collectCache(ls, ts) && sbCache.length() > 0) {
                res.add(sbCache.toString());
                sbCache.delete(0, sbCache.length());
            }
            if (!Character.isSpaceChar(c)) {
                sbCache.append(c);
            }
            ls = ts;
        }
        if (sbCache.length() > 0) {
            res.add(sbCache.toString());
        }
        return res;
    }

    /**
     * 不需要分割的形式：
     * 1.字母+字母
     * 2.数字+数字
     * 3.字母+数字
     * 4.数字+字母
     * 5.数字+特殊
     * 6.字母+特殊
     * 7.特殊+数字
     * 8.特殊+字母
     *
     * @param ls
     * @param ts
     * @return True: 状态互斥，需要分割并返回cache
     * False：非互斥，继续叠加cache
     */
    private static boolean collectCache(CharType ls, CharType ts) {
        if (ts == CharType.BLANK) {
            // 4optimize
            return true;
        }
        if (ls == CharType.ALP && ts == CharType.ALP) {
            return false;
        }
        if (ls == CharType.DIGIT && ts == CharType.DIGIT) {
            return false;
        }
        if (ls == CharType.DIGIT && ts == CharType.ALP) {
            return false;
        }
        if (ls == CharType.DIGIT && ts == CharType.OTHER) {
            return false;
        }
        if (ls == CharType.ALP && ts == CharType.OTHER) {
            return false;
        }
        if (ls == CharType.OTHER && ts == CharType.DIGIT) {
            return false;
        }
        if (ls == CharType.OTHER && ts == CharType.ALP) {
            return false;
        }
        return true;
    }

    /**
     * 将originKeyword根据pattern进行按类型分段拆分，并返回顺序的组合
     *
     * @param originKeyword
     * @return 除原始串以外的组合case
     */
    public static ArrayList<String> combinationKeywordsChar(String originKeyword) {
        final ArrayList<String> result = new ArrayList<>();
        final ArrayList<String> cookedOutputs = new ArrayList<>();
        ArrayList<String> cks = splitOriginWords(originKeyword);
        combination(cks, cookedOutputs, new IFunWorker() {
            @Override
            public Object doWork(Object obj) {
                List<String> r1 = (List<String>) obj;
                result.add(listToString3(r1, null));
                return null;
            }
        });
        if (originKeyword.length() <= MiniSearchConfigure.getPhraseCharNum()) {
            result.remove(originKeyword);
        }
        return result;
    }

    /**
     * 输出origins的组合
     *
     * @param origins
     * @param output
     * @param funWorker
     */
    public static void combination(List<String> origins, ArrayList<String> output, IFunWorker funWorker) {
        if (origins.size() <= 0) {
            // output
            funWorker.doWork(output);
            return;
        }
        if (output.size() >= MiniSearchConfigure.getPhraseCharNum()) {
            //
            funWorker.doWork(output);
            return;
        }

        for (int i = 0; i < origins.size(); i++) {
            ArrayList<String> ops = (ArrayList<String>) output.clone();
            ops.add(origins.get(i));
            List<String> or1 = origins.subList(i + 1, origins.size());
            combination(or1, ops, funWorker);
        }
    }

    /**
     * list转字符串
     *
     * @param list
     * @param separator
     * @return
     */
    private static String listToString3(List list, Character separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (separator != null && i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 返回排序去重过的char数组
     * 压缩之意义
     */
    public static char[] toUnDupSortedChars(String strs) {
        if (strs == null || strs.trim().length() == 0) {
            return null;
        }
        char[] chars = strs.trim().toCharArray();
        return defUnDupSort(chars);
    }

    public static char[] defUnDupSort(char[] chars) {
        Arrays.sort(chars);
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < chars.length; idx ++) {
            if (idx < chars.length - 1 && chars[idx] == chars[idx + 1]) {
                continue;
            } else {
                stringBuilder.append(chars[idx]);
            }
        }
        return stringBuilder.toString().toCharArray();
    }

    static class A {
        private int a;

        private String mk;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getMk() {
            return mk;
        }

        public void setMk(String mk) {
            this.mk = mk;
        }
    }

    public static void main(String[] args) {


//        System.out.println(getPingYin("超美少女系列 蛯原舞"));
        A aa = new A();
        aa.setMk("da");
        aa.setA(3);
        A bb = new A();
        bb.setMk("da");
        System.out.println(objectConditionMatch(aa, bb));
    }


    private LiteTools() {}
}
