package fr.ladder.polyglot;

import java.util.List;

/**
 * @author Snowtyy
 */
public interface MessageStore {

    boolean exists(String language, String path);

    String get(String language, String path, Var... vars);

    String[] array(String language, String path, Var... vars);

    List<String> list(String language, String path, Var... vars);

    void addDefaultVariable(String key, Object value);

    void addDefaultVariableMessage(String key, String path, Var... vars);

    void store(String language, String path, String message);

}
