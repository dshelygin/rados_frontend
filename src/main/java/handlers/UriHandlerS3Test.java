package handlers;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import s3.S3Connection;

/**
 * Created by dshelygin on 21.09.2017.
 * тестовый обработчик
 */
@Mapped(uri = "/test")
public class UriHandlerS3Test extends UriHandlerS3{
    @Override
    public FullHttpResponse process(HttpRequest request, StringBuilder buff) {

        String uri = request.uri();

        //сохраняем key и bucket
        //если один из них (или оба) пусты, отправляем сообщение об этом
        String keyAndBucketResponse = setKeyAndBucket(uri);
        if (!keyAndBucketResponse.equals("")) {
            return getMessageResponse(keyAndBucketResponse, HttpResponseStatus.BAD_REQUEST, false);
        }

       // S3Connection.INSTANCE.setS3ObjectMetaParameter(getBucket(),getKey(),"test","Hello World");

        String responseMessage = S3Connection.INSTANCE.getS3ObjectMetaParameter(getBucket(),getKey(),"test");

        String tmp = "for checking commit merging";

        return getMessageResponse(responseMessage, HttpResponseStatus.OK, false);





    }
}
