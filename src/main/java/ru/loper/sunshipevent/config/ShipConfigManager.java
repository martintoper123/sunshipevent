package ru.loper.sunshipevent.config;

import lombok.Generated;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;

public class ShipConfigManager extends ConfigManager {
   private ShipEventConfigManager eventConfigManager;
   private String noPermissionMessage;
   private String noTreasureMessage;
   private String onlyKeyMessage;

   public ShipConfigManager(Plugin plugin) {
      super(plugin);
   }

   public void loadConfigs() {
      this.addCustomConfig(new CustomConfig("ship.yml", this.plugin));
      this.addCustomConfig(new CustomConfig("messages.yml", this.plugin));
      this.eventConfigManager = new ShipEventConfigManager(this.getShipEventConfig(), this.plugin);
   }

   public void loadValues() {
      CustomConfig messagesConfig = this.getCustomConfig("messages.yml");
      this.noPermissionMessage = messagesConfig.configMessage("no_permissions", "&c▶ &fУ вас недостаточно прав!");
      this.noTreasureMessage = messagesConfig.configMessage("no_treasure", "&c▶ &fЭту отмычку нельзя вложить в сокровищницу");
      this.onlyKeyMessage = messagesConfig.configMessage("only_key", "&c▶ &fДля открытия необходима отмычка");
   }

   public CustomConfig getShipEventConfig() {
      return this.getCustomConfig("ship.yml");
   }

   @Generated
   public ShipEventConfigManager getEventConfigManager() {
      return this.eventConfigManager;
   }

   @Generated
   public String getNoPermissionMessage() {
      return this.noPermissionMessage;
   }

   @Generated
   public String getNoTreasureMessage() {
      return this.noTreasureMessage;
   }

   @Generated
   public String getOnlyKeyMessage() {
      return this.onlyKeyMessage;
   }
}
