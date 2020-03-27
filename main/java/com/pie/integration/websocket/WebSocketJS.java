package com.pie.integration.websocket;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class WebSocketJS {

    private static final Logger LOGGER = LogManager.getLogger();

    public WebSocketJS(){}

    public void newWebSocket(String url){
        try {
            new WebSocketClient().connect(url);
        } catch (InterruptedException e) {
            LOGGER.error("Uh-oh, Your Websocket had an Interrupted Exception Error: "+e.toString());
        } catch (IOException e) {
            LOGGER.error("Uh-oh, Your Websocket had an IOException Error: "+e.toString());
        } catch (URISyntaxException e) {
            LOGGER.error("Uh-oh, Your Websocket connection to had an URISyntaxException Error: "+e.toString());
        }
    }
}
