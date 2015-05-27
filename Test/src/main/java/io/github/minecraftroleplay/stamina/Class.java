package io.github.minecraftroleplay.stamina;
import org.bukkit.plugin.java.JavaPlugin;
public class Class {
	public final class Test extends JavaPlugin {
		@Override
	    public void onEnable() {
			getLogger().info("onEnable has been invoked!");
	    }
	    @Override
	    public void onDisable() {
			getLogger().info("onDisable has been invoked!");
	    }
	}
}