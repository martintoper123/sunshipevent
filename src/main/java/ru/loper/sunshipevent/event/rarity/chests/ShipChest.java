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

public class ShipChest extends Chest {
   private int closeTimer = 0;

   public ShipChest(ChestsManager chestsManager, Location location, Event event) {
      super(chestsManager, location, event);
   }

   public void updateHologram() {
      if (this.open) {
         this.closeTimer--;
         if (this.closeTimer <= 0) {
            this.close();
         } else if (this.event.getEventRarity().isHologramSpawn()) {
            List<String> lines = this.chestsManager.getEventRarity().getChestsSection().getStringList("remove_hologram");
            if (!lines.isEmpty()) {
               this.updateHologram(lines);
            }
         }
      }
   }

   protected void updateHologram(List<String> lines) {
      List<String> formattedLines = lines.stream().map(line -> line.replace("{time}", TimeUtils.formatSeconds(this.closeTimer))).collect(Collectors.toList());
      HologramsHook.createOrUpdateHologram(formattedLines, this.hologramLocation, this.hologramName);
   }

   public void open() {
   }

   public void open(ShipKey shipKey) {
      if (!this.open) {
         this.open = true;
         this.closeTimer = 60;
         List<ItemStack> lootItems = (List<ItemStack>)(shipKey.getLoot() == null
            ? new ArrayList<>()
            : shipKey.getLoot().generateLoot(this.chestsManager.getGenerateItemsCount()));
         if (lootItems.isEmpty()) {
            this.filled = true;
         } else {
            new ChestFillRunnable(lootItems, this).runTaskTimer(SunEventManager.getInstance(), 1L, 1L);
         }
      }
   }

   public void onInteract(PlayerInteractEvent event) {
      event.setCancelled(true);
      Player player = event.getPlayer();
      ItemStack itemInHand = player.getInventory().getItemInMainHand();
      if (this.open) {
         this.onPlayerOpen(event.getPlayer());
      } else {
         ShipKey shipKey = SunShipEvent.getInstance().getConfigManager().getEventConfigManager().getShipKey(itemInHand);
         if (shipKey == null) {
            player.sendMessage(SunShipEvent.getInstance().getConfigManager().getOnlyKeyMessage());
         } else {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            this.open(shipKey);
            this.onPlayerOpen(player);
         }
      }
   }

   public void onPlayerOpen(Player player) {
      super.onPlayerOpen(player);
      player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0F, 1.0F);
   }

   public void close() {
      if (this.open) {
         this.open = false;
         this.filled = false;
         this.closeTimer = 0;
         HologramsHook.remove(this.hologramName);

         for (HumanEntity humanEntity : this.inventory.getViewers()) {
            if (!(humanEntity instanceof Player player)) {
               return;
            }

            player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1.0F, 1.0F);
            player.closeInventory();
         }

         this.inventory.clear();
      }
   }

   public void spawn() {
      Block block = this.location.getBlock();
      CustomBlockData customBlockData = new CustomBlockData(block, SunEventManager.getInstance());
      customBlockData.set(CHEST_UUID_KEY, PersistentDataType.STRING, this.getChestId());
   }

   public void remove() {
      this.close();
      super.remove();
   }
}
