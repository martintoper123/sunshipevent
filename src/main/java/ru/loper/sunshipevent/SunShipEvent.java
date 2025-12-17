package ru.loper.sunshipevent;

import java.util.Optional;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suneventmanager.api.modules.event.EventManager;
import ru.loper.sunshipevent.commands.ShipCommand;
import ru.loper.sunshipevent.config.ShipConfigManager;
import ru.loper.sunshipevent.event.ShipEvent;
import ru.loper.sunshipevent.listeners.CapitanListener;
import ru.loper.sunshipevent.listeners.ShipKeyListener;

public final class SunShipEvent extends JavaPlugin {
   private static SunShipEvent instance;
   private ShipConfigManager configManager;

   @Override
   public void onEnable() {
      instance = this;

      this.configManager = new ShipConfigManager(this);

      Bukkit.getPluginManager().registerEvents(new CapitanListener(), this);
      Bukkit.getPluginManager().registerEvents(new ShipKeyListener(), this);

      Optional.ofNullable(this.getCommand("ship"))
         .orElseThrow(() -> new IllegalStateException("Command 'ship' not found!"))
         .setExecutor(new ShipCommand(this));

      this.registerEvent();
   }

   private void registerEvent() {
      // ✅ Mini-pattern: optional dependency lookup at runtime
      Plugin semPlugin = Bukkit.getPluginManager().getPlugin("SunEventManager");
      if (semPlugin == null || !semPlugin.isEnabled()) {
         getLogger().warning("SunEventManager not found or not enabled. ShipEvent will run without event registration.");
         return;
      }

      // Мы не обращаемся к SunEventManager.getInstance() напрямую.
      // Вместо этого безопасно получаем EventManager через рефлексию.
      try {
         Object semInstance = semPlugin.getClass().getMethod("getInstance").invoke(null);
         if (semInstance == null) {
            getLogger().warning("SunEventManager.getInstance() returned null. Skipping event registration.");
            return;
         }

         Object em = semInstance.getClass().getMethod("getEventManager").invoke(semInstance);
         if (!(em instanceof EventManager eventManager)) {
            getLogger().warning("SunEventManager.getEventManager() returned invalid type. Skipping event registration.");
            return;
         }

         eventManager.registerEvent(this, "ship", ShipEvent.class, this.getConfigManager().getEventConfigManager());
      } catch (Throwable t) {
         getLogger().warning("Failed to hook into SunEventManager. Skipping event registration. Cause: " + t.getClass().getSimpleName() + ": " + t.getMessage());
      }
   }

   @Generated
   public ShipConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public static SunShipEvent getInstance() {
      return instance;
   }
}
