package fr.ladder.polyglot.base;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import fr.ladder.polyglot.Messages;
import fr.ladder.polyglot.Var;
import fr.ladder.reflex.Reflex;
import fr.ladder.reflex.PluginInspector;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PolyglotMessages implements Messages.Implementation {

    private final Plugin _engine;

    private final Map<String, String> _messages;

    private final Collection<Var> defaultVars = new ArrayList<>();

    public PolyglotMessages(Plugin engine) {
        _engine = engine;
        _messages = new HashMap<>();
    }

    public void loadAllMessages(Plugin plugin, PluginInspector inspector) {
        // fetch language from config
        final String language = _engine.getConfig()
                .getString("language", "fr")
                .toLowerCase();

        final Pattern pattern = Pattern.compile("lang/" + language + "/.*\\.json");
        // fetch all lang files

        int previousSize = _messages.size();
        inspector.getResources(pattern).forEach(resource -> this.loadAllMessages(plugin, resource));
        plugin.getLogger().info("| All messages has been successfully loaded.");
        plugin.getLogger().info("| Number of loaded messages: " + (_messages.size() - previousSize));

    }

    public void loadAllMessages(Plugin plugin) {
        try(PluginInspector inspector = Reflex.getInspector(plugin)) {
            this.loadAllMessages(plugin, inspector);
        }
    }

    private void loadAllMessages(Plugin plugin, String filename) {
        try(InputStream inputStream = plugin.getResource(filename)) {
            if(inputStream == null) {
                plugin.getLogger().warning("| File '" + filename + "' wasn't found.");
                return;
            }

            plugin.getLogger().info("| Loading messages of '" + filename + "'.");
            try(JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
                this.load("", new JsonParser().parse(reader));
            }
            catch(JsonSyntaxException e) {
                plugin.getLogger().warning("| File '" + filename + "' has a bad json syntax.");
            }
        } catch(IOException e) {
            plugin.getLogger().warning("An error occurred while loading lang file: " + filename);
        }
    }

    private void load(String path, JsonElement element) {
        if(element instanceof JsonArray array) {
            StringBuilder message = new StringBuilder();
            for(int i = 0; i < array.size(); i++) {
                message.append(array.get(i).getAsString());
                if(i != array.size() - 1)
                    message.append("\n");
            }
            _messages.put(path.substring(1), message.toString());
        } else if(element instanceof JsonObject obj) {
            obj.entrySet().forEach(member -> this.load(path + "." + member.getKey(), member.getValue()));
        } else if(element instanceof JsonPrimitive primitive && primitive.isString()) {
            _messages.put(path.substring(1), element.getAsString());
        }
    }

    private String formatMessage(String message, Var... vars) {
        for(Var var : vars) {
            message = message.replace('{' + var.key() + '}', var.value());
        }

        for(Var var : this.defaultVars) {
            message = message.replace('{' + var.key() + '}', var.value());
        }

        return message.replace("\t", "  ");
    }

    @Override
    public boolean exists(String path) {
        return _messages.containsKey(path);
    }

    @Override
    public String get(String path, Var... vars) {
        String message = _messages.get(path);
        if(message == null)
            throw new IllegalArgumentException("Missing message for the path: \"" + path + "\"");

        return this.formatMessage(message, vars);
    }

    @Override
    public String[] array(String path, Var... vars) {
        return this.get(path, vars).split("\n");
    }

    @Override
    public void addDefaultVariable(String key, Object value) {
        this.defaultVars.add(Var.of(key, value));
    }
}
