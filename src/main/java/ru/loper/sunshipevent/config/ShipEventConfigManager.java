package ru.loper.sunshipevent.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suneventmanager.config.EventConfigManager;
import ru.loper.sunshipevent.event.rarity.ShipEventRarityManager;

public class ShipEventConfigManager extends EventConfigManager {
   private final Map<String, ShipKey> shipKeys;

   public ShipEventConfigManager(CustomConfig eventConfig, Plugin plugin) {
      super(eventConfig, plugin);
      this.rarityManager = new ShipEventRarityManager(this, plugin);
      this.shipKeys = new HashMap<>();
      this.loadShipItems();
   }

   private void loadShipItems() {
      ConfigurationSection keysSection = this.eventConfig.getConfig().getConfigurationSection("keys");
      if (keysSection != null) {
         for (String key : keysSection.getKeys(false)) {
            ConfigurationSection keySection = keysSection.getConfigurationSection(key);
            if (keySection != null) {
               this.shipKeys.put(key, new ShipKey(keySection));
            }
         }
      }
   }

   public ShipKey getShipKey(ItemStack itemStack) {
      if (itemStack != null
         && itemStack.hasItemMeta()
         && itemStack.getItemMeta().getPersistentDataContainer().has(ShipKey.getShipItemKey(), PersistentDataType.STRING)) {
         String name = (String)itemStack.getItemMeta().getPersistentDataContainer().get(ShipKey.getShipItemKey(), PersistentDataType.STRING);
         return this.shipKeys.get(name);
      } else {
         return null;
      }
   }

   public ShipKey getShipKey(String name) {
      return this.shipKeys.get(name);
   }

   @Generated
   public Map<String, ShipKey> getShipKeys() {
      return this.shipKeys;
   }
}
