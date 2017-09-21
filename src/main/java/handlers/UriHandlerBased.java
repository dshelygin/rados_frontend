package handlers;

import io.netty.handler.codec.http.*;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * Created by dshelygin on 06.09.2017.
 *  класс - родитель для обработчиков uri
 */
public abstract class UriHandlerBased {
    public abstract FullHttpResponse  process(HttpRequest request, StringBuilder buff);

    FullHttpResponse getMessageResponse(String responseMessage, HttpResponseStatus status, Boolean isKeepAlive) {
        FullHttpResponse response =  new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                copiedBuffer(responseMessage.getBytes())
        );

        if (isKeepAlive) {
            response.headers().set(
                    HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE
            );
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,responseMessage.length());

        return response;

    }
}
