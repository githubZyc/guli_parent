package com.atguigu.commonutils.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/**
 * MD5进行文件、字符串、byte[]数组进行加密
 * MessageDigest为非线程安全类型 多线程下会出现数据错误，修改为非单例模式
 */
public class MD5Util {
	//哈希值规则包含的字符
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	/**
	 * 对文件进行MD5加密
	 *
	 * @作者: 张晓东
	 * @创建日期： 2015年11月12日
	 *
	 * @param file
	 * @throws IOException
	 * @返回值： String
	 *
	 * @修改记录（修改时间、作者、原因）：
	 */
	public static String getFileMD5String(File file) throws IOException {
		try{
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");//获取信息摘要实例
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			messagedigest.update(byteBuffer);
			return bufferToHex(messagedigest.digest());//进行哈希值计算
		}catch (NoSuchAlgorithmException e){
			return null;
		}
	}



	/**
	 * @description: 对文件进行MD5加密(如果文件size超出int最大范围  getFileMD5String会报错)
	 * @author L.J.R
	 * @date 2020/3/7 15:47
	 * @param file
	 * @return java.lang.String
	 */
	public static String getFileMD5StringTo(File file){
		byte[] byteArr = new byte[4096];
		boolean fileNull = true;
		try{
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");//获取信息摘要实例
			int len = 0;
			FileInputStream in = new FileInputStream(file);
			len = in.read(byteArr);

			if (len > 0){
				fileNull = false;
				while (len > 0){
					messagedigest.update(byteArr, 0, len);
					len = in.read(byteArr);
				}
			}
			if (fileNull){
				return bufferToHex(null);
			}
			return bufferToHex(messagedigest.digest());//进行哈希值计算
		}catch (Exception e){
			return null;
		}
	}

	/**
	 * 对字符串进行MD5加密
	 *
	 * @作者: 张晓东
	 * @创建日期： 2015年11月12日
	 *
	 * @返回值： String
	 *
	 * @修改记录（修改时间、作者、原因）：
	 */
	public static String getMD5String(String s) {
		return getMD5Byte(s.getBytes());
	}

	/**
	 * 对byte类型的数组进行MD5加密
	 *
	 * @作者: 张晓东
	 * @创建日期： 2015年11月12日
	 *
	 * @param bytes
	 * @返回值： String
	 *
	 * @修改记录（修改时间、作者、原因）：
	 */
	public static String getMD5Byte(byte[] bytes) {
		try{
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");//获取信息摘要实例
			messagedigest.update(bytes);
			return bufferToHex(messagedigest.digest());
		}catch (NoSuchAlgorithmException e){
			return null;
		}
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	/**
	 * 
	 *
	 * @作者: 张晓东
	 * @创建日期： 2015年11月14日
	 *
	 * @param bytes 字节数组
	 * @param m 起始字节
	 * @param n 字节长度
	 * @return
	 * @返回值： String
	 *
	 * @修改记录（修改时间、作者、原因）：
	 */
	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			char c0 = hexDigits[(bytes[l] & 0xf0) >> 4];
			char c1 = hexDigits[bytes[l] & 0xf];
			stringbuffer.append(c0);
			stringbuffer.append(c1);
		}
		return stringbuffer.toString();
	}
	
	public static String getId() { 
        try { 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            UUID uuid = UUID.randomUUID(); 
            String guidStr = uuid.toString(); 
            md.update(guidStr.getBytes(), 0, guidStr.length()); 
            return new BigInteger(1, md.digest()).toString(16); 
        } catch (NoSuchAlgorithmException e) { 
            return null; 
        } 
    }

	/** 
     * 自定义规则生成32位编码 
     * @return string 
     */  
    public static String getUUIDByRules(String rules)  
    {  
      int rpoint = 0;  
        StringBuffer generateRandStr = new StringBuffer();  
        Random rand = new Random();  
        int length = 32;  
        for(int i=0;i<length;i++)  
        {  
            if(rules!=null){  
                rpoint = rules.length();  
                int randNum = rand.nextInt(rpoint);  
                generateRandStr.append(rules.substring(randNum,randNum+1));  
            }  
        }  
        return generateRandStr+"";  
    }  
	
	public static void main(String[] args) throws IOException {
		/*String filePath2 = "F:/图片/搜狗图片/766971.jpg";
		System.out.println("766971文件加密结果为：" + getFileMD5String(new File(filePath2)));
		String filePath = "F:/图片/搜狗图片/766972.jpg";
		System.out.println("766972文件加密结果为：" + getFileMD5String(new File(filePath)));*/
		String filePath1 = UUID.randomUUID().toString();
		System.out.println(getMD5String(filePath1));
		System.out.println(getMD5String("111111"));
		System.out.println(getUUIDByRules("1234567890qwertyuiopasdfghjklzxcvbnm"));
	}
}
