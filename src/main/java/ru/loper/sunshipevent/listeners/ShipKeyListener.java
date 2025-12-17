package ru.loper.sunshipevent.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.sunshipevent.config.ShipKey;

public class ShipKeyListener implements Listener {
   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ShipKey.getShipItemKey(), PersistentDataType.STRING)) {
         event.setCancelled(true);
      }
   }
}
