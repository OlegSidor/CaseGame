package smartcraft.casegame.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.CGPlayer;

public class Cg implements CommandExecutor {

  private CaseGame plugin = CaseGame.getInstance();

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String str, @NotNull String[] args) {
    if ((sender instanceof Player)) {
      Player p = ((Player) sender).getPlayer();
      if(args.length > 0){
        if(args[0].equalsIgnoreCase("join")){
          if(args.length > 1){
            if(plugin.getGames().containsKey(args[1])) {
              if(!p.hasMetadata("CaseGame")) {
                Game game = plugin.getGames().get(args[1]);
                game.join(p);
              } else p.sendMessage(ChatColor.RED+"Вы уже находитеть в игре!");
            } else p.sendMessage(ChatColor.RED+"Арена не найдена!");
          } else p.sendMessage(ChatColor.GOLD+"/cg join (Имя арены) - вход в игру");
        } else if(args[0].equalsIgnoreCase("leave")){
          if(p.hasMetadata("CaseGame")) {
            String name = p.getMetadata("CaseGame").get(0).asString();
            if(plugin.getGames().containsKey(name)) {
              Game game = plugin.getGames().get(name);
              if(game.getPlayers().containsKey(p.getName())){
                CGPlayer player = game.getPlayers().get(p.getName());
                player.leave();
              } else p.sendMessage(ChatColor.RED+"Вы не находитесь в игре!");
            } else p.sendMessage(ChatColor.RED+"Произошла ошибка при выходе");
          } else p.sendMessage(ChatColor.RED+"Вы не находитесь в игре!");
        }
      } else return help(p);
    } else sender.sendMessage(ChatColor.RED + "Only for Players");
    return true;
  }

  private boolean help(Player p) {
    p.sendMessage(ChatColor.GOLD+"Используйте: ");
    p.sendMessage(ChatColor.GOLD+"/cg leave - выход из игры");
    p.sendMessage(ChatColor.GOLD+"/cg join (Имя арены) - вход в игру");
    return true;
  }
}
