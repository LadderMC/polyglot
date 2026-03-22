package fr.ladder.polyglot.impl;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import fr.ladder.polyglot.MessageLoader;
import fr.ladder.polyglot.MessageStore;
import fr.ladder.reflex.PluginInspector;
import fr.ladder.reflex.Reflex;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Snowtyy
 */
public class PolyglotMessageLoader implements MessageLoader {

    private static final String FOLDER_PATH = "lang/";

    @Override
    public void loadMessages(MessageStore messageStore, Plugin plugin) {
        Instant now = Instant.now();
        Pattern pattern = Pattern.compile(FOLDER_PATH + "[a-z]{2}/.*\\.json");
        plugin.getLogger().info("[POLYGLOT] Loading polyglot messages...");
        try(PluginInspector inspector = Reflex.getInspector(plugin)) {
            inspector.getResources(pattern)
                    .forEach(resource -> this.loadResource(messageStore, plugin, resource));
        }
        Duration duration = Duration.between(now, Instant.now());
        plugin.getLogger().info("[POLYGLOT] Loaded messages. time: " + (duration.toNanos() / 10000) / 100D + "ms");
    }

    private void loadResource(MessageStore messageStore, Plugin plugin, String resource) {
        String language = resource.substring(FOLDER_PATH.length()).split("/")[0];

        InputStream inputStream = plugin.getResource(resource);
        if(inputStream == null) {
            plugin.getLogger().warning("| Resource '" + resource + "' wasn't found.");
            return;
        }


        try(JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
            plugin.getLogger().info("| Loading messages from '" + resource + "'.");
            this.loadJsonElement(messageStore, language, "", new JsonParser().parse(reader));
        }
        catch(JsonSyntaxException e) {
            plugin.getLogger().warning("| File '" + resource + "' has a bad json syntax.");
        }
        catch (IOException e) {
            plugin.getLogger().warning("| An error occurred while closing input stream.");
        }
    }

    private void loadJsonElement(MessageStore messageStore, String language, String path, JsonElement element) {
        if(element instanceof JsonArray array) {
            StringBuilder message = new StringBuilder();
            for(int i = 0; i < array.size(); i++) {
                message.append(array.get(i).getAsString());
                if(i != array.size() - 1)
                    message.append("\n");
            }
            messageStore.store(language, path, message.toString());
        } else if(element instanceof JsonObject obj) {
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String newPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                this.loadJsonElement(messageStore, language, newPath, entry.getValue());
            }
        } else if(element instanceof JsonPrimitive primitive && primitive.isString()) {
            messageStore.store(language, path, element.getAsString());
        }
    }
}
