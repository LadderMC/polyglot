package fr.ladder.polyglot.impl;

import fr.ladder.polyglot.MessageStore;
import fr.ladder.polyglot.Var;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Snowtyy
 */
public class PolyglotMessageStore implements MessageStore {

    private final Map<String, Map<String, String>> messages;

    private final Collection<Var> defaultVars;

    public PolyglotMessageStore() {
        messages = new HashMap<>();
        defaultVars = new ArrayList<>();
    }

    @Override
    public boolean exists(String language, String path) {
        this.ensureValidLanguage(language);
        return messages.containsKey(language) && messages.get(language).containsKey(path);
    }

    @Override
    public String get(String language, String path, Var... vars) {
        this.ensureValidLanguage(language);
        String message = messages.computeIfAbsent(language, k -> new HashMap<>()).get(path);
        if(message == null)
            throw new IllegalArgumentException("Missing message for the path: \"" + path + "\"");

        return this.formatMessage(language, message, vars);
    }

    @Override
    public Map<String, String> getAll(String path, Var... vars) {
        Map<String, String> result = new HashMap<>();
        messages.forEach((language, map) -> {
            String message = this.formatMessage(language, map.getOrDefault(path, ""), vars);
            result.put(language, message);
        });

        return result;
    }

    @Override
    public String[] array(String language, String path, Var... vars) {
        return this.get(language, path, vars).split("\n");
    }

    @Override
    public List<String> list(String language, String path, Var... vars) {
        return List.of(this.array(language, path, vars));
    }

    @Override
    public void addDefaultVariable(String key, Object value) {
        defaultVars.add(Var.of(key, value));
    }

    @Override
    public void addDefaultVariableMessage(String key, String path, Var... vars) {
        defaultVars.add(Var.message(key, path, vars));
    }

    @Override
    public void store(String language, String path, String message) {
        this.ensureValidLanguage(language);
        messages.computeIfAbsent(language, k -> new HashMap<>()).put(path, message);
    }

    private String formatMessage(String language, String message, Var... vars) {
        for(Var var : vars) {
            message = message.replace('{' + var.key() + '}', var.resolveAsMessage()
                    ? this.get(language, var.value(), var.vars())
                    : var.value());
        }

        for(Var var : this.defaultVars) {
            message = message.replace('{' + var.key() + '}', var.resolveAsMessage()
                    ? this.get(language, var.value(), var.vars())
                    : var.value());
        }

        return message.replace("\t", "  ");
    }

    private void ensureValidLanguage(String language) {
        if(Pattern.compile("^[a-z]{2}$").matcher(language).matches())
            return;

        throw new IllegalArgumentException("Invalid language code: " + language);
    }
}
