package com.pie.integration.websocket.handler;

import com.pie.integration.websocket.WebSocketEvent;
import dev.latvian.kubejs.script.ScriptType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            LOGGER.info("WebSocket Client connected!");
            new WebSocketEvent("connected",ctx,(FullHttpResponse) msg).post(ScriptType.SERVER,"websocket.connected");
            handshakeFuture.setSuccess();

            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.getStatus() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            LOGGER.info("WebSocket Client received message: " + textFrame.text());
            new WebSocketEvent("message",ctx,(TextWebSocketFrame) msg).post(ScriptType.SERVER,"websocket.message.received");
        } else if (frame instanceof PongWebSocketFrame) {
            LOGGER.info("WebSocket Client received pong");
            new WebSocketEvent("pong",ctx,(TextWebSocketFrame) msg).post(ScriptType.SERVER,"websocket.message.pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            LOGGER.info("WebSocket Client received closing");
            new WebSocketEvent("close",ctx,(TextWebSocketFrame) msg).post(ScriptType.SERVER,"websocket.close");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

}