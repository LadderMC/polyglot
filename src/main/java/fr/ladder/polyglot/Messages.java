package fr.ladder.polyglot;

import fr.ladder.polyglot.impl.PolyglotMessageLoader;
import fr.ladder.polyglot.impl.PolyglotMessageStore;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

public final class Messages {

    private static volatile MessageStore messageStore = new PolyglotMessageStore();

    private static volatile MessageLoader messageLoader = new PolyglotMessageLoader();

    private static final PriorityQueue<WeightedResolver> languageResolvers = new PriorityQueue<>();

    private Messages() {
        throw new IllegalAccessError("This is a static class and cannot be instantiated");
    }

    public static void overrideStore(MessageStore messageStore) {
        Messages.messageStore = messageStore;
    }

    public static void overrideLoader(MessageLoader messageLoader) {
        Messages.messageLoader = messageLoader;
    }

    public static void addResolver(LanguageResolver resolver, int weight) {
        languageResolvers.add(new WeightedResolver(resolver, weight));
    }

    public static void loadMessages(Plugin plugin) {
        messageLoader.loadMessages(messageStore, plugin);
    }

    public static boolean exists(Object context, String path) {
        return messageStore.exists(resolveLanguage(context), path);
    }

    public static String get(Object context, String path, Var... vars) {
        return messageStore.get(resolveLanguage(context), path, vars);
    }

    public static Map<String, String> getAll(String path, Var... vars) {
        return messageStore.getAll(path, vars);
    }

    public static String[] array(Object context, String path, Var... vars) {
        return messageStore.array(resolveLanguage(context), path, vars);
    }

    public static List<String> list(Object context, String path, Var... vars) {
        return messageStore.list(resolveLanguage(context), path, vars);
    }

    public static void addDefaultVariable(String key, Object value) {
        messageStore.addDefaultVariable(key, value);
    }

    public static void addDefaultVariableMessage(String key, String path, Var... vars) {
        messageStore.addDefaultVariableMessage(key, path, vars);
    }

    private record WeightedResolver(LanguageResolver resolver, int weight) implements Comparable<WeightedResolver> {

        @Override
        public int compareTo(WeightedResolver other) {
            return Integer.compare(other.weight, this.weight);
        }

    }

    private static String resolveLanguage(Object context) {
        for (WeightedResolver resolver : languageResolvers) {
            Optional<String> lang = resolver.resolver().resolve(context);
            if (lang.isPresent()) {
                return lang.get();
            }
        }
        throw new IllegalStateException("No language resolver could determine the language");
    }
}
