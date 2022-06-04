package com.github.ScipioAM.scipio_utils_crypto;

import com.github.ScipioAM.scipio_utils_crypto.listener.OnProcessingListener;
import com.github.ScipioAM.scipio_utils_crypto.mode.SaltLevel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Class: AbstractCrypto
 * Description:
 * Author: Alan Min
 * Create Time: 2018/6/1
 */
public abstract class AbstractCrypto {

    private String customSalt;//自定义的盐，为null但指定要加盐的话，就用下面的法则加盐
    protected int bufferSize = 8192;//流处理时，缓冲池的大小
    protected OnProcessingListener processingListener;//流处理时的监听器

    //---------------------------------------------------------------------------------

    /**
     * 设置缓冲池大小
     *
     * @param totalCount 预计要处理的字节总数
     */
    protected void setBufferSize(long totalCount) {
        if (totalCount <= 1024) {
            bufferSize = (int) totalCount;
        } else {
            bufferSize = 10240;
        }
    }

    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    public void setProcessingListener(OnProcessingListener processingListener) {
        this.processingListener = processingListener;
    }

    //---------------------------------------------------------------------------------

    /**
     * 获取随机密钥的种子
     *
     * @param userSeed 用户指定的密钥种子
     */
    protected SecureRandom getSecureRandom(String userSeed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom;
        if (userSeed == null || "".equals(userSeed)) {
            secureRandom = new SecureRandom();
        } else {
//            String rule = getSaltContent(userSeed);//加盐
//            secureRandom = new SecureRandom(userSeed.getBytes());
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(userSeed.getBytes());
        }
        return secureRandom;
    }

    /**
     * 获取加盐后的明文内容
     */
    protected String getSaltContent(String content) {
//        if(isFixed)//固定盐
//            return getSaltFixed(content);
//        else//随机盐
//            return (content + getSaltRandom());
        return getSaltFixed(content);
    }

    /**
     * 获取默认加盐层数
     *
     * @return 如果自定义盐为空则不加盐，否则加盐层级为1
     */
    public SaltLevel getDefaultSaltLevel() {
        return ((customSalt == null || "".equals(customSalt)) ? SaltLevel.NONE : SaltLevel.LEVEL_1);
    }

    /**
     * 加盐 - 随机字符串
     */
    private String getSaltRandom() {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        Random random0 = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < sb.capacity(); i++)
            sb.append(hex[random0.nextInt(16)]);
        return sb.toString();
    }

    /**
     * 加盐 - 固定字符串
     */
    private String getSaltFixed(String content) {
        StringBuilder sb = new StringBuilder();
        if (customSalt == null || "".equals(customSalt)) {
            char[] arr = content.toCharArray();
            Integer i = 0;
            for (char c : arr) {
                sb.append(i);
                sb.append(c);
                i++;
            }
            sb.append("@WQ");

        } else {
            sb.append(content).append(customSalt);
        }
        return sb.toString();
    }

    public void setCustomSalt(String customSalt) {
        this.customSalt = customSalt;
    }

    //---------------------------------------------------------------------------------

    /**
     * 字节数组转16进制字符串 - 格式化方式
     */
    protected String byte2HexByFormat(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 字节数组转16进制字符串 - 位运算方式
     */
    protected String byte2HexByBitwise(byte[] bytes) {
        final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)// 从第一个字节开始，对每一个字节,转换成 16 进制字符的转换
        {
            char[] tempArr = new char[2];
            tempArr[0] = hexDigits[(b >>> 4) & 15];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
            tempArr[1] = hexDigits[b & 15];// 取字节中低 4 位的数字转换
            sb.append(tempArr);
        }
        return sb.toString();
    }

}
