package ru.loper.sunshipevent.commands.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.utils.Colorize;
import ru.loper.sunshipevent.config.ShipConfigManager;
import ru.loper.sunshipevent.config.ShipKey;

public class GiveKeyCommand implements SubCommand {
   private final ShipConfigManager configManager;

   public void onCommand(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /ship give [key] [player] [amount]"));
      } else {
         ShipKey shipKey = this.configManager.getEventConfigManager().getShipKey(args[1]);
         if (shipKey == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДанного ключа не существует"));
         } else {
            Player player = this.resolveTargetPlayer(sender, args);
            if (player != null) {
               int amount = this.resolveAmount(args);
               if (amount <= 0) {
                  sender.sendMessage(Colorize.parse("&c ▶ &fНекорректное количество предметов"));
               }

               ItemBuilder itemBuilder = shipKey.getKeyBuilder();
               ItemStack item = itemBuilder.build();
               item.setAmount(amount);
               player.getInventory().addItem(new ItemStack[]{item});
               sender.sendMessage(Colorize.parse(String.format("&a ▶ &fВыдан ключ &e%s &fигроку &e%s", itemBuilder.name(), player.getName())));
            }
         }
      }
   }

   private Player resolveTargetPlayer(CommandSender sender, String[] args) {
      if (args.length < 3) {
         if (!(sender instanceof Player)) {
            sender.sendMessage(Colorize.parse("&cДанная команда доступна только игрокам"));
            return null;
         } else {
            return (Player)sender;
         }
      } else {
         Player player = Bukkit.getPlayer(args[2]);
         if (player == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fУказанный игрок не найден или не в сети"));
            return null;
         } else {
            return player;
         }
      }
   }

   private int resolveAmount(String[] args) {
      if (args.length < 4) {
         return 1;
      } else {
         try {
            return Math.max(1, Integer.parseInt(args[3]));
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   public List<String> onTabCompleter(CommandSender commandSender, String[] args) {
      if (args.length == 2) {
         return this.configManager
            .getEventConfigManager()
            .getShipKeys()
            .keySet()
            .stream()
            .filter(line -> line.toLowerCase().startsWith(args[1].toLowerCase()))
            .collect(Collectors.toList());
      } else {
         return args.length == 3
            ? Bukkit.getOnlinePlayers()
               .stream()
               .<String>map(HumanEntity::getName)
               .filter(line -> line.toLowerCase().startsWith(args[1].toLowerCase()))
               .collect(Collectors.toList())
            : Collections.emptyList();
      }
   }

   @Generated
   public GiveKeyCommand(ShipConfigManager configManager) {
      this.configManager = configManager;
   }
}
