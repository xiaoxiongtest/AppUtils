package com.apputils.example.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;


import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

/**
* @ClassName:	AbDeviceUtil 
* @Description:	TODO
* @Author		yu
* @Version		1.0
* @Time		[2014年12月17日 上午10:51:38] 
*/
public class AbDeviceUtil {
    
    private static String IMEI, MACADDRESS, NETWORKTYPE, NETWORKSUBTYPE, NETWORKSUBNAME;
    
    /** @Fields PROVIDERSNAME : 服务商名称 */
    private static String PROVIDERSNAME;
    
    /**
     * 获取手机系统SDK版本
     * 
     * @return 如API 17 则返回 17
     */
    /** 
     * @author yu
     * @Title: getSDKVersion 
     * @Description: TODO
     * @param @return 
     * @return int 
     * @throws 
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
    
    /** 
     * @author ljl
     * @Description: 判断是否联网 
     * @param @param context
     * @param @return 
     * @return int  0不可联网，1已连接wifi，2，其他网络
     * @throws 
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = nw.getActiveNetworkInfo();
        if (netinfo == null) {
            return 0;
        }
        if (!netinfo.isAvailable()) {
            return 0;
        }
        if (netinfo.getTypeName().toUpperCase(Locale.ENGLISH).equals("WIFI")) {
            return 1;
        }
        return 2;
        
    }
    
    /** 
     * @author yu
     * @Title: getLocalIpAddress 
     * @Description: 获取ip地址
     * @param @return 默认“”
     * @return String 
     * @throws 
     */
    public static String getLocalIpAddress(Context context) {
        getNetworkType(context);
        if (NETWORKTYPE.equals("MOBILE")) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            }
            catch (SocketException e) {
                MLog.p(e);
            }
        }
        else if (NETWORKTYPE.equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            // 获取32位整型IP地址
            int ipAddress = wifiInfo.getIpAddress();
            
            //返回整型地址转换成“*.*.*.*”地址
            return String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
            
        }
        return "";
    }
    
    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return
     */
    public boolean isWifiConnected(Context context) {  
    	try {
    		ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager  
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            if (mWiFiNetworkInfo != null) {  
                return mWiFiNetworkInfo.isAvailable();  
            } 
		} catch (Exception e) {
			return false ;
		}
        return false;  
    }
    
    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {  
    	try {
    		if (context != null) {  
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
                        .getSystemService(Context.CONNECTIVITY_SERVICE);  
                NetworkInfo mMobileNetworkInfo = mConnectivityManager  
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
                if (mMobileNetworkInfo != null) {  
                    return mMobileNetworkInfo.isAvailable();  
                }  
            } 
		} catch (Exception e) {
			return false;
		}
        return false;  
    }
    /**
     * Gps是否打开
     * 需要<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    /**
     * 判断当前网络是否是移动数据网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }
    
    /**
     * 设备MODEL
     * 
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }
    
    /**
     * 供应商
     * 
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }
    
    /**
     * 设备名称
     * 
     * @return
     */
    public static String getDevice() {
        return android.os.Build.DEVICE;
    }
    
    /**
     * 制造商
     * 
     * @return
     */
    public static String getFacturer() {
        return android.os.Build.MANUFACTURER;
    }
    
    /**
     * sdk版本
     * 
     * @return
     */
    public static String getReleaseVersion() {
        String version = android.os.Build.VERSION.RELEASE;
        if (version.toUpperCase(Locale.ENGLISH).indexOf("ANDROID") >= 0) {
            version = "android " + version;
        }
        return version;
    }
    
    /**
     * 获取网络类型
     * 
     * @param context
     * @return
     */
    public static String getNetWorkSubName(Context context) {
        getNetworkType(context);
        return NETWORKSUBNAME;
    }
    
    /**
     * 获取网络类型描述
     * 
     * @param context
     * @return
     */
    public static String getNetWorkSubType(Context context) {
        getNetworkType(context);
        return NETWORKSUBTYPE;
    }
    
    /**
     * 获取手机串号
     * 
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        if (IMEI == null) {
            TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = telephonemanage.getDeviceId();
            PROVIDERSNAME = telephonemanage.getNetworkOperatorName();
        }
        return IMEI;
    }
    
    /**
     * @Description: 获取电话号码 
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonemanage.getLine1Number();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = "";
        }
        return phoneNumber;
    }
    
    /** 
     * @Description: 获取设备唯一标示
     * @return String   IMEI 否则MacAddress
     */
    public static String getDeviceid(Context context) {
        String ren = getImei(context);
        if (ren != null && ren.length() > 0) {
            return ren;
        }
        return getMacAddress(context);
    }
    
    /**
     * 获取运营商名称
     * 
     * @param context
     * @return
     */
    public static String getProvidersName(Context context) {
        if (TextUtils.isEmpty(PROVIDERSNAME))
            getImei(context);
        return PROVIDERSNAME;
    }
    
    /**
     * 获取运营商名称
     * 
     * @param context
     * @return
     */
    public static String getIspName(Context context) {
        
        String isp = getProvidersName(context);
        if (null != isp) {
            isp = isp.replaceAll(" ", "").toLowerCase();
            if (isp.equals("chinamobile")) {
                isp = "中国移动";
            }
            if (isp.equals("chinaunicom")) {
                isp = "中国联通";
            }
            if (isp.equals("chinatelecom")) {
                isp = "中国电信";
            }
        }
        else {
            isp = "";
        }
        return isp;
    }
    
    /** 
     * @Description: 获取MacAddress
     * @param @param context
     * @return String 
     */
    public static String getMacAddress(Context context) {
        if (MACADDRESS == null) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            MACADDRESS = info.getMacAddress();
        }
        return MACADDRESS;
    }
    
    /** 
     * @author yu
     * @Title: getNetworkType 
     * @Description: 获取网络类型
     * @param @param context
     * @param @return 
     * @return String 
     * @throws 
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (info == null) {
            NETWORKTYPE = "NONE";
            NETWORKSUBNAME = "NONE";
            NETWORKSUBTYPE = "NONE";
            return NETWORKTYPE;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
                NETWORKTYPE = "MOBILE";
                NETWORKSUBTYPE = info.getSubtypeName();
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        NETWORKSUBNAME = "unknown";
                        break;
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        NETWORKSUBNAME = "2G 1xRTT";
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        NETWORKSUBNAME = "2G CDMA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        NETWORKSUBNAME = "2G EDGE";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        NETWORKSUBNAME = "3G EVDO_0";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        NETWORKSUBNAME = "3G EVDO_A";
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        NETWORKSUBNAME = "2G GPRS";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        NETWORKSUBNAME = "HSDPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        NETWORKSUBNAME = "HSPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        NETWORKSUBNAME = "HSUPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        NETWORKSUBNAME = "UMTS";
                        break;
                }
                break;
            case ConnectivityManager.TYPE_WIFI:
                NETWORKTYPE = "WIFI";
                NETWORKSUBNAME = "WIFI";
                NETWORKSUBTYPE = "WIFI";
                break;
            default:
                NETWORKTYPE = "NONE";
                NETWORKSUBNAME = "NONE";
                NETWORKSUBTYPE = "NONE";
                break;
        }
        return NETWORKTYPE;
    }
    
    /** 
     * @Description: 获取可用CPU核心数
     * @return int  Default to return 1 core 
     */
    public static int getNumCores() {
        try {
            //Get directory containing CPU info 
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about 
            File[] files = dir.listFiles(new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number 
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
                
            });
            //Return the number of cores (virtual CPU devices) 
            return files.length;
        }
        catch (Exception e) {
            //Default to return 1 core 
            return 1;
        }
    }
}
