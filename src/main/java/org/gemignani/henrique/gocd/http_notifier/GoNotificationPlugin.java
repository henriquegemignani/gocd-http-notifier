package org.gemignani.henrique.gocd.http_notifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.*;

@Extension
public class GoNotificationPlugin
        implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(GoNotificationPlugin.class);
    public static final String EXTENSION_TYPE = "notification";
    private static final List<String> goSupportedVersions = Arrays.asList("1.0");
    public static final String REQUEST_PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String REQUEST_PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String REQUEST_PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";
    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;
    private PipelineListener pipelineListener;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        DefaultGoApiRequest apiRequest = new DefaultGoApiRequest("go.processor.plugin-settings.get", "1.0", pluginIdentifier());
        goApplicationAccessor.submit(apiRequest).responseBody();
        PluginConfig pluginConfig = new Gson().fromJson(goApplicationAccessor.submit(apiRequest).responseBody(), PluginConfig.class);

        pipelineListener = new PipelineListener(pluginConfig);
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        LOGGER.debug("received go plugin api request " + goPluginApiRequest.requestName());
        if (goPluginApiRequest.requestName().equals(REQUEST_PLUGIN_SETTINGS_GET_CONFIGURATION))
            return handlePluginSettingsGetConfiguration();
        if (goPluginApiRequest.requestName().equals(REQUEST_PLUGIN_SETTINGS_GET_VIEW))
            return handlePluginSettingsGetView();
        if (goPluginApiRequest.requestName().equals(REQUEST_PLUGIN_SETTINGS_VALIDATE_CONFIGURATION))
            return handlePluginSettingsValidateConfiguration();
        if (goPluginApiRequest.requestName().equals(REQUEST_NOTIFICATIONS_INTERESTED_IN))
            return handleNotificationsInterestedIn();
        if (goPluginApiRequest.requestName().equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        return null;
    }

    private GoPluginApiResponse handlePluginSettingsGetConfiguration() {
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return SUCCESS_RESPONSE_CODE;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                JsonObject object = new JsonObject();
                JsonObject item = new JsonObject();
                item.addProperty("display-name", "URL");
                item.addProperty("display-order", "0");
                object.add("url", item);
                return object.toString();
            }
        };
    }

    private GoPluginApiResponse handlePluginSettingsGetView() {
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return SUCCESS_RESPONSE_CODE;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                JsonObject object = new JsonObject();
                object.addProperty("template", "<div class=\"form_item_block\"><label>URL:<span class=\"asterisk\">*</span><input type=\"text\" ng-model=\"url\" ng-required=\"true\"></label></div>");
                return object.toString();
            }
        };
    }

    private GoPluginApiResponse handlePluginSettingsValidateConfiguration() {
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return SUCCESS_RESPONSE_CODE;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return new JsonArray().toString();
            }
        };
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        LOGGER.debug("received pluginIdentifier request");
        return new GoPluginIdentifier(EXTENSION_TYPE, goSupportedVersions);
    }

    private GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, List<String>> response = new HashMap<String, List<String>>();
        response.put("notifications", Arrays.asList(REQUEST_STAGE_STATUS));
        LOGGER.debug("requesting details of stage-status notifications");
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        LOGGER.debug("handling stage notification");

        int responseCode = SUCCESS_RESPONSE_CODE;

        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        try {
            response.put("status", "success");
            pipelineListener.notify(goPluginApiRequest);
        } catch (Exception e) {
            LOGGER.error("failed to notify pipeline listener", e);
            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            messages.add(e.getMessage());
        }

        response.put("messages", messages);
        return renderJSON(responseCode, response);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            public int responseCode() {
                return responseCode;
            }

            public Map<String, String> responseHeaders() {
                return null;
            }

            public String responseBody() {
                return json;
            }
        };
    }
}
