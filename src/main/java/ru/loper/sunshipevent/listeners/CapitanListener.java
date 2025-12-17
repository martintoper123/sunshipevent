package ru.loper.sunshipevent.listeners;

import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.npc.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.loper.sunshipevent.event.npc.ShipCapitan;

public class CapitanListener implements Listener {
   @EventHandler
   public void onNpcInteract(NpcInteractEvent event) {
      Npc npc = event.getNpc();
      Player player = event.getPlayer();
      ShipCapitan shipCapitan = ShipCapitan.getShipCapitan(npc.getUuid());
      if (shipCapitan != null) {
         if (!shipCapitan.isTakeKey()) {
            this.pushPlayer(player);
         } else {
            ItemStack itemStack = shipCapitan.getCurrentKey();
            if (itemStack != null) {
               player.getInventory().addItem(new ItemStack[]{itemStack});
               shipCapitan.setTakeKey(false);
            }
         }
      }
   }

   private void pushPlayer(Player player) {
      Vector direction = player.getLocation().getDirection().multiply(-1);
      direction.setY(0.2);
      player.setVelocity(direction.normalize().multiply(3));
   }
}
