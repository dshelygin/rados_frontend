package utils;





import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dshelygin on 13.09.2017.
 * набор утилит для работ, связанных с криптографией
 */
public class CryptoUtils {

    final static Logger logger = Logger.getLogger(CryptoUtils.class.getName());

    // генерирует хэш по заданному алгоритму для массива данных
    // массив генерируется полностью для всего массива за раз
    public static  byte[] getHashAsByte(byte[] data, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(data);
        return digest.digest();
    }

    //представляет хэш в качетсве шестнацатеричной строки.
    public static  String getHashAsString(byte[] data, String algorithm) throws Exception {
        byte[] digestValue = getHashAsByte(data, algorithm);
        return DatatypeConverter.printHexBinary(digestValue);
    }



}
