package org.gemignani.henrique.gocd.http_notifier;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

public class PipelineListener {
    private final PipelineDetailsPopulator populator;
    private Logger LOGGER = Logger.getLoggerFor(PipelineListener.class);

    public PipelineListener() {
        this.populator = new PipelineDetailsPopulator();
    }

    public void notify(GoPluginApiRequest message)
            throws Exception {
        LOGGER.info("notify called with request name '" + message.requestName() + "' and requestBody '" + message.requestBody() + "'");
        String expandedMessage = populator.extendMessageToIncludePipelineDetails(message.requestBody());

        //this.webSocketServer.sendToAll(expandedMessage);
    }
}