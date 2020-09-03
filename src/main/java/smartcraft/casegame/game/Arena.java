package smartcraft.casegame.game;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.Team;
import smartcraft.casegame.timers.MobSpawnTimer;

import java.io.Serializable;
import java.util.*;

public class Arena implements Serializable {

  private Game game;
  private Location regionMin;
  private Location regionMax;
  private Location spawnRegionMin;
  private Location spawnRegionMax;
  private Map<Team.Teams, List<Location>> spawns = new HashMap<>();
  private MobSpawnTimer spawnTimer;
  private CaseGame plugin = CaseGame.getInstance();
  private List<Block> blocks = new ArrayList<>();

  public Arena(Game game) {
    this.game = game;
    spawnTimer = new MobSpawnTimer(game);
  }


  public void start() {
    List<Location> usedLocations = new ArrayList<>();
    game.getPlayers().forEach((s, player) -> {
      if(player.getTeam() == null){
        player.forceTeam();
      }
      spawns.get(player.getTeam().getName()).forEach(location -> {
        if (!usedLocations.contains(location)) {
          usedLocations.add(location);
          player.setSpawnLocation(location);
          player.getPlayer().teleport(location);
          player.giveKit();
          game.getTemporaryBlock().give(player.getPlayer(), game.getTemporaryBlock().getCount());
          player.getPlayer().setInvulnerable(false);
        }
      });
    });

    spawnTimer.start();
    generateChests();
    generateBeacon();
    game.saveBlockLocation(blocks);
  }

  private void generateChests() {
    for (int i = 0; i < 4; i++) {
      Block bossChestBlock = spawnTimer.randomLocation().getBlock();
      bossChestBlock.setType(Material.CHEST);
      Chest bossChest = (Chest) bossChestBlock.getState();
      ItemStack item = new ItemStack(Material.DIAMOND);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.getPersistentDataContainer().set(plugin.artifactKey, PersistentDataType.SHORT, (short)1);
      itemMeta.setDisplayName(ChatColor.GREEN + "Артефакт");
      List<String> lore = new ArrayList<>();
      lore.add("Артефакт предназначен для призыва босса");
      itemMeta.setLore(lore);
      item.setItemMeta(itemMeta);
      bossChest.getBlockInventory().addItem(item);
      blocks.add(bossChestBlock);

      Block mimicBlock = spawnTimer.randomLocation().getBlock();
      mimicBlock.setType(Material.CHEST);
      mimicBlock.setMetadata("CGMimic", new FixedMetadataValue(plugin, true));
      blocks.add(mimicBlock);
    }
  }

  private void generateBeacon() {
    for (int i = 0; i < 4; i++) {
      Block beacon = spawnTimer.randomLocation().getBlock();
      beacon.setType(Material.BEACON);
      beacon.setMetadata("CGBossBeacon", new FixedMetadataValue(plugin, true));
      blocks.add(beacon);
    }
  }

  public void setRegion(Location min, Location max) {
    regionMin = min;
    regionMax = max;
  }

  public void setRegion(Region region) {
    regionMin = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ());
    regionMax = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
  }

  public void setSpawnRegion(Location min, Location max) {
    spawnRegionMax = max;
    spawnRegionMin = min;
  }

  public void setSpawnRegion(Region spawnRegion) {
    spawnRegionMin = new Location(BukkitAdapter.adapt(spawnRegion.getWorld()), spawnRegion.getMinimumPoint().getBlockX(), spawnRegion.getMinimumPoint().getBlockY(), spawnRegion.getMinimumPoint().getBlockZ());
    spawnRegionMax = new Location(BukkitAdapter.adapt(spawnRegion.getWorld()), spawnRegion.getMaximumPoint().getBlockX(), spawnRegion.getMaximumPoint().getBlockY(), spawnRegion.getMaximumPoint().getBlockZ());
  }

  public boolean setSpawn(Team.Teams team, int index, Location location) {
    if (spawns.containsKey(team)) {
      List<Location> locations = spawns.get(team);
      locations.set(index, location);
      spawns.put(team, locations);
    } else {
      List<Location> locations = new ArrayList<Location>(Collections.nCopies(game.getMaxPlayers() / 2, null));
      locations.set(index, location);
      spawns.put(team, locations);
    }
    return true;
  }

  public void updateSpawns() {
    if (!spawns.isEmpty()) {
      for (Map.Entry<Team.Teams, List<Location>> entry : spawns.entrySet()) {
        if (entry.getValue().size() > (game.getMaxPlayers() / 2)) {
          for (int i = (entry.getValue().size() - (game.getMaxPlayers() / 2)); i < entry.getValue().size(); i++) {
            entry.getValue().remove(i);
          }
        } else if (entry.getValue().size() < (game.getMaxPlayers() / 2)) {
          entry.getValue().addAll(Collections.nCopies((game.getMaxPlayers() / 2) - entry.getValue().size(), null));
        }
      }
    }
  }

  public Location getRegionMin() {
    return regionMin;
  }

  public Location getRegionMax() {
    return regionMax;
  }

  public Location getSpawnRegionMin() {
    return spawnRegionMin;
  }

  public Location getSpawnRegionMax() {
    return spawnRegionMax;
  }

  public Map<Team.Teams, List<Location>> getSpawns() {
    return spawns;
  }

  public MobSpawnTimer getSpawnTimer() {
    return spawnTimer;
  }

  public void updateSpawnerTimer(){
    MobSpawnTimer newTimer = new MobSpawnTimer(game);
    newTimer.setChances(spawnTimer.getChances());
    newTimer.setMobs(spawnTimer.getMobs());
    newTimer.setMaxCount(spawnTimer.getMaxCount());
    newTimer.setSpawnPeriod(spawnTimer.getSpawnPeriod());
    spawnTimer = newTimer;
  }

  public List<Block> getBlocks() {
    return blocks;
  }
}
