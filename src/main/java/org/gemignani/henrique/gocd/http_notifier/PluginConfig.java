package org.gemignani.henrique.gocd.http_notifier;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class PluginConfig {
    private static Logger LOGGER = Logger.getLoggerFor(GoNotificationPlugin.class);
    private static final String PLUGIN_CONF = "gocd-websocket-notifier.conf";
    private int port = 8887;

    public PluginConfig() {
        String userHome = System.getProperty("user.home");
        File pluginConfig = new File(userHome + File.separator + PLUGIN_CONF);
        if (!pluginConfig.exists()) {
            LOGGER.warn(String.format("Config file %s was not found in %s. Using default values.", PLUGIN_CONF, userHome));
        }
        else {
            Config config = ConfigFactory.parseFile(pluginConfig);
            if (config.hasPath("port")) {
                setPort(config.getInt("port"));
            }
        }
    }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
