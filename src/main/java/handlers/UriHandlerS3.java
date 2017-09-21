package handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import static utils.Variables.*;

/**
 * Created by dshelygin on 20.09.2017.
 * класс- родитель для обработчиков uri, предназначенных для получения информации о файлах, хранящихся в ceph.
 */
 abstract class UriHandlerS3 extends  UriHandlerBased {


    private String key;  //ключ объекта S3
    private String bucket; //корзина объекта S3


    String setKeyAndBucket(String uri) {
        String responseMessage = "";
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        try {
            bucket = decoder.parameters().get(URI_BUCKET_NAME).get(0);
            key = decoder.parameters().get(URI_KEY_NAME).get(0);
        } catch (NullPointerException e) {
            return "incorrect request: you need " + URI_BUCKET_NAME + " and " + URI_KEY_NAME;
        }

        if (bucket.equals("")) {
            responseMessage = responseMessage + URI_BUCKET_NAME + " can't be empty\n";
        }
        if (key.equals("")) {
            responseMessage = responseMessage + URI_KEY_NAME + " can't be empty\n";
        }

        return responseMessage;
    }

    String getBucket() {
        return bucket;
    }

    String getKey() {
        return key;
    }

}
