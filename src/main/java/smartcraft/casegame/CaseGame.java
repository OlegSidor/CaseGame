package smartcraft.casegame;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import smartcraft.casegame.handlers.BossHandlers;
import smartcraft.casegame.handlers.Events;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.commands.Cg;
import smartcraft.casegame.commands.Cga;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.handlers.MobSkillsHandler;
import smartcraft.casegame.inGameEntity.CGPlayer;
import smartcraft.casegame.tabCompletion.TabComplete;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseGame extends JavaPlugin {

  private static CaseGame instance;
  private Map<String, Game> games = new HashMap<>();

  public NamespacedKey navigationKey = new NamespacedKey(this, "Navigation");
  public NamespacedKey kitKey = new NamespacedKey(this, "Kit");
  public NamespacedKey teamKey = new NamespacedKey(this, "Team");
  public NamespacedKey artifactKey = new NamespacedKey(this, "Artifact");
  public NamespacedKey temporaryBlockkKey = new NamespacedKey(this, "TemporaryBlock");

  public List<FallingBlock> airDrop = new ArrayList<>();

  @Override
  public void onEnable() {
    instance = this;

    //CONFIG FILE
    if (!new File(this.getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }
    //COMMANDS
    getCommand("cg").setExecutor(new Cg());
    getCommand("cg").setTabCompleter(new TabComplete());
    getCommand("cga").setExecutor(new Cga());
    getCommand("cga").setTabCompleter(new TabComplete());


    //EVENTHANDLERS
    Bukkit.getPluginManager().registerEvents(new Events(), this);
    Bukkit.getPluginManager().registerEvents(new MobSkillsHandler(), this);
    Bukkit.getPluginManager().registerEvents(new BossHandlers(), this);

    //LOAD GAMES
    new BukkitRunnable() {
      @Override
      public void run() {
        File gameDir = new File(getDataFolder() + "/games");
        if (gameDir.exists()) {
          String contents[] = gameDir.list();
          if (contents != null) {
            for (int i = 0; i < contents.length; i++) {
              String name = contents[i].substring(0, contents[i].length() - 4);
              Game game = new Game(name);
              game.load();
              if (!game.isDisabled()) {
                games.put(name, game);
                getLogger().info("Game " + name + " loaded");
              } else getLogger().info("Game " + name + " is disabled");
            }
          }
        }
      }
    }.runTaskLater(this, 1);

    getLogger().info("CaseGame enabled");
  }

  @Override
  public void onDisable() {
    //KICK PLAYERS
    for (Map.Entry<String, Game> entry : games.entrySet()) {
      for (Map.Entry<String, CGPlayer> player : entry.getValue().getPlayers().entrySet()) {
        player.getValue().leave(false);
      }
      entry.getValue().getPlayers().clear();
    }
    getLogger().info("CaseGame disabled");
  }


  public WorldEditPlugin getWorldEdit() {
    Plugin p = Bukkit.getPluginManager().getPlugin("WorldEdit");
    if ((p instanceof WorldEditPlugin)) {
      return (WorldEditPlugin) p;
    }
    return null;
  }

  public static CaseGame getInstance() {
    return instance;
  }

  public Map<String, Game> getGames() {
    return games;
  }


  public Game getGame(Player p){
    //GET GAME FROM PLAYER META
    if(!p.hasMetadata("CaseGame")) return null;
    String arenaName = p.getMetadata("CaseGame").get(0).asString();
    if(!games.containsKey(arenaName)) return null;
    return getGames().get(arenaName);
  }

  public Player getNearPlayer(Location location) {
    List<Entity> near = location.getWorld().getEntities();
    List<Player> playersInRadius = new ArrayList<>();
    for (Entity en : near) {
      if (en.getLocation().distance(location) <= 20) {
        if (en instanceof Player) {
          if (en.hasMetadata("CaseGame")) {
            playersInRadius.add((Player) en);
          }
        }
      }
    }
    Player result = null;
    double lastDistance = Double.MAX_VALUE;
    for(Player p : playersInRadius) {
      double distance = p.getLocation().distance(location);
      if(distance < lastDistance) {
        lastDistance = distance;
        result = p;
      }
    }
    return result;
  }
}
