package handlers;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.StringUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.io.IOUtils;
import s3.S3Connection;
import utils.CryptoUtils;


import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Variables.*;

/**
 * Created by dshelygin on 20.09.2017.
 * обработчик для получения хэша файла
 */

@Mapped(uri = "/getHash")
public class UriHandlerS3GetHash extends UriHandlerS3 {
    final private static Logger logger = Logger.getLogger(UriHandlerS3GetHash.class.getName());

    private String alg;  //алгоритм для вычисления хэша

    @Override
    public FullHttpResponse process(HttpRequest request, StringBuilder buff) {

        String uri = request.uri();

        //сохраняем key и bucket
        //если один из них (или оба) пусты, отправляем сообщение об этом
        String keyAndBucketResponse = setKeyAndBucket(uri);
        if (!keyAndBucketResponse.equals("") ) {
            return getMessageResponse(keyAndBucketResponse,HttpResponseStatus.BAD_REQUEST, false );
        }

        //cохраняем алгоритм
        //если алгоритм не передан, используем значение по-умолчанию
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        try {
            alg = decoder.parameters().get(URI_ALG_NAME).get(0);
        } catch (NullPointerException e) {
           alg = DEFAULT_ALG;
        }

        if (alg.equals("")) {
            alg = DEFAULT_ALG;
        }

        //получаем хэш, хранящийся в мете S3
        String storedHash = S3Connection.INSTANCE.getS3ObjectMetaParameter(getBucket(),getKey(),META_HASH_FIELD_NAME);

        //todo совпадение алгоритма с заданным в url не проверяется
        //если хэш получен из меты, возвращаем его,
        // в противном случае, забираем файл, считаем хэщ, сохраняем его значение в мете
        if (!StringUtils.isNullOrEmpty(storedHash)) {
            return getMessageResponse("hash:" + storedHash , HttpResponseStatus.OK, false );
        } else {
            S3ObjectInputStream objectStream = S3Connection.INSTANCE.getS3ObjectAsStream(getBucket(), getKey());
            if (objectStream == null) {
                return getMessageResponse("object: " + getBucket() + "\\" + getKey() + " not found",
                        HttpResponseStatus.BAD_REQUEST, false);

            } else {
                try {
                    byte[] data = IOUtils.toByteArray(objectStream);
                    String fileHash = CryptoUtils.getHashAsString(data, alg);
                    S3Connection.INSTANCE.setS3ObjectMetaParameter(getBucket(),getKey(),META_HASH_FIELD_NAME,fileHash);
                    return getMessageResponse("hash:" + fileHash,
                            HttpResponseStatus.OK, false);

                } catch (Exception e) {
                    logger.log(Level.WARNING, e.toString());
                    return getMessageResponse(e.toString(),
                            HttpResponseStatus.INTERNAL_SERVER_ERROR, false);
                }
            }
        }

    }

}
