package fr.ladder.polyglot.sample;

import fr.ladder.polyglot.Messages;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * @author Snowtyy
 */
public class SamplePlugin extends JavaPlugin {

    public SamplePlugin() {
        Messages.addResolver(ctx -> Optional.of("fr"), 0);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {

        Messages.loadMessages(this);

        this.getLogger().info("test1: " + Messages.get(null,"fr.ladder.polyglot.test.common_message"));
        this.getLogger().info("test2: " + Messages.get(null,"fr.ladder.polyglot.test.second.common_message"));
        this.getLogger().info("test3: " + Messages.get(null,"fr.ladder.polyglot.test.list"));

        for (String line : Messages.array(null, "fr.ladder.polyglot.test.list")) {
            this.getLogger().info("- " + line);
        }

    }
}
