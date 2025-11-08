package fr.ladder.polyglot;

import org.bukkit.ChatColor;

public record Var(String key, String value) {
    
    public static Var of(String key, String value) {
        return new Var(key, value);
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
}
