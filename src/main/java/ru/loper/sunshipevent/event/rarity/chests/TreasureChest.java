package ru.loper.sunshipevent.event.rarity.chests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suneventmanager.SunEventManager;
import ru.loper.suneventmanager.api.customblockdata.CustomBlockData;
import ru.loper.suneventmanager.api.modules.chest.Chest;
import ru.loper.suneventmanager.api.modules.chest.ChestsManager;
import ru.loper.suneventmanager.api.modules.chest.runnable.ChestFillRunnable;
import ru.loper.suneventmanager.api.modules.event.Event;
import ru.loper.suneventmanager.hook.HologramsHook;
import ru.loper.suneventmanager.utils.TimeUtils;
import ru.loper.sunshipevent.SunShipEvent;
import ru.loper.sunshipevent.config.ShipKey;

public class TreasureChest extends Chest {
   private int openTimer = 0;
   private int usedKeys = 0;
   private boolean activated = false;

   public TreasureChest(ChestsManager chestsManager, Location location, Event event) {
      super(chestsManager, location, event);
   }

   public void updateHologram() {
      if (this.activated && !this.open) {
         this.openTimer--;
         if (this.openTimer <= 0) {
            this.open();
         }
      }

      if (this.event.getEventRarity().isHologramSpawn()) {
         List<String> lines = this.open
            ? this.chestsManager.getEventRarity().getRareSection().getStringList("holograms.open")
            : (this.activated ? this.chestsManager.getEventRarity().getHologramStop() : this.chestsManager.getEventRarity().getHologramStart());
         if (!lines.isEmpty()) {
            this.updateHologram(lines);
         }
      }
   }

   @Override
   protected void updateHologram(List<String> lines) {
      List<String> formattedLines = lines.stream()
         .map(line -> line.replace("{time}", TimeUtils.formatSeconds(this.openTimer)).replace("{used_keys}", String.valueOf(this.usedKeys)))
         .collect(Collectors.toList());
      HologramsHook.createOrUpdateHologram(formattedLines, this.hologramLocation, this.hologramName);
   }

   @Override
   public void onInteract(PlayerInteractEvent event) {
      event.setCancelled(true);
      Player player = event.getPlayer();
      ItemStack itemInHand = player.getInventory().getItemInMainHand();
      if (this.open) {
         this.onPlayerOpen(player);
      } else {
         ShipKey shipKey = SunShipEvent.getInstance().getConfigManager().getEventConfigManager().getShipKey(itemInHand);
         if (shipKey != null && !this.activated) {
            if (!shipKey.isTreasure()) {
               player.sendMessage(SunShipEvent.getInstance().getConfigManager().getNoTreasureMessage());
            } else {
               this.usedKeys++;
               itemInHand.setAmount(itemInHand.getAmount() - 1);
               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
               if (this.usedKeys >= 4 && !this.activated) {
                  this.activateChest();
               }
            }
         }
      }
   }

   private void activateChest() {
      this.openTimer = 60;
      this.activated = true;
   }

   @Override
   public void open() {
      if (!this.open) {
         this.open = true;
         List<ItemStack> lootItems = this.chestsManager.getRandomItems();
         if (lootItems.isEmpty()) {
            this.filled = true;
         } else {
            new ChestFillRunnable(lootItems, this).runTaskTimer(SunEventManager.getInstance(), 1L, 1L);
         }
      }
   }

   @Override
   public void onPlayerOpen(Player player) {
      super.onPlayerOpen(player);
      player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
   }

   public void close() {
      if (this.open) {
         this.open = false;
         this.filled = false;
         HologramsHook.remove(this.hologramName);

         // ✅ FIX: типизированная копия viewers, без raw ArrayList
         new ArrayList<HumanEntity>(this.inventory.getViewers()).forEach(HumanEntity::closeInventory);

         this.inventory.clear();
      }
   }

   @Override
   public void spawn() {
      Block block = this.location.getBlock();
      CustomBlockData customBlockData = new CustomBlockData(block, SunEventManager.getInstance());
      customBlockData.set(CHEST_UUID_KEY, PersistentDataType.STRING, this.getChestId());
   }

   @Override
   public void remove() {
      this.close();
      super.remove();
   }
}
