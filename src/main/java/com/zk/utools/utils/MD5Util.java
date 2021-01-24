package com.zk.utools.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xh on 2019/4/8.
 */
public final class MD5Util {

    private MD5Util() {
    }

    private static final int MAX_N = 256;
    private static final int MAX_D = 16;

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * MD5加密  加密之后返回32位字母大写的字符串
     *
     * @param s String
     * @return String
     * @throws NoSuchAlgorithmException </>
     */
    public static final String md5(String s) throws NoSuchAlgorithmException {


        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(s.getBytes());

        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            resultSb.append(byteToHexString(bytes[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += MAX_N;
        }
        int d1 = n / MAX_D;
        int d2 = n % MAX_D;
        return hexDigits[d1] + hexDigits[d2];
    }
}
