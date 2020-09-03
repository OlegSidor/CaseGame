package smartcraft.casegame.tabCompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

  private CaseGame plugin = CaseGame.getInstance();

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
    List<String> list = new ArrayList<>();

    if(!sender.hasPermission("smartcraf.casegame.admin")) return null;
    //CGA COMMAND
    if (cmd.getName().equalsIgnoreCase("cga")) {
      if (args.length == 1) {

        //SUB COMMANDS
        list.add("create");
        list.add("setLobby");
        list.add("setMaxPlayers");
        list.add("setMaxAirdrops");
        list.add("setArea");
        list.add("setMobArea");
        list.add("setSpawn");
        list.add("setSpawnPeriod");
        list.add("setMaxMobs");
        list.add("setTime");
        list.add("save");
        list.add("select");
        list.add("status");
        list.add("disable");
        list.add("enable");
        //END

        return search(args[0], list);
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("select")) {

          //GET LIST OF GAMES
          File gameDir = new File(plugin.getDataFolder() + "/games");
          if (!gameDir.exists()) return null;
          String contents[] = gameDir.list();
          if(contents == null) return null;

          //TAB SEARCH
          for (int i = 0; i < contents.length; i++) {
            String name = contents[i].substring(0, contents[i].length() - 4);
            if (name.startsWith(args[1])) {
              list.add(name);
            }
          }
          return list;
        } else if (args[0].equalsIgnoreCase("setspawn")) {
          list.add("red");
          list.add("blue");
          return list;
        } else if(args[0].equalsIgnoreCase("disable")){

          list.addAll(plugin.getGames().values().stream().map(game -> {
            if(args[1].equals("")) return game.getName();
            if(game.getName().startsWith(args[1])){
              return game.getName();
            }
            return null;
          }).collect(Collectors.toList()));
          return list;

        } else if(args[0].equalsIgnoreCase("enable")){

          File gameDir = new File(plugin.getDataFolder() + "/games");
          if (!gameDir.exists()) return null;
          String contents[] = gameDir.list();
          if(contents == null) return null;
          list.addAll(Arrays.stream(contents).map(name -> {
            if(args[1].equals("")) return name.substring(0, name.length() - 4);
            if(name.startsWith(args[1])){
              return name.substring(0, name.length() - 4);
            }
            return null;
          }).collect(Collectors.toList()));
          return list;

        }
      }
      //CG COMMAND
    } else if (cmd.getName().equalsIgnoreCase("cg")) {
      if (args.length == 1) {

        //SUB COMMANDS
        list.add("leave");
        list.add("join");
        //END

        return search(args[0], list);
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("join")) {
          List<String> selected = new ArrayList<>();
          for (Map.Entry<String, Game> entry : plugin.getGames().entrySet()) {
            list.add(entry.getKey());
            if(args[1] != null) {
              if(entry.getKey().startsWith(args[1])){
                selected.add(entry.getKey());
              }
            }
          }
          return selected.isEmpty() ? list : selected;
        }
      }
    }
    return null;
  }

  private List<String> search(String cmd, List<String> list) {
    if (cmd != null) {
      List<String> selected = new ArrayList<>();
      for (String name : list) {
        if (name.toLowerCase().startsWith(cmd.toLowerCase())) {
          selected.add(name);
        }
      }
      return selected;
    }
    return list;
  }
}
