package org.gemignani.henrique.gocd.http_notifier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

public class PipelineListener {
    private final PluginConfig pluginConfig;
    private Logger LOGGER = Logger.getLoggerFor(PipelineListener.class);

    public PipelineListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public void notify(GoPluginApiRequest message)
            throws Exception {
        LOGGER.info("notify called with request name '" + message.requestName() + "' and requestBody '" + message.requestBody() + "'");

        HttpClient httpclient = HttpClients.createDefault();

        HttpPost request = new HttpPost(pluginConfig.getUrl());

        HttpEntity entity = EntityBuilder.create()
                .setText(message.requestBody())
                .setContentEncoding("UTF-8")
                .setContentType(ContentType.APPLICATION_JSON)
                .build();
        request.setEntity(entity);

        //Execute and get the response.
        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        	throw new Exception("Got not-OK status code response: " + response.getStatusLine());
        }
    }
}
