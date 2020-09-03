package smartcraft.casegame.game;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {


  private static CaseGame plugin = CaseGame.getInstance();

  public static boolean saveGame(String name, Game game) {

    File gameFile = getConfigFile(name);
    if (gameFile == null) return false;
    FileConfiguration config = YamlConfiguration.loadConfiguration(gameFile);
    config.set("name", name);
    config.set("time", game.getTime());
    config.set("lobby", game.getLobby().getSpawnLocation());
    config.set("maxPlayers", game.getMaxPlayers());
    config.set("region.Max", game.getArena().getRegionMax());
    config.set("region.Min", game.getArena().getRegionMin());
    config.set("spawnRegion.Max", game.getArena().getSpawnRegionMax());
    config.set("spawnRegion.Min", game.getArena().getSpawnRegionMin());
    config.set("disabled", game.isDisabled());
    config.set("maxAirDropCount", game.getAirDrop().getMaxCount());

    if (!game.getArena().getSpawns().isEmpty()) {
      for (Map.Entry<Team.Teams, List<Location>> entry : game.getArena().getSpawns().entrySet()) {
        ConfigurationSection configurationSection = config.createSection("spawns." + entry.getKey().toString());
        for (int i = 0; i < entry.getValue().size(); i++)
          configurationSection.set(i + "", entry.getValue().get(i));
      }
    }

    if (plugin.getConfig().getInt("maxMobSpawn") != game.getArena().getSpawnTimer().getMaxCount()) {
      config.set("maxMobSpawn", game.getArena().getSpawnTimer().getMaxCount());
    }
    if (plugin.getConfig().getInt("spawnPeriod") != game.getArena().getSpawnTimer().getSpawnPeriod()) {
      config.set("spawnPeriod", game.getArena().getSpawnTimer().getSpawnPeriod());
    }

    try {
      config.save(gameFile);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean loadGame(String name, Game game) {

    //ARENA AND LOBBY LOAD
    File gameDir = new File(plugin.getDataFolder() + "/games");
    if (!gameDir.exists()) return false;
    File gameFile = new File(plugin.getDataFolder() + "/games/" + name + ".yml");
    if (!gameFile.exists()) return false;
    FileConfiguration config = YamlConfiguration.loadConfiguration(gameFile);
    if (config.contains("name")) {
      String gameName = config.getString("name");
      game.setName(gameName);
    }

    if (config.contains("lobby")) {
      Location lobby = (Location) config.get("lobby");
      game.getLobby().setSpawnLocation(lobby);
    }

    if (config.contains("maxPlayers")) {
      int maxPlayers = config.getInt("maxPlayers");
      game.setMaxPlayers(maxPlayers);
    }
    if (config.contains("region")) {
      Location regionMax = (Location) config.get("region.Max");
      Location regionMin = (Location) config.get("region.Min");
      game.getArena().setRegion(regionMin, regionMax);
    }

    if (config.contains("spawnRegion")) {
      Location spawnRegionMax = (Location) config.get("spawnRegion.Max");
      Location spawnRegionMin = (Location) config.get("spawnRegion.Min");
      game.getArena().setSpawnRegion(spawnRegionMin, spawnRegionMax);
    }

    if (config.contains("spawns")) {
      Set<String> teams = config.getConfigurationSection("spawns").getKeys(false);
      for (String teamKey : teams) {
        Set<String> locationKeys = config.getConfigurationSection("spawns." + teamKey).getKeys(false);
        for (String index : locationKeys) {
          Location location = (Location) config.get("spawns." + teamKey + "." + index);
          Team.Teams team = Team.getTeam(teamKey);
          game.getArena().setSpawn(team, Integer.parseInt(index), location);
        }
      }
    }

    if(config.contains("prize")){
         config.getConfigurationSection("prize").getKeys(false).forEach(s -> {
         game.getPrize().put(Integer.parseInt(s), config.getStringList("prize."+s));
       });
    }

    FileConfiguration valuableConfig = config;
    if (!valuableConfig.contains("time"))
      valuableConfig = plugin.getConfig();

    int time = valuableConfig.getInt("time");
    game.setTime(time);

    game.setDisabled(config.getBoolean("disabled", false));
    //END

    //AIRDROPS

    valuableConfig = config;
    if (!valuableConfig.contains("airdrop"))
      valuableConfig = plugin.getConfig();

    for (String key : valuableConfig.getConfigurationSection("airdrop").getKeys(false)) {
      List<ItemStack> items = new ArrayList<>();
      valuableConfig.getStringList("airdrop."+key).forEach(item -> {
        items.add(getItem(item));
      });
      game.getAirDrop().getPacks().put(key, items);
    }

    valuableConfig = config;
    if (!valuableConfig.contains("maxAirDropCount"))
      valuableConfig = plugin.getConfig();

    game.getAirDrop().setMaxCount(valuableConfig.getInt("maxAirDropCount"));

    valuableConfig = config;
    if (!valuableConfig.contains("allowedAirDrops"))
      valuableConfig = plugin.getConfig();

    game.getAirDrop().getAllowedAirDrops().addAll(valuableConfig.getStringList("allowedAirDrops"));

    //END

    //KITS LOAD
    loadKits(game);


    //LOAD TEAMS
    for (Team.Teams team : Team.Teams.values()) {
      game.getTeams().put(team, new Team(team, game.getCGScoreboard().getScoreboard()));
    }

    //LOAD SPAWN TIMER
    valuableConfig = config;
    if (!valuableConfig.contains("mobChances"))
      valuableConfig = plugin.getConfig();

    Set<String> mobs = valuableConfig.getConfigurationSection("mobChances").getKeys(false);
    for (String mob : mobs) {
      int chance = valuableConfig.getInt("mobChances." + mob);
      game.getArena().getSpawnTimer().getChances().put(mob, chance);
    }
    game.getArena().getSpawnTimer().prepare();

    valuableConfig = config;
    if (!config.contains("spawnPeriod"))
      valuableConfig = plugin.getConfig();

    int spawnPeriod = valuableConfig.getInt("spawnPeriod");
    game.getArena().getSpawnTimer().setSpawnPeriod(spawnPeriod);

    valuableConfig = config;
    if (!config.contains("maxMobSpawn"))
      valuableConfig = plugin.getConfig();

    int maxMobSpawn = valuableConfig.getInt("maxMobSpawn");
    game.getArena().getSpawnTimer().setMaxCount(maxMobSpawn);
    //END

    //TemporaryBlock
    valuableConfig = config;
    if (!config.contains("temporaryBlockRemove"))
      valuableConfig = plugin.getConfig();

    int removeTime = valuableConfig.getInt("temporaryBlockRemove");
    game.getTemporaryBlock().setRemoveTime(removeTime);

    valuableConfig = config;
    if (!config.contains("temporaryBlocksAmount"))
      valuableConfig = plugin.getConfig();

    int blockCount = valuableConfig.getInt("temporaryBlocksAmount");
    game.getTemporaryBlock().setCount(blockCount);

    valuableConfig = config;
    if (!config.contains("temporaryBlockMaxHeight"))
      valuableConfig = plugin.getConfig();

    int maxHeight = valuableConfig.getInt("temporaryBlockMaxHeight");
    game.getTemporaryBlock().setMaxHeight(maxHeight);
    //END


    //REMOVE LAST GAMEDATA
    if(!game.isStarted()) {
      if (config.isConfigurationSection("gameData")) {
        Set<String> keys = config.getConfigurationSection("gameData").getKeys(false);
        keys.forEach(key -> {
          if (config.contains("gameData." + key)) {
            Location location = config.getLocation("gameData." + key);
            if (location != null) {
              location.getBlock().setType(Material.AIR);
              location.getNearbyEntities(1, 1, 1).forEach(Entity::remove);
            }
          }

        });
        config.set("gameData", null);
      }
    }
    //END


    try {
      config.save(gameFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return true;
  }

  public static boolean remove(String name) {
    File gameDir = new File(plugin.getDataFolder() + "/games");
    if (!gameDir.exists()) return false;
    File gameFile = new File(plugin.getDataFolder() + "/games/" + name + ".yml");
    if (!gameFile.exists()) return false;

    return gameFile.delete();
  }

  public static void loadKits(Game game) {
    File kitsDir = new File(plugin.getDataFolder() + "/kits");
    if (!kitsDir.exists()) {
      kitsDir.mkdir();
      return;
    }

    String contents[] = kitsDir.list();
    if (contents != null) {
      for (String file : contents) {
        File kitFile = new File(plugin.getDataFolder() + "/kits/" + file);
        FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
        String name = config.getString("item.name");
        String fileName = file.substring(0, file.length() - 4);
        int slot = config.getInt("item.slot");
        ItemStack icon = getItem(config.getString("item.icon", null));

        //PREPARE SELECT ITEM (ICON)
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (config.contains("item.lore")) {
          List<String> iconLore = config.getStringList("item.lore");
          iconMeta.setLore(iconLore);
        }

        iconMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        iconMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        iconMeta.getPersistentDataContainer().set(plugin.kitKey, PersistentDataType.STRING, fileName);

        icon.setItemMeta(iconMeta);
        //END

        List<String> kitItems = config.getStringList("kititems");
        List<ItemStack> content = new ArrayList<>();
        for (String item : kitItems) {
          content.add(getItem(item));
        }
        ItemStack helmet = getItem(config.getString("armor.helmet", null));
        ItemStack chestplate = getItem(config.getString("armor.chestplate", null));
        ItemStack leggings = getItem(config.getString("armor.leggings", null));
        ItemStack boots = getItem(config.getString("armor.boots", null));

        ItemStack[] contens = new ItemStack[content.size()];
        content.toArray(contens);

        game.getKits().put(fileName, new Kit(name, slot, icon, contens, helmet, chestplate, leggings, boots));
      }
    }

  }

  private static ItemStack getItem(String itemString) {
    if (itemString == null) return null;
    String material = itemString.split(" ")[0].split(":")[0];
    if (material == null) return null;
    int amount = Integer.parseInt(itemString.split(" ")[1]);
    if (amount == 0) amount = 1;
    Material itemMaterial = Material.getMaterial(material);
    if (itemMaterial == null) return null;
    ItemStack item = new ItemStack(itemMaterial, amount);
    if (item.getType().equals(Material.POTION) ||
        item.getType().equals(Material.SPLASH_POTION) ||
        item.getType().equals(Material.LINGERING_POTION)) {
      PotionMeta itemMeta = ((PotionMeta) item.getItemMeta());
      String potion = itemString.split(" ")[0].split(":")[1];
      PotionType potionType = PotionType.valueOf(potion);
      String[] potion_arr = itemString.split(" ")[0].split(":");
      boolean extended = false;
      boolean upgraded = false;
      if (potion_arr.length == 3) {
        if (Integer.parseInt(potion_arr[2]) == 2) {
          extended = true;
        } else if (Integer.parseInt(potion_arr[2]) == 1) {
          upgraded = true;
        }
      }
      itemMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
      item.setItemMeta(itemMeta);
    }
    if (itemString.contains("/")) {
      String enchants = itemString.split("/")[1];
      String[] enchantsInfo = enchants.split(" ");
      for (int i = 0; i < enchantsInfo.length; i += 2) {
        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantsInfo[i]));
        if (enchant == null) continue;
        item.addUnsafeEnchantment(enchant, Integer.parseInt(enchantsInfo[i + 1]));
      }
    }
    return item;
  }

  public static File getConfigFile(String arenaName) {
    File gameDir = new File(plugin.getDataFolder() + "/games");
    if (!gameDir.exists()) {
      gameDir.mkdir();
    }
    File gameFile = new File(plugin.getDataFolder() + "/games/" + arenaName + ".yml");
    if (!gameFile.exists()) {
      try {
        gameFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    return gameFile;
  }

  public static void saveBlockLocation(List<Block> list, String arenaName) {
    File gameFile = getConfigFile(arenaName);
    if (gameFile == null) return;
    FileConfiguration config = YamlConfiguration.loadConfiguration(gameFile);
    for (int i = 0; i < list.size(); i++) {
      config.set("gameData.b" + i, list.get(i).getLocation());
    }
    try {
      config.save(gameFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
