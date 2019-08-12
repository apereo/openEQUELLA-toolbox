package org.apereo.openequella.tools.toolbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;


public class JsonReportDriver {
    private static Logger LOGGER = LogManager.getLogger(JsonReportDriver.class);

    public List<String> execute(Config config, String[] args) {
        List<String> results = new ArrayList<>();
        try {

            JsonElement root = FileUtils.parseAsJson(config, Config.REPORT_JSON_RAW_DATA_FILENAME);
            JsonArray rootArray = root.getAsJsonObject().getAsJsonArray(config.getConfig(Config.REPORT_JSON_ROOT_ARRAY_KEY));

            // Build the header
            results.add("Location," + config.getConfig(Config.REPORT_JSON_KEYWORDS));

            // Expose the data
            for(int i = 0; i < rootArray.size(); i++) {
                JsonObject currentObject = rootArray.get(i).getAsJsonObject();
                LOGGER.debug("Working with JSON Object: {}", currentObject);
                String row = FileUtils.exposeKeys(currentObject, config.getConfig(Config.REPORT_JSON_KEYWORDS));
                String wrapper = config.getConfig(Config.REPORT_CONFIG_TEMP_FILENAME) + "," + row;
                if(!results.contains(wrapper)) {
                    results.add(wrapper);
                }
            }

            LOGGER.info("Results:");
            StringBuilder sb = new StringBuilder();
            for(String s : results) {
                sb.append(s).append("\n");
            }
            LOGGER.info(sb.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}
