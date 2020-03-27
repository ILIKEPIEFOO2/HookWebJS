package com.pie.integration;


import com.google.gson.JsonNull;
import dev.latvian.kubejs.util.MapJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dev.latvian.kubejs.util.JsonUtilsJS;

public class WebHook {

    private static final Logger LOGGER = LogManager.getLogger();

    public WebHook(){}

    public String httpPost(String url,String jsonInputString) {
            if(!isJSONValid(jsonInputString)){
                LOGGER.error("MalformedURL: Incorrect JSON content:"+jsonInputString);
                return "{}";
            }
            try {
                URL request = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) request.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);

                return getResponse(connection);

            } catch (MalformedURLException e) {
                LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
                return "{}";
            } catch (IOException e) {
                LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
                return "{}";
            }
    }

    public String httpPost(String url,Object content) {
        try {
            String jsonString = MapJS.of(content).toJson().toString();
            URL request = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);

            return getResponse(connection);

        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
            return "{}";
        } catch (IOException e) {
            LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
            return "{}";
        }
    }

    public String httpPost(String url, String jsonInputString, Object requestPropertiesObj) {
        if(!isJSONValid(jsonInputString)){
            LOGGER.error("MalformedURL: Incorrect JSON content:"+jsonInputString);
            return "{}";
        }
        try {
            MapJS requestProperties = MapJS.of(requestPropertiesObj);
            URL request = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("POST");
            applyUrlParameters(connection,requestPropertiesObj);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);

            return getResponse(connection);

        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
            return "{}";
        } catch (IOException e) {
            LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
            return "{}";
        }
    }

    public String httpPost(String url, Object content, Object requestPropertiesObj) {
        try {
            String jsonString = MapJS.of(content).toJson().toString();
            URL request = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("POST");
            applyUrlParameters(connection,requestPropertiesObj);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);

            return getResponse(connection);

        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
            return "{}";
        } catch (IOException e) {
            LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
            return "{}";
        }
    }

    public String httpGet(String url){
        try {
            URL request = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            return getResponse(connection);

        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
            return "{}";
        } catch (IOException e) {
            LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
            return "{}";
        }
    }

    public String httpGet(String url, Object requestPropertiesObj){
        try {
            URL request = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            applyUrlParameters(connection,requestPropertiesObj);

            return getResponse(connection);

        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURL: Incorrect Url '"+url+"', StackTrace:\n"+e.toString());
            return "{}";
        } catch (IOException e) {
            LOGGER.error("IO Exception: Something weird happened, StackTrace:\n"+e.toString());
            return "{}";
        }
    }

    private void applyUrlParameters(HttpURLConnection connection,Object requestPropertiesObj) {
        MapJS requestProperties = MapJS.of(requestPropertiesObj);
        for(String propertyName : requestProperties.keySet()) {
            connection.setRequestProperty(propertyName, requestProperties.get(propertyName).toString());
        }
    }

    private String getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream(),"utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null){
            response.append(responseLine.trim());
        }

        connection.disconnect();
        return response.toString();
    }

    private boolean isJSONValid(String test) {
        return JsonUtilsJS.of(test)!=JsonNull.INSTANCE;
    }

    public DiscordWebHook postToWebHook(String webhookUrl){
        return new DiscordWebHook(webhookUrl);
    }

    public DiscordWebHook.EmbedObject newEmbed(){ return new DiscordWebHook.EmbedObject(); };
}
