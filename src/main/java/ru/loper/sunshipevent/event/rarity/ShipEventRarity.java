package ru.loper.sunshipevent.event.rarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarity;
import ru.loper.suneventmanager.utils.LocationUtils;
import ru.loper.sunlootmanager.api.manager.LootManager;
import ru.loper.sunshipevent.config.ShipEventConfigManager;
import ru.loper.sunshipevent.event.rarity.chests.ShipChestsManager;

public class ShipEventRarity extends EventRarity {
   private final ShipEventConfigManager configManager;
   private final List<String> npcTakeHolograms;
   private final List<String> npcWaitHolograms;
   private final Map<String, Integer> keyChances;
   private final int respawnTime;
   private final int respawnCount;
   private final Vector npcLocationOffset;

   public ShipEventRarity(ConfigurationSection rareSection, @NotNull LootManager lootManager, @NotNull ShipEventConfigManager configManager) {
      super(rareSection, lootManager, configManager.getEventConfig());
      this.configManager = configManager;
      this.npcTakeHolograms = rareSection.getStringList("npc.hologram.take");
      this.npcWaitHolograms = rareSection.getStringList("npc.hologram.wait");
      this.respawnTime = rareSection.getInt("npc.respawn.time");
      this.respawnCount = rareSection.getInt("npc.respawn.count");
      this.npcLocationOffset = LocationUtils.getVectorFromSection(rareSection.getConfigurationSection("npc.location_offset"));
      this.keyChances = new HashMap<>();
      this.loadKeyChances();
      this.chestsManager = new ShipChestsManager(this);
   }

   private void loadKeyChances() {
      ConfigurationSection keyChancesSection = this.rareSection.getConfigurationSection("key_chances");
      if (keyChancesSection != null) {
         for (String key : keyChancesSection.getKeys(false)) {
            if (this.configManager.getShipKeys().containsKey(key)) {
               this.keyChances.put(key, keyChancesSection.getInt(key));
            }
         }
      }
   }

   public ItemBuilder getRandomKey() {
      if (this.keyChances.isEmpty()) {
         return null;
      } else {
         int totalChance = this.keyChances.values().stream().mapToInt(Integer::intValue).sum();
         int randomValue = ThreadLocalRandom.current().nextInt(0, totalChance + 1);
         int currentSum = 0;

         for (Entry<String, Integer> entry : this.keyChances.entrySet()) {
            currentSum += entry.getValue();
            if (randomValue <= currentSum) {
               return this.configManager.getShipKeys().get(entry.getKey()).getKeyBuilder();
            }
         }

         return this.configManager.getShipKeys().get(this.keyChances.keySet().iterator().next()).getKeyBuilder();
      }
   }

   @Generated
   public ShipEventConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public List<String> getNpcTakeHolograms() {
      return this.npcTakeHolograms;
   }

   @Generated
   public List<String> getNpcWaitHolograms() {
      return this.npcWaitHolograms;
   }

   @Generated
   public Map<String, Integer> getKeyChances() {
      return this.keyChances;
   }

   @Generated
   public int getRespawnTime() {
      return this.respawnTime;
   }

   @Generated
   public int getRespawnCount() {
      return this.respawnCount;
   }

   @Generated
   public Vector getNpcLocationOffset() {
      return this.npcLocationOffset;
   }
}
