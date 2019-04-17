package site.cnkj.util;

import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.file.Path;

public class RSADemo {

    /*
    这里使用的是“Legion of the Bouncy Castle”组织开发的轻量级java加解密包bcprov-jdk15on-158.jar包
    以下先将Provider注册到环境中，否则会提示下面在使用"BC"时，会报错找不到对应的provider
    */
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /*
    实现RSA公钥加密的方法
    */
    public static String enc(String plainText) throws IOException, GeneralSecurityException  {
        Path pubKeyPath = Paths.get("F:\\dls\\chunk\\config\\rsa_public_key.der");//请改为你的路径
        //byte[] data = Files.readAllBytes(pubKeyPath);
        //X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(data);
        //KeyFactory kf = KeyFactory.getInstance("RSA");

        byte[] buffer = Files.readAllBytes(pubKeyPath);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PublicKey key = keyFactory.generatePublic(keySpec);
        //PublicKey key = kf.generatePublic(x509Spec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        String cipherText = new String(encodedBytes, "UTF-8");
        return cipherText;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // TODO Auto-generated method stub
        String plainText = "kdfao9@#&^kdsfa";
        String cipherText = enc(plainText);
        System.out.println("encypted text:\n"+ cipherText);
    }

}