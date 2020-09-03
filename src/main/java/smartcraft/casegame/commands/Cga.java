package smartcraft.casegame.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.AirDrop;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.Team;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Cga implements CommandExecutor {

  private CaseGame plugin = CaseGame.getInstance();
  private Game editGame;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String str, @NotNull String[] args) {
    if(!sender.hasPermission("smartcraf.casegame.admin")){
      sender.sendMessage(ChatColor.RED+"У вас нету прав для этого");
      return true;
    }
    if (args.length > 0) {
      //NOT ONLY FOR PLAYER SECTION
      if (args[0].equalsIgnoreCase("help")) {
        return help(sender);
      } else if (args[0].equalsIgnoreCase("reload")) {
        sender.sendMessage(ChatColor.GREEN + "Конфиг перезагружен.");
        return true;
      }
      //----------END---------

      //ONLY FOR PLAYER
      if (sender instanceof Player) {
        Player p = (Player) sender;
        if (args[0].equalsIgnoreCase("create")) {
          if (args.length > 1) {
            if (editGame != null) {
              p.sendMessage(ChatColor.RED + "Сохроните или удалите незавершонную игру!");
              return true;
            } else {
              editGame = new Game(args[1]);
              p.sendMessage(ChatColor.GREEN + "Арена " + args[1] + " создана");
            }
          } else p.sendMessage(ChatColor.GOLD + "/cga create <name>");
        } else if (args[0].equalsIgnoreCase("setlobby")) {
          if (!isEdit(p)) return true;
          editGame.getLobby().setSpawnLocation(p.getLocation());
          p.sendMessage(ChatColor.GREEN + "Лобби установлено");
          return true;
        } else if (args[0].equalsIgnoreCase("setmaxplayers")) {
          if (args.length > 1) {
            if (!isEdit(p)) return true;
            if (Integer.parseInt(args[1]) <= 0) {
              p.sendMessage(ChatColor.RED + "Игроков должно быть > 0");
              return true;
            }
            editGame.setMaxPlayers(Integer.parseInt(args[1]) * 2);
            p.sendMessage(ChatColor.GREEN + "Максимальное количество игроков в команде установлено");
          } else p.sendMessage(ChatColor.GOLD + "/cga setmaxplayers <number> - Максимум игроков в одной команде");
        } else if (args[0].equalsIgnoreCase("setarea")) {
          if (!isEdit(p)) return true;
          try {
            Region region = plugin.getWorldEdit().getSession(p).getSelection(BukkitAdapter.adapt(p.getWorld()));
            if (region == null) {
              p.sendMessage(ChatColor.RED + "Сначала нужно выделить територию");
              return true;
            }
            editGame.getArena().setRegion(region);
            p.sendMessage(ChatColor.GREEN + "Регион установлен");
            return true;
          } catch (IncompleteRegionException e) {
            e.printStackTrace();
            return false;
          }
        } else if (args[0].equalsIgnoreCase("setmobarea")) {
          if (!isEdit(p)) return true;
          try {
            Region region = plugin.getWorldEdit().getSession(p).getSelection(BukkitAdapter.adapt(p.getWorld()));
            if (region == null) {
              p.sendMessage(ChatColor.RED + "Сначала нужно выделить територию");
              return true;
            }
            editGame.getArena().setSpawnRegion(region);
            p.sendMessage(ChatColor.GREEN + "Регион спавна мобов установлен");
            return true;
          } catch (IncompleteRegionException e) {
            e.printStackTrace();
            return false;
          }
        } else if (args[0].equalsIgnoreCase("setTime")) {
          if (!isEdit(p)) return true;
          if (args.length > 1) {
            int time = Integer.parseInt(args[1]);
            if (time <= 0) {
              p.sendMessage(ChatColor.RED + "Время должно быть больше 0 секунд");
              return true;
            }
            editGame.setTime(time);
            p.sendMessage(ChatColor.GREEN + "Время игры установлено!");
            if (time <= 120) {
              p.sendMessage(ChatColor.GOLD + "Желательно устанавливать время больше 120 секунд");
            }
            return true;
          } else p.sendMessage(ChatColor.GOLD + "/cga setTime <number> - Время игры ( в секундах )");
        }
        else if(args[0].equalsIgnoreCase("setMaxAirdrops")){
          if (!isEdit(p)) return true;
          if (args.length > 1) {
            int count = Integer.parseInt(args[1]);
            if (count <= 0) {
              p.sendMessage(ChatColor.RED + "Количество должно быть больше 0");
              return true;
            }
            editGame.getAirDrop().setMaxCount(count);
            p.sendMessage(ChatColor.GREEN+"Максимальное количество аирдропов установлено.");
            return true;
          } else     p.sendMessage(ChatColor.GOLD + "/cga setMaxAirdrops <number> - Максимум аирдропов");
        }
        else if (args[0].equalsIgnoreCase("save")) {
          if (!isEdit(p)) return true;
          if (!editGame.status()) {
            p.sendMessage(ChatColor.RED + "Игра не завершена, проверьте статус");
            return true;
          }

          if (plugin.getGames().containsKey(editGame.getName())) {
            Game current = plugin.getGames().get(editGame.getName());
            if(current.isStarted()){
              current.getPlayers().values().forEach(player -> player.getPlayer().sendMessage(ChatColor.RED+"Игра была остановлена администратором."));
              current.stop();
            }
            plugin.getGames().remove(editGame.getName());
          }

          if (editGame.save()) {
            plugin.getGames().put(editGame.getName(), editGame);
            editGame = null;
            p.sendMessage(ChatColor.GREEN + "Игра сохронена");
          } else {
            p.sendMessage(ChatColor.RED + "Ошибка сохронения!");
          }
          return true;
        } else if (args[0].equalsIgnoreCase("select")) {
          if (args.length > 1) {
            if (editGame != null) {
              p.sendMessage(ChatColor.RED + "Сохроните или удалите незавершонную игру!");
              return true;
            }
            editGame = new Game(args[1]);
            if (editGame.load()) {
              p.sendMessage(ChatColor.GREEN + "Игра успешно загружена");
            } else p.sendMessage(ChatColor.RED + "Не удалось загрузить игру.");
            return true;
          } else p.sendMessage(ChatColor.GOLD + "/cga select <name> - Выбрать арену для редактирования");
          return true;
        } else if (args[0].equalsIgnoreCase("status")) {
          if (!isEdit(p)) return true;
          editGame.status(p);
        } else if (args[0].equalsIgnoreCase("setspawn")) {
          if (!isEdit(p)) return true;
          if (args.length > 2) {
            Team.Teams team = Team.getTeam(args[1]);
            if (team != null) {
              int index = Integer.parseInt(args[2]);
              if (editGame.getMaxPlayers() == 0) {
                p.sendMessage(ChatColor.RED + "Установите максимальное количество игроков.");
                return true;
              }
              if (index <= 0 || index > (editGame.getMaxPlayers() / 2)) {
                p.sendMessage(ChatColor.RED + "0 < number <= " + (int) (editGame.getMaxPlayers() / 2));
                return true;
              }
              index--;
              if (editGame.getArena().setSpawn(team, index, p.getLocation())) {
                p.sendMessage(ChatColor.GREEN + "Точка спавна для игрока под номером " + args[2] + " в команде " + args[1] + " установлена");
                return true;
              }
            } else p.sendMessage(ChatColor.RED + "Команда не найдена!");
          } else p.sendMessage(ChatColor.GOLD + "/cga setspawn red/blue <number> - Место спавна");
          return true;
        } else if (args[0].equalsIgnoreCase("remove")) {
          if (!isEdit(p)) return true;
          if (editGame.remove()) {
            p.sendMessage(ChatColor.GREEN + "Игра успешно удалена!");
            editGame = null;
          } else p.sendMessage(ChatColor.RED + "Ошибка удаления игры!");
        } else if (args[0].equalsIgnoreCase("setSpawnPeriod")) {
          if (!isEdit(p)) return true;
          if (args.length > 1) {
            editGame.getArena().getSpawnTimer().setSpawnPeriod(Integer.parseInt(args[1]));
            p.sendMessage(ChatColor.GREEN+"Период спавна монстров установлен.");
          } else p.sendMessage(ChatColor.GOLD + "/cga setSpawnPeriod <number> - Период спавна мобов");
        } else if (args[0].equalsIgnoreCase("setMaxMobs")) {
          if (!isEdit(p)) return true;
          if (args.length > 1) {
            editGame.getArena().getSpawnTimer().setMaxCount(Integer.parseInt(args[1]));
            p.sendMessage(ChatColor.GREEN+"Максимальное количество мобов установлено.");
          } else p.sendMessage(ChatColor.GOLD + "/cga setMaxMobs <number> - Максимальное количество мобов на арене");
        } else if (args[0].equalsIgnoreCase("disable")) {
          if (args.length > 1) {
            if (plugin.getGames().containsKey(args[1])) {
              Game game = plugin.getGames().get(args[1]);
              if (editGame != null && editGame.getName().equals(args[1])) {
                game = editGame;
              }

              game.stop();
              game.setDisabled(true);
              game.save();
              plugin.getGames().remove(args[1]);
              p.sendMessage(ChatColor.GREEN + "Игра отключена");
            } else p.sendMessage(ChatColor.RED + "Игра не найдена");
          } else p.sendMessage(ChatColor.GOLD + "/cga disable <name> - отключить игру");
        } else if (args[0].equalsIgnoreCase("enable")) {
          if (args.length > 1) {
            Game game = new Game(args[1]);
            if (editGame != null && editGame.getName().equals(args[1])) {
              game = editGame;
            }

            if (game.load()) {
              game.setDisabled(false);
              game.save();
              plugin.getGames().put(game.getName(), game);
              p.sendMessage(ChatColor.GREEN + "Игра включена");
            } else p.sendMessage(ChatColor.RED + "Не удалось загрузить игру");
            return true;
          } else p.sendMessage(ChatColor.GOLD + "/cga enable <name> - включить игру");
        } else if (args[0].equalsIgnoreCase("test")) {
          plugin.getGames().get("test").getAirDrop().spawn(p.getLocation(), "medium");
          return true;
        }
      } else sender.sendMessage(ChatColor.RED + "Only for players");
      //------END-----

    } else help(sender);
    return true;
  }


  private boolean help(CommandSender p) {
    p.sendMessage(ChatColor.GOLD + "/cga create <name> - Создать игру");
    p.sendMessage(ChatColor.GOLD + "/cga setLobby - Установить лобби");
    p.sendMessage(ChatColor.GOLD + "/cga setMaxPlayers <number> - Максимум игроков в одной команде");
    p.sendMessage(ChatColor.GOLD + "/cga setMaxAirdrops <number> - Максимум аирдропов");
    p.sendMessage(ChatColor.GOLD + "/cga setArea - Граници арены");
    p.sendMessage(ChatColor.GOLD + "/cga setTime <number> - Время игры ( в секундах )");
    p.sendMessage(ChatColor.GOLD + "/cga setMobArea - Границы спавна монстров");
    p.sendMessage(ChatColor.GOLD + "/cga setSpawn team1/team2 <number> - Место спавна");
    p.sendMessage(ChatColor.GOLD + "/cga setSpawnPeriod <number> - Период спавна мобов");
    p.sendMessage(ChatColor.GOLD + "/cga setMaxMobs <number> - Максимальное количество мобов на арене");
    p.sendMessage(ChatColor.GOLD + "/cga save - сохронить игру");
    p.sendMessage(ChatColor.GOLD + "/cga select <name> - Выбрать арену для редактирования");
    p.sendMessage(ChatColor.GOLD + "/cga status - Выдаст значения игры");
    p.sendMessage(ChatColor.GOLD + "/cga disable <name> - отключить игру");
    p.sendMessage(ChatColor.GOLD + "/cga enable <name> - включить игру");

    return true;
  }

  private boolean isEdit(Player p) {
    if (editGame == null) {
      p.sendMessage(ChatColor.RED + "Сначала нужно создать игру или выбрать существующую");
      return false;
    }
    return true;
  }

}
