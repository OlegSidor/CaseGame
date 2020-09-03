package smartcraft.casegame.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.Tools.TemporaryBlock;
import smartcraft.casegame.inGameEntity.CGPlayer;
import smartcraft.casegame.inGameEntity.Team;
import smartcraft.casegame.timers.GamePassing;

import java.util.*;

public class Game {
  private String name;
  private int maxPlayers;
  private int time;
  private Lobby lobby = new Lobby(this);
  private Arena arena = new Arena(this);
  private CGScoreboard scoreboard = new CGScoreboard();
  private Map<String, CGPlayer> players = new HashMap<>();
  private Map<String, Kit> kits = new HashMap<>();
  private Map<Team.Teams, Team> teams = new HashMap<>();
  private Map<Integer, List<String>> prize = new HashMap<>();
  private CaseGame plugin = CaseGame.getInstance();
  private boolean started = false;
  private boolean disabled = false;
  private TemporaryBlock temporaryBlock = new TemporaryBlock();
  private GamePassing gamePassing;
  private AirDrop airDrop;

  public Game(String name) {
    this.name = name;
    airDrop = new AirDrop(this);
  }

  public boolean status() {
    if (time == 0) return false;
    if (maxPlayers == 0) return false;
    if (lobby.getSpawnLocation() == null) return false;
    if (arena.getRegionMax() == null || arena.getRegionMin() == null) return false;
    if (arena.getSpawnRegionMax() == null || arena.getSpawnRegionMin() == null) return false;

    if (!arena.getSpawns().isEmpty()) {
      for (Map.Entry<Team.Teams, List<Location>> entry : arena.getSpawns().entrySet()) {
        for (int i = 1; i <= entry.getValue().size(); i++) {
          if (entry.getValue().get(i - 1) == null) return false;
        }
      }
    } else return false;

    return true;
  }

  public void status(Player p) {
    p.sendMessage(ChatColor.GREEN + "Name: " + name);
    p.sendMessage((time > 0 ? ChatColor.GREEN : ChatColor.RED) + "Time: " + time);
    p.sendMessage((disabled ? ChatColor.RED : ChatColor.GREEN) + "Disabled: " + disabled);
    p.sendMessage(ChatColor.GREEN + "SpawnPeriod: " + arena.getSpawnTimer().getSpawnPeriod());
    p.sendMessage(ChatColor.GREEN + "MaxMobCount: " + arena.getSpawnTimer().getMaxCount());
    if (maxPlayers != 0) {
      p.sendMessage(ChatColor.GREEN + "MaxPlayers: " + maxPlayers);
    } else p.sendMessage(ChatColor.RED + "maxPlayers: NOT_SET");
    if (lobby.getSpawnLocation() != null) {
      p.sendMessage(ChatColor.GREEN + "Lobby: OK");
    } else p.sendMessage(ChatColor.RED + "lobby: NOT_SET");
    if (arena.getRegionMax() != null && arena.getRegionMin() != null) {
      p.sendMessage(ChatColor.GREEN + "Region: OK");
    } else p.sendMessage(ChatColor.RED + "Region: NOT_SET");

    if (arena.getSpawnRegionMax() != null && arena.getSpawnRegionMin() != null) {
      p.sendMessage(ChatColor.GREEN + "SpawnRegion: OK");
    } else p.sendMessage(ChatColor.RED + "SpawnRegion: NOT_SET");

    if (!arena.getSpawns().isEmpty()) {
      p.sendMessage(ChatColor.GOLD + "SpawnPoints: ");
      for (Map.Entry<Team.Teams, List<Location>> entry : arena.getSpawns().entrySet()) {
        p.sendMessage(ChatColor.GOLD + entry.getKey().toString() + ":");
        for (int i = 1; i <= entry.getValue().size(); i++) {
          if (entry.getValue().get(i - 1) != null) {
            p.sendMessage(ChatColor.GREEN + "" + i + "" + ": OK");
          } else p.sendMessage(ChatColor.RED + "" + i + "" + ": NOT_SET");
        }
      }
    } else p.sendMessage(ChatColor.RED + "SpawnPositions: NOT_SET");

  }

  public void join(Player p) {
    if (!lobby.isFull()) {
      CGPlayer player = new CGPlayer(p, this);
      players.entrySet().forEach(entry -> entry.getValue().getPlayer().sendMessage(ChatColor.GREEN + p.getName() + " вошел"));
      players.put(p.getName(), player);
      player.join();
      if (lobby.isFull()) start();
    } else p.sendMessage(ChatColor.RED + "Игра уже заполнена.");
  }

  public void start() {
    //KNOCK OUT NOT READY TIMER

    if (isStarted()) return;
    if (!lobby.isFull()) return;
    if (!lobby.isAllReady()) {
      lobby.startKnockOut();
      return;
    } else if (lobby.isKnockOut()) {
      lobby.stopKnockOut();
    }
    //END

    gamePassing = new GamePassing(this);

    new BukkitRunnable() {
      int timeout = 5;

      @Override
      public void run() {
        if (timeout == 0) {
          players.forEach((key, value) -> {

            //START

            value.getPlayer().sendTitle(ChatColor.GREEN + "ЗАПУСК!", null, 20, 20, 20);

            scoreboard.set(value.getPlayer());

            started = true;
          });
          this.cancel();
          arena.start();
          gamePassing.start();
        } else
          players.forEach((key, value) -> value.getPlayer().sendTitle(ChatColor.GREEN + "" + timeout, null, 20, 20, 20));
        timeout--;
      }

    }.runTaskTimer(plugin, 0, 20);
  }

  public void stop() {
    if (!isStarted()) return;
    started = false;
    if (!players.isEmpty()) {
      for (Map.Entry<String, CGPlayer> player : players.entrySet()) {
        player.getValue().leave(false);
      }
      players.clear();
    }

    teams.forEach((teamName, team) -> team.removeScore(team.getScore()));

    arena.getSpawnTimer().stop();
    arena.updateSpawnerTimer();
    arena.getBlocks().forEach(block -> {
      block.setType(Material.AIR);
      block.getLocation().getNearbyEntities(1, 1, 1).forEach(Entity::remove);
    });
    arena.getBlocks().clear();
    airDrop.remove();
    if (gamePassing.isStarted()) {
      gamePassing.stop();
    }
  }

  public void results() {
    if (!players.isEmpty()) {
      Team best = teams.values().stream().filter((team -> !team.getPlayers().isEmpty())).max(Comparator.comparing(Team::getScore)).get();

      players.values().forEach(player -> {
        player.getPlayer().sendTitle(best.getColorName() + " победили!", null, 40, 20, 20);
      });

      best.givePrize(this);
    }
  }

  public void saveBlockLocation(List<Block> list) {
    Config.saveBlockLocation(list, name);
  }


  public boolean save() {
    return Config.saveGame(name, this);
  }

  public boolean load() {
    return Config.loadGame(name, this);
  }

  public boolean remove() {
    return Config.remove(name);
  }

  public Lobby getLobby() {
    return lobby;
  }

  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
    arena.updateSpawns();
  }

  public Arena getArena() {
    return arena;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Map<String, CGPlayer> getPlayers() {
    return players;
  }

  public Map<String, Kit> getKits() {
    return kits;
  }

  public Map<Team.Teams, Team> getTeams() {
    return teams;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public CGScoreboard getCGScoreboard() {
    return scoreboard;
  }

  public TemporaryBlock getTemporaryBlock() {
    return temporaryBlock;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public Map<Integer, List<String>> getPrize() {
    return prize;
  }

  public AirDrop getAirDrop() {
    return airDrop;
  }
}
