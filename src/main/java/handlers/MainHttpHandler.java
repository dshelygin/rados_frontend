package handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import utils.ReflectionTools;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * Created by dshelygin on 21.09.2017.
 * основной обработчик HTTP запросов
 */
public class MainHttpHandler extends ChannelInboundHandlerAdapter {
    //сопоставляет uri и обработчик
    private Map<String, UriHandlerBased> handlers = new HashMap<String, UriHandlerBased>();
    private final StringBuilder buf = new StringBuilder();

    public MainHttpHandler() {

        //получаем список обработчиков для заданных uri
        //uri задается в виде аннотации перед обработчиком
        if (handlers.size()==0) {
            try {
                for (Class c : ReflectionTools.getClasses("handlers")) {
                    Annotation annotation = c.getAnnotation(Mapped.class);
                    if (annotation!=null) {
                        handlers.put(((Mapped) annotation).uri(), (UriHandlerBased)c.newInstance());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            final FullHttpRequest request = (FullHttpRequest) msg;

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            buf.setLength(0);

            String context = queryStringDecoder.path();

            //получаем обработчик
            UriHandlerBased handler = handlers.get(context);

            FullHttpResponse response;
            if (handler!=null) {
                response = handler.process(request, buf);
            } else {
                //если обработчик не найдем шлем сообщение
                final String responseMessage = "url not found!";


                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NOT_ACCEPTABLE,
                        copiedBuffer(responseMessage.getBytes())
                );

                if (HttpUtil.isKeepAlive(request)) {
                    response.headers().set(
                            HttpHeaderNames.CONNECTION,
                            HttpHeaderValues.KEEP_ALIVE
                    );
                }

                response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,responseMessage.length());

            }

            ctx.writeAndFlush(response);

        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }
}
