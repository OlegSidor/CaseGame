package smartcraft.casegame.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class MobSpawnTimer extends BukkitRunnable {

  private CaseGame plugin = CaseGame.getInstance();
  private Game game;
  private Map<String, Integer> chances = new HashMap<>();
  private List<EnemyA> mobs = new ArrayList<>();
  private List<LivingEntity> spawnedMobs = new ArrayList<>();
  private int spawnPeriod;
  private int maxCount;
  private Random random = new Random();
  private boolean started = false;

  public MobSpawnTimer(Game game) {
    this.game = game;
  }


  @Override
  public void run() {
    spawnedMobs.removeAll(spawnedMobs.stream().filter(Entity::isDead).collect(Collectors.toList()));
    if (spawnedMobs.size() < maxCount) {
      int random = this.random.nextInt(mobs.size());
      spawnedMobs.add(mobs.get(random).spawn(randomLocation()));
    }
  }

  public void prepare() {
    if (!chances.isEmpty()) {
      int maxChance = chances.values().stream().mapToInt(n -> n).sum();
      chances.forEach((mob, chance) -> {
        try {
          String className = "smartcraft.casegame.inGameEntity.enemys." + mob;
          Class<?> enemyClass = Class.forName(className);
          Object enemy = enemyClass.getDeclaredConstructor(String.class).newInstance(className);

          int n = (int) Math.floor(maxChance * ((double) chance / 100));
          mobs.addAll(Collections.nCopies(n, (EnemyA) enemy));
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void start() {
    started = true;
    this.runTaskTimer(plugin, 0, 20 * spawnPeriod);
  }

  public void stop() {
    if(started) {
      if (!this.isCancelled())
        this.cancel();

      if (!spawnedMobs.isEmpty()) {
        spawnedMobs.forEach(entity -> {
          if (!entity.isDead())
            entity.damage(9000);
        });
      }
      started = false;
    }
  }

  public void addMob(LivingEntity e){
    spawnedMobs.add(e);
  }

  public Map<String, Integer> getChances() {
    return chances;
  }

  public void setSpawnPeriod(int spawnPeriod) {
    this.spawnPeriod = spawnPeriod;
  }

  public int getSpawnPeriod() {
    return spawnPeriod;
  }

  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public Location randomLocation() {
    Location location;
    Random rg = new Random();
    do {
      int minX = game.getArena().getSpawnRegionMin().getBlockX();
      int maxX = game.getArena().getSpawnRegionMax().getBlockX();
      int minZ = game.getArena().getSpawnRegionMin().getBlockZ();
      int maxZ = game.getArena().getSpawnRegionMax().getBlockZ();
      int minY = game.getArena().getSpawnRegionMin().getBlockY();
      int maxY = game.getArena().getSpawnRegionMax().getBlockY();
      int x = rg.nextInt(maxX - minX + 1) + minX;
      int z = rg.nextInt(maxZ - minZ + 1) + minZ;
      int y = rg.nextInt(maxY - minY + 1) + minY;
      location = new Location(game.getArena().getSpawnRegionMin().getWorld(), x + 0.5D, y + 0.5D, z + 0.5D);
    } while (!location.getBlock().getType().equals(Material.AIR));
    while (location.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
      location.subtract(0, 1, 0);
    }
    return location;
  }

  public void setChances(Map<String, Integer> chances) {
    this.chances = chances;
  }

  public void setMobs(List<EnemyA> mobs) {
    this.mobs = mobs;
  }

  public List<EnemyA> getMobs() {
    return mobs;
  }
}
