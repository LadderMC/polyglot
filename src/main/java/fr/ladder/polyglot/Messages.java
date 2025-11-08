package fr.ladder.polyglot;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class Messages {
    
    private static Implementation implementation;

    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
    
    public static void loadAllMessages(JavaPlugin plugin) {
        implementation.loadAllMessages(plugin);
    }
    
    public static boolean exists(String path) {
        return implementation.exists(path);
    }
    
    public static String get(String path, Var... vars) {
        return implementation.get(path, vars);
    }

    public static String[] array(String path, Var... vars) {
        return implementation.array(path, vars);
    }

    public static List<String> list(String path, Var... vars) {
        return List.of(implementation.array(path, vars));
    }

    public static void addDefaultVariable(String key, Object value) {
        implementation.addDefaultVariable(key, value);
    }
    
    public interface Implementation {
        
        void loadAllMessages(JavaPlugin plugin);
        
        boolean exists(String path);
        
        String get(String path, Var... vars);
        
        String[] array(String path, Var... vars);

        void addDefaultVariable(String key, Object value);
    }
}
