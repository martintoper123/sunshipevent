package ru.loper.sunshipevent.event.rarity;

import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import ru.loper.suneventmanager.SunEventManager;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarityManager;
import ru.loper.sunlootmanager.SunLootManager;
import ru.loper.sunshipevent.config.ShipEventConfigManager;

public class ShipEventRarityManager extends EventRarityManager {
   private final ShipEventConfigManager configManager;

   public ShipEventRarityManager(ShipEventConfigManager configManager, Plugin plugin) {
      super(configManager.getEventConfig(), plugin);
      this.configManager = configManager;
   }

   protected void loadRarities() {
      ConfigurationSection raresSection = this.config.getConfig().getConfigurationSection("rarities");
      if (raresSection == null) {
         this.getPlugin().getLogger().severe("Раздел 'rarities' не найден в конфиге!");
      } else {
         for (String rarityKey : raresSection.getKeys(false)) {
            ConfigurationSection raritySection = raresSection.getConfigurationSection(rarityKey);
            if (raritySection != null) {
               Bukkit.getScheduler().runTaskLater(SunEventManager.getInstance(), () -> this.loadRarity(rarityKey, raritySection), 1L);
            }
         }
      }
   }

   private void loadRarity(String rarityKey, ConfigurationSection raritySection) {
      this.rarities.put(rarityKey.toLowerCase(), new ShipEventRarity(raritySection, SunLootManager.getInstance().getLootManager(), this.configManager));
   }

   @Generated
   public ShipEventConfigManager getConfigManager() {
      return this.configManager;
   }
}
