package smartcraft.casegame.timers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.CGPlayer;

import java.util.Map;
import java.util.stream.Collectors;

public class KnockOutTimer extends BukkitRunnable {

  private CaseGame plugin = CaseGame.getInstance();
  private Game game;
  private final int timeOut = plugin.getConfig().getInt("knockOutTimer");
  private int counter = 0;

  public KnockOutTimer(Game game){
    this.game = game;
  }

  @Override
  public void run() {
    if(counter < timeOut){
      for(Map.Entry<String, CGPlayer> entry : game.getPlayers().entrySet()){
        Player p = entry.getValue().getPlayer();
        if(!entry.getValue().isReady()){
          p.setLevel(timeOut - counter);
        } else p.setLevel(0);
      }
      counter++;
    } else {
      this.cancel();
      for(Map.Entry<String, CGPlayer> entry : game.getPlayers().entrySet()){
        if(!entry.getValue().isReady()){
          entry.getValue().leave(false);
          entry.getValue().getPlayer().sendMessage(ChatColor.RED+"Вы не подтвердили свою готовность!");
        }
      }
      game.getPlayers().keySet().removeAll(game.getPlayers().values().stream().filter(player -> !player.isReady()).map(player -> player.getPlayer().getName()).collect(Collectors.toList()));
    }
  }

  public int getTimeOut() {
    return timeOut;
  }
}
