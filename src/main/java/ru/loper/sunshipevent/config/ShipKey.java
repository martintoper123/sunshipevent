package ru.loper.sunshipevent.config;

import lombok.Generated;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.sunlootmanager.SunLootManager;
import ru.loper.sunlootmanager.api.modules.Loot;
import ru.loper.sunshipevent.SunShipEvent;

public class ShipKey {
   private static final NamespacedKey shipItemKey = new NamespacedKey(SunShipEvent.getInstance(), "ship_item");
   private final Loot loot;
   private final ItemBuilder keyBuilder;
   private final boolean treasure;

   public ShipKey(ConfigurationSection section) {
      this.keyBuilder = ItemBuilder.fromConfig(section);
      this.keyBuilder.namespacedKey(shipItemKey, PersistentDataType.STRING, section.getName());
      this.loot = (Loot)SunLootManager.getInstance().getLootManager().getLoot(section.getString("loot")).orElse(null);
      this.treasure = section.getBoolean("treasure", false);
   }

   @Generated
   public Loot getLoot() {
      return this.loot;
   }

   @Generated
   public ItemBuilder getKeyBuilder() {
      return this.keyBuilder;
   }

   @Generated
   public boolean isTreasure() {
      return this.treasure;
   }

   @Generated
   public static NamespacedKey getShipItemKey() {
      return shipItemKey;
   }
}
