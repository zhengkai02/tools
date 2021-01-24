package com.zk.utools.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

/**
 * @author kaikai_zheng
 * @version 1.0.0
 * @className IPUtil
 * @description //获取IP工具类
 * @data 2020-09-25 10:28
 */
@Slf4j
public class IPUtil {
    public static final String UNKNOWN = "unknown";

    /**
     * 获取客户端IP地址
     *
     * @param request 当前请求对象
     * @return 实际IP地址
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        log.trace("当前IP来源[X-Forwarded-For], 值[{}]", ip);
        if (!StringUtils.isEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        log.trace("当前IP来源[X-Real-IP], 值[{}]", ip);
        if (!StringUtils.isEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.trace("当前IP来源[Proxy-Client-IP], 值[{}]", ip);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.trace("当前IP来源[WL-Proxy-Client-IP], 值[{}]", ip);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.trace("当前IP来源[HTTP_CLIENT_IP], 值[{}]", ip);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.trace("当前IP来源[HTTP_X_FORWARDED_FOR], 值[{}]", ip);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            log.trace("当前IP来源[getRemoteAddr], 值[{}]", ip);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            String ipv4FromLocal = getIpv4FromLocal();
            if (StringUtils.isNotEmpty(ipv4FromLocal)) {
                ip = ipv4FromLocal;
            }
        }
        return ip;
    }

    private static String getIpv4FromLocal() {
        String ip = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec("cmd.exe /c ipconfig | findstr IPv4");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = br.readLine();
            ip = line.substring(line.indexOf(':') + 1).trim();
        } catch (IOException e) {
            log.warn("获取本地IP异常", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.debug("流关闭异常", e);
            }
        }
        return ip;
    }


    /**
     * @return java.lang.String
     * @description 获取客户端IP地址
     * @author xju_z
     * @date 2021/1/23 23:37
     * @params [request]
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }

        return ipAddress;
    }

    /**
     * 获取mac地址的方法
     *
     * @param ia InetAddress对象
     * @return mac地址
     * @throws SocketException
     */
    private static String getLocalMac(InetAddress ia) throws SocketException {
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
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
        return sb.toString().toUpperCase();
    }

    /**
     * @return java.lang.String
     * @description 获取服务端IP
     * @author xju_z
     * @date 2021/1/23 23:33
     * @params []
     */
    public static String getServerIpAddr() {
        String ip = "";
        try {
            ip = normalizeHostAddress(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.getMessage();
        }
        return ip;

    }

    private static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }
}

