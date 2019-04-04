package com.itheima.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static void main(String[] args) {
        encode("hanyu199310001");
    }

    /**
     * 加密
     *
     * @param str
     */
    private static void encode(String str) {
        try {
            //指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //将需要加密的字符串转换成byte类型的数组,然后进行随机哈希过程
            byte[] bs = digest.digest(str.getBytes());
            //循环遍历bs,让其生成32位字符串(固定写法)
            StringBuffer buffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                buffer.append(hexString);
            }

            System.out.println(buffer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
