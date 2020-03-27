package com.pie.integration.websocket;

import dev.latvian.kubejs.event.EventJS;
import io.netty.channel.ChannelHandlerContext;

public class WebSocketEvent extends EventJS {

    private final String type;
    private final ChannelHandlerContext ctx;
    private final Object msg;

    public WebSocketEvent(String type, ChannelHandlerContext ctx, Object msg){
        this.type=type;
        this.ctx=ctx;
        this.msg=msg;
    }

    public String getType(){
        return this.type;
    }

    public ChannelHandlerContext getContext(){
        return this.ctx;
    }

    public Object getMessage(){
        return this.msg;
    }
}
