package smartcraft.casegame.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.CGPlayer;
import smartcraft.casegame.timers.KnockOutTimer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Lobby {

  private CaseGame plugin = CaseGame.getInstance();
  private Game game;
  private Location spawnLocation;
  private KnockOutTimer knockOutTimer;

  public Lobby(Game game) {
    this.game = game;
  }

  public Location getSpawnLocation() {
    return spawnLocation;
  }

  public void setSpawnLocation(Location spawnLocation) {
    this.spawnLocation = spawnLocation;
  }

  public boolean isFull() {
    return game.getPlayers().size() >= game.getMaxPlayers();
  }

  public boolean isAllReady() {
    for (Map.Entry<String, CGPlayer> entry : game.getPlayers().entrySet()) {
      if (!entry.getValue().isReady()) return false;
    }
    return true;
  }

  public void startKnockOut() {
    knockOutTimer = new KnockOutTimer(game);
    knockOutTimer.runTaskTimer(plugin, 0, 20);

    for (Map.Entry<String, CGPlayer> entry : game.getPlayers().entrySet()) {
      if (!entry.getValue().isReady()) {
        Player p = entry.getValue().getPlayer();
        p.sendMessage(ChatColor.RED + "Подтвердите свою готовность или вы будете изгнаны в течении " + knockOutTimer.getTimeOut() + " секунд!");
      }
    }

  }

  public void stopKnockOut() {
    knockOutTimer.cancel();
    for (Map.Entry<String, CGPlayer> entry : game.getPlayers().entrySet())
      entry.getValue().getPlayer().setLevel(0);
  }

  public boolean isKnockOut() {
    return knockOutTimer != null && !knockOutTimer.isCancelled();
  }
}
