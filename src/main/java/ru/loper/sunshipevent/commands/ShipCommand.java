package ru.loper.sunshipevent.commands;

import org.bukkit.permissions.Permission;
import ru.loper.suncore.api.command.AdvancedSmartCommandExecutor;
import ru.loper.sunshipevent.SunShipEvent;
import ru.loper.sunshipevent.commands.impl.GiveKeyCommand;
import ru.loper.sunshipevent.config.ShipConfigManager;

public class ShipCommand extends AdvancedSmartCommandExecutor {
   private final ShipConfigManager configManager;

   public ShipCommand(SunShipEvent plugin) {
      this.configManager = plugin.getConfigManager();
      this.addSubCommand(new GiveKeyCommand(this.configManager), new Permission("sunshipevent.command.give"), "give", new String[0]);
   }

   public String getDontPermissionMessage() {
      return this.configManager.getNoPermissionMessage();
   }
}
