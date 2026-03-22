package fr.ladder.polyglot;

import org.bukkit.ChatColor;

public record Var(String key, String value, boolean resolveAsMessage, Var[] vars) {

    private static final Var[] EMPTY = new Var[0];

    public static Var of(String key, String value) {
        return new Var(key, value, false, EMPTY);
    }
    
    public static Var of(String key, String value, ChatColor color) {
        return of(key, color.toString() + value);
    }
    
    public static Var of(String key, Object value) {
        return of(key, value.toString());
    }
    
    public static Var of(String key, Object value, ChatColor color) {
        return of(key, color.toString() + value.toString());
    }

    public static Var message(String key, String path, Var... vars) {
        return new Var(key, path, true, vars);
    }

}
