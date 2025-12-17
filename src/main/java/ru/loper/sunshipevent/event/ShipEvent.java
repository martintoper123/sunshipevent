package ru.loper.sunshipevent.event;

import org.bukkit.Material;
import ru.loper.suneventmanager.api.modules.event.Event;
import ru.loper.suneventmanager.api.modules.event.EventState;
import ru.loper.sunshipevent.SunShipEvent;
import ru.loper.sunshipevent.event.npc.ShipCapitan;
import ru.loper.sunshipevent.event.rarity.ShipEventRarity;

public class ShipEvent extends Event {
   private final ShipCapitan shipCapitan = new ShipCapitan(this);
   private int respawnTimer = this.getEventRarity().getRespawnTime();
   private int stayRespawns = this.getEventRarity().getRespawnCount();

   public ShipEvent() {
      super("ship", SunShipEvent.getInstance().getConfigManager().getEventConfigManager());
   }

   public void eventTimerProgress() {
      this.eventTimer--;
      if (this.eventTimer <= 0) {
         this.updateState(this.currentState);
      }

      if (this.currentState.equals(EventState.STARTED) && this.stayRespawns > 0) {
         this.respawnTimer--;
         if (this.respawnTimer <= 0) {
            this.respawnKey();
         }
      }

      if (this.currentState.equals(EventState.STARTED) || this.currentState.equals(EventState.STOPPING)) {
         this.eventRarity.getChestsManager().updateHolograms(this);
      }
   }

   public void activate() {
      super.activate();
      this.schematicManager.setSearchMaterials(new Material[]{this.eventRarity.getChestsManager().getChestMaterial()});
   }

   public void prestart() {
   }

   public void updateStarted() {
      this.eventTimer = this.configManager.getEventStopTime();
      this.currentState = EventState.STOPPING;
      this.open();
   }

   public void start() {
      this.shipCapitan.spawnNpc();
   }

   public void open() {
   }

   public void remove() {
      if (this.shipCapitan != null) {
         this.shipCapitan.remove();
      }
   }

   private void respawnKey() {
      this.respawnTimer = this.getEventRarity().getRespawnTime();
      this.shipCapitan.setTakeKey(true);
      this.stayRespawns--;
   }

   public ShipEventRarity getEventRarity() {
      return (ShipEventRarity)this.eventRarity;
   }
}
