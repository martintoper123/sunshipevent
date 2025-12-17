package ru.loper.sunshipevent.event.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityPropertyRegistry;
import lol.pyr.znpcsplus.api.npc.Npc;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;
import lol.pyr.znpcsplus.api.npc.NpcTypeRegistry;
import lol.pyr.znpcsplus.util.LookType;
import lol.pyr.znpcsplus.util.NpcLocation;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.loper.sunshipevent.event.ShipEvent;
import ru.loper.sunshipevent.event.rarity.ShipEventRarity;

public class ShipCapitan {
   private static final Map<UUID, ShipCapitan> CAPITANs = new HashMap<>();
   private final ShipEvent event;
   private final NpcRegistry npcRegistry;
   private final NpcTypeRegistry npcTypeRegistry;
   private final EntityPropertyRegistry entityPropertyRegistry;
   private ItemStack currentKey;
   private Npc npc;
   private boolean takeKey;

   public ShipCapitan(ShipEvent event) {
      this.event = event;
      this.npcRegistry = NpcApiProvider.get().getNpcRegistry();
      this.npcTypeRegistry = NpcApiProvider.get().getNpcTypeRegistry();
      this.entityPropertyRegistry = NpcApiProvider.get().getPropertyRegistry();
   }

   public static ShipCapitan getShipCapitan(UUID uuid) {
      return CAPITANs.get(uuid);
   }

   public void spawnNpc() {
      ShipEventRarity eventRarity = this.event.getEventRarity();
      Location location = this.event.getEventLocation().clone().add(eventRarity.getNpcLocationOffset());
      NpcEntry npcEntry = this.npcRegistry
         .create(this.getNpcUuid(), location.getWorld(), this.npcTypeRegistry.getByName("wandering_trader"), new NpcLocation(location));
      this.npc = npcEntry.getNpc();
      this.npc.setProperty(this.entityPropertyRegistry.getByName("look", LookType.class), LookType.PER_PLAYER);
      this.npc.setProperty(this.entityPropertyRegistry.getByName("look_distance", Double.class), 48.0);
      this.setTakeKey(true);
      npcEntry.setProcessed(true);
      CAPITANs.put(this.npc.getUuid(), this);
   }

   public void remove() {
      this.npcRegistry.delete(this.getNpcUuid());
   }

   private String getNpcUuid() {
      return this.event.getId() + this.event.getUuid();
   }

   public void setTakeKey(boolean takeKey) {
      if (takeKey) {
         this.takeKey = true;
         this.updateHologram(this.event.getEventRarity().getNpcTakeHolograms());
         this.currentKey = this.event.getEventRarity().getRandomKey().build();
         this.npc.setProperty(this.entityPropertyRegistry.getByName("hand", ItemStack.class), this.currentKey);
      } else {
         this.takeKey = false;
         this.updateHologram(this.event.getEventRarity().getNpcWaitHolograms());
         this.currentKey = null;
         this.npc.setProperty(this.entityPropertyRegistry.getByName("hand", ItemStack.class), new ItemStack(Material.AIR));
      }
   }

   private void updateHologram(List<String> hologram) {
      if (this.npc != null) {
         this.npc.getHologram().clearLines();
         hologram.forEach(this.npc.getHologram()::addLine);
      }
   }

   @Generated
   public ShipEvent getEvent() {
      return this.event;
   }

   @Generated
   public NpcRegistry getNpcRegistry() {
      return this.npcRegistry;
   }

   @Generated
   public NpcTypeRegistry getNpcTypeRegistry() {
      return this.npcTypeRegistry;
   }

   @Generated
   public EntityPropertyRegistry getEntityPropertyRegistry() {
      return this.entityPropertyRegistry;
   }

   @Generated
   public ItemStack getCurrentKey() {
      return this.currentKey;
   }

   @Generated
   public Npc getNpc() {
      return this.npc;
   }

   @Generated
   public boolean isTakeKey() {
      return this.takeKey;
   }
}
