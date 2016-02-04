package org.gemignani.henrique.gocd.http_notifier;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class PipelineListener {
    private final PipelineDetailsPopulator populator;
    private final PluginConfig pluginConfig;
    private Logger LOGGER = Logger.getLoggerFor(PipelineListener.class);

    public PipelineListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.populator = new PipelineDetailsPopulator();
    }

    public void notify(GoPluginApiRequest message)
            throws Exception {
        LOGGER.info("notify called with request name '" + message.requestName() + "' and requestBody '" + message.requestBody() + "'");
//        String expandedMessage = populator.extendMessageToIncludePipelineDetails(message.requestBody());



        HttpClient httpclient = HttpClients.createDefault();
        LOGGER.info("httpclient: " + httpclient);

        LOGGER.info("pluginConfig: " + pluginConfig);
        LOGGER.info("pluginConfig.getUrl: " + pluginConfig.getUrl());

        HttpPost request = new HttpPost(pluginConfig.getUrl());
        LOGGER.info("request: " + request);

        // Request parameters and other properties.
//        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//        params.add(new BasicNameValuePair("param-1", "12345"));
//        params.add(new BasicNameValuePair("param-2", "Hello!"));
//        request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpEntity entity = EntityBuilder.create()
                .setText(message.requestBody())
                .setContentEncoding("UTF-8")
                .setContentType(ContentType.APPLICATION_JSON)
                .build();
        LOGGER.info("entity: " + entity);
        request.setEntity(entity);

        //Execute and get the response.
        HttpResponse response = httpclient.execute(request);
        LOGGER.info("response: " + response);
    }
}
