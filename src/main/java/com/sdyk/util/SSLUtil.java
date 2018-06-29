package com.sdyk.util;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by taylor on 2018/6/8.
 */
public class SSLUtil {
    public static SSLEngine createSSLEngine() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS"); ///
        String projectPath=new File("").getAbsolutePath();
        String property_filenames=projectPath + "/etc/gornix.jks";
        InputStream ksInputStream = new FileInputStream(property_filenames); /// 证书存放地址
        ks.load(ksInputStream, "654321".toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "654321".toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext.createSSLEngine();
    }
}
