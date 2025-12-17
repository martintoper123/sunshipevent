package ru.loper.sunshipevent.event.rarity.chests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suneventmanager.api.modules.chest.Chest;
import ru.loper.suneventmanager.api.modules.chest.ChestsManager;
import ru.loper.suneventmanager.api.modules.event.Event;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarity;
import ru.loper.suneventmanager.utils.LocationUtils;

public class ShipChestsManager extends ChestsManager {
   protected final Set<Vector> treasureOffsets = new HashSet<>();
   protected String treasureTitle;
   protected Material treasureMaterial;
   protected int treasureSize;
   protected int minTreasureItems;
   protected int maxTreasureItems;

   public ShipChestsManager(EventRarity eventRarity) {
      super(eventRarity);
      this.loadTreasureValues();
   }

   protected void loadChestValues() {
      ConfigurationSection chestsSection = this.eventRarity.getChestsSection();
      if (chestsSection != null) {
         this.chestTitle = Colorize.parse(chestsSection.getString("title", ""));
         this.chestSize = chestsSection.getInt("rows", 3) * 9;
         this.chestMaterial = this.getMaterialOrDefault(chestsSection.getString("material", "BLUE_SHULKER_BOX"));
         this.minItems = chestsSection.getInt("items.min", 1);
         this.maxItems = chestsSection.getInt("items.max", this.chestSize);
      } else {
         this.chestTitle = "";
         this.chestSize = 27;
         this.chestMaterial = Material.BLUE_SHULKER_BOX;
         this.minItems = 1;
         this.maxItems = this.chestSize;
      }
   }

   protected void loadTreasureValues() {
      ConfigurationSection treasuresSection = this.eventRarity.getRareSection().getConfigurationSection("treasures");
      if (treasuresSection != null) {
         this.treasureTitle = Colorize.parse(treasuresSection.getString("title", ""));
         this.treasureSize = treasuresSection.getInt("rows", 3) * 9;
         this.treasureMaterial = this.getMaterialOrDefault(treasuresSection.getString("material", "RESPAWN_ANCHOR"));
         this.minTreasureItems = treasuresSection.getInt("items.min", 1);
         this.maxTreasureItems = treasuresSection.getInt("items.max", this.chestSize);
         this.loadTreasureOffsets(treasuresSection);
      } else {
         this.treasureTitle = "";
         this.treasureSize = 27;
         this.treasureMaterial = Material.BLUE_SHULKER_BOX;
         this.minTreasureItems = 1;
         this.maxTreasureItems = this.chestSize;
      }
   }

   protected void loadTreasureOffsets(ConfigurationSection chestsSection) {
      ConfigurationSection offsetsSection = chestsSection.getConfigurationSection("offsets");
      if (offsetsSection != null) {
         for (String key : offsetsSection.getKeys(false)) {
            ConfigurationSection offsetSection = offsetsSection.getConfigurationSection(key);
            if (offsetSection != null) {
               this.treasureOffsets.add(LocationUtils.getVectorFromSection(offsetSection));
            }
         }
      }
   }

   public void loadChests(Event event) {
      Location eventLocation = event.getEventLocation();
      if (eventLocation != null) {
         List<Location> chestLocations = event.getSchematicManager().getLocationsByMaterial(this.chestMaterial);
         Set<Chest> chests = new HashSet<>();

         for (Location chestLocation : chestLocations) {
            ShipChest chest = new ShipChest(this, chestLocation, event);
            chest.spawn();
            chests.add(chest);
         }

         for (Vector chestOffset : this.treasureOffsets) {
            Location chestLocation = eventLocation.clone().add(chestOffset);
            TreasureChest chest = new TreasureChest(this, chestLocation, event);
            chest.spawn();
            chests.add(chest);
         }

         this.eventChests.put(event, chests);
      }
   }
}
