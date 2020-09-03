package smartcraft.casegame.handlers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.game.Kit;
import smartcraft.casegame.game.Navigation;
import smartcraft.casegame.inGameEntity.Boss;
import smartcraft.casegame.inGameEntity.CGPlayer;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.Team;
import smartcraft.casegame.inGameEntity.enemys.AngryHighSchool;
import smartcraft.casegame.navigationItems.Item;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Events implements Listener {

  private static CaseGame plugin = CaseGame.getInstance();
  private Random random = new Random();

  //KIT SELECT
  @EventHandler
  public void kitSelect(InventoryClickEvent e) {
    if (e.getWhoClicked().hasMetadata("CaseGame")) {
      if (e.getCurrentItem() != null) {
        ItemStack item = e.getCurrentItem();
        if (item.hasItemMeta()) {
          if (item.getItemMeta().getPersistentDataContainer().has(plugin.kitKey, PersistentDataType.STRING)) {
            String kitName = item.getItemMeta().getPersistentDataContainer().get(plugin.kitKey, PersistentDataType.STRING);
            if (e.getWhoClicked().hasPermission("casegame.kit." + kitName)) {
              Game game = plugin.getGame((Player) e.getWhoClicked());
              Kit kit = game.getKits().get(kitName);
              CGPlayer player = game.getPlayers().get(e.getWhoClicked().getName());
              player.setKit(kit);
              e.getWhoClicked().sendMessage(ChatColor.GREEN + "Кит " + kit.getName() + "" + ChatColor.GREEN + " выбран");
            } else e.getWhoClicked().sendMessage(ChatColor.GREEN + "Вы не можете использовать данный кит");
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
          }
        }
      }
    }
  }
  //END

  //TEAM SELECT
  @EventHandler
  public void teamSelect(InventoryClickEvent e) {
    if (e.getWhoClicked().hasMetadata("CaseGame")) {
      if (e.getCurrentItem() != null) {
        ItemStack item = e.getCurrentItem();
        if (item.hasItemMeta()) {
          if (item.getItemMeta().getPersistentDataContainer().has(plugin.teamKey, PersistentDataType.STRING)) {
            Team.Teams teamName = Team.getTeam(item.getItemMeta().getPersistentDataContainer().get(plugin.teamKey, PersistentDataType.STRING));
            Game game = plugin.getGame((Player) e.getWhoClicked());
            CGPlayer player = game.getPlayers().get(e.getWhoClicked().getName());
            Team team = game.getTeams().get(teamName);
            if (team.add(player)) {
              if (player.getTeam() != null) {
                player.getTeam().remove(player);
              }
              player.setTeam(team);
              String teamColoredName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("teams." + teamName.toString(), teamName.toString()));
              e.getWhoClicked().sendMessage(ChatColor.GREEN + "Добро пожаловать в команду: " + teamColoredName);

              //CHANGE TEAM ITEM COLOR
              e.getWhoClicked().getInventory().getItemInMainHand().setType(e.getCurrentItem().getType());
              ItemMeta teamItemMeta = e.getWhoClicked().getInventory().getItemInMainHand().getItemMeta();
              teamItemMeta.setDisplayName(ChatColor.getLastColors(teamColoredName) + ChatColor.stripColor(teamItemMeta.getDisplayName()));
              e.getWhoClicked().getInventory().getItemInMainHand().setItemMeta(teamItemMeta);
            }
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
          }
        }
      }
    }
  }
  //END

  //BLOCK NAVIGATION MOVE
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    e.setCancelled(Navigation.isNavigation(e.getCurrentItem()));
  }
  //END

  //BLOCK NAVIGATION DROP
  @EventHandler
  public void onDrop(PlayerDropItemEvent e) {
    e.setCancelled(Navigation.isNavigation(e.getItemDrop().getItemStack()));
  }
  //END


  //NAVIGATION INTERACT
  @EventHandler
  public void useNavigation(PlayerInteractEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      if (e.getItem() != null) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
          ItemStack item = e.getItem();
          if (item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(plugin.navigationKey, PersistentDataType.STRING)) {
              String className = container.get(plugin.navigationKey, PersistentDataType.STRING);
              try {
                Object object = Class.forName("smartcraft.casegame.navigationItems." + className).getDeclaredConstructor().newInstance();
                String arenaName = e.getPlayer().getMetadata("CaseGame").get(0).asString();
                CGPlayer player = plugin.getGames().get(arenaName).getPlayers().get(e.getPlayer().getName());
                ((Item) object).click(player);
                e.setCancelled(true);
              } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException er) {
                er.printStackTrace();
              }
            }
          }
        }
      }
    }
  }
  //END


  //PLAYER LEAVE
  @EventHandler
  public void onLeave(PlayerQuitEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      Game game = plugin.getGame(e.getPlayer());
      game.getPlayers().get(e.getPlayer().getName()).leave();
    }
  }
  //END

  //ADD/SUB POINTS
  @EventHandler
  public void onKill(EntityDeathEvent e) {
    if (e.getEntity().getKiller() != null) {
      LivingEntity mob = e.getEntity();
      if (mob.hasMetadata("CgMob")) {
        Player p = e.getEntity().getKiller();
        if (p.hasMetadata("CaseGame")) {
          Game game = plugin.getGame(p);
          CGPlayer player = game.getPlayers().get(p.getName());
          int points = mob.getMetadata("CgMob").get(0).asInt();
          player.getTeam().addScore(points);
        }
      }
    }
  }
  //END


  //PLAYER DEATH
  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (e.getEntity().hasMetadata("CaseGame")) {
      e.setKeepInventory(true);
      if (e.getEntity().getKiller() != null) {
        Player killer = e.getEntity().getKiller();

        Game game = plugin.getGame(e.getEntity());
        CGPlayer CGplayer = game.getPlayers().get(e.getEntity().getName());
        CGPlayer CGkiller = game.getPlayers().get(killer.getName());

        CGplayer.getTeam().removeScore(10);
        CGkiller.getTeam().addScore(10);
        e.getDrops().clear();

      }
    }
  }
  //END

  //PLAYER RESPAWN
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRespawn(PlayerRespawnEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      Game game = plugin.getGame(e.getPlayer());
      CGPlayer player = game.getPlayers().get(e.getPlayer().getName());
      e.setRespawnLocation(player.getSpawnLocation());
    }
  }
  //END


  //PLAYER LEAVE FROM AREA
  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      Game game = plugin.getGame(e.getPlayer());
      if (game.isStarted())
        if ((e.getTo().getBlockX() >= game.getArena().getRegionMax().getBlockX()
            || e.getTo().getBlockY() >= game.getArena().getRegionMax().getBlockY()
            || e.getTo().getBlockZ() >= game.getArena().getRegionMax().getBlockZ()) ||
            e.getTo().getBlockX() <= game.getArena().getRegionMin().getBlockX()
            || e.getTo().getBlockY() <= game.getArena().getRegionMin().getBlockY()
            || e.getTo().getBlockZ() <= game.getArena().getRegionMin().getBlockZ()
            ) {
          e.setCancelled(true);
        }
    }
  }
  //END

  //ON OPEN MIMIC
  @EventHandler
  public void onOpenMinic(PlayerInteractEvent e) {
    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      if (e.getClickedBlock() != null) {
        if (e.getClickedBlock().hasMetadata("CGMimic")) {
          if (e.getPlayer().hasMetadata("CaseGame")) {
            Game game = plugin.getGame(e.getPlayer());
            e.getClickedBlock().setType(Material.AIR);
            LivingEntity entity = new AngryHighSchool(AngryHighSchool.class.getName()).spawn(e.getClickedBlock().getLocation());
            game.getArena().getSpawnTimer().addMob(entity);
            ((Monster) entity).setTarget(e.getPlayer());
          }
        }
      }
    }
  }
  //END

  //SPAWN BOSS
  @EventHandler
  public void onDropArtifact(PlayerDropItemEvent e) {
    Player p = e.getPlayer();
    if (p.hasMetadata("CaseGame")) {
      ItemStack item = e.getItemDrop().getItemStack();
      if (item.hasItemMeta())
        if (item.getItemMeta().getPersistentDataContainer().has(plugin.artifactKey, PersistentDataType.SHORT)) {
          Location position = e.getItemDrop().getLocation().add(0, -1, 0);
          int radius = 1;
          List<Block> blocks = new ArrayList<>();
          for (int x = position.getBlockX() - radius; x <= position.getBlockX() + radius; x++) {
            for (int y = position.getBlockY() - radius; y <= position.getBlockY() + radius; y++) {
              for (int z = position.getBlockZ() - radius; z <= position.getBlockZ() + radius; z++) {
                blocks.add(position.getWorld().getBlockAt(x, y, z));
              }
            }
          }
          for (Block block : blocks) {
            if (block.getType().equals(Material.BEACON) && block.hasMetadata("CGBossBeacon")) {
              block.setType(Material.AIR);
              e.getItemDrop().remove();
              //TODO: UNCOMMENT on MORE boss
//              int random = this.random.ints(0, Boss.values().length).findFirst().getAsInt();
              ((EnemyA) Boss.values()[0].getObject()).spawn(block.getLocation());
              break;
            }
          }
        }
    }
  }
  //END

  // BLOCK BREAK
  @EventHandler
  public void blockBreak(BlockBreakEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      if (!e.getPlayer().hasPermission("smartcraft.casegame.admin")) {
        e.setCancelled(true);
      }
    }
  }
  //END

  // USER COMMANDS
  @EventHandler
  public void onUseCommand(PlayerCommandPreprocessEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      if (!e.getPlayer().hasPermission("smartcraft.casegame.admin")) {
        List<String> AllowedCmds = plugin.getConfig().getStringList("allowedCmd");
        for (String cmd : AllowedCmds) {
          if (!e.getMessage().equalsIgnoreCase("/" + cmd)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "У вас нету доступа к этой команде во время игры!");
          }
        }
      }
    }
  }
  //END

  //Prevent Drowned
  @EventHandler
  public void onDrowner(EntityTransformEvent e){
    if(e.getTransformedEntity().hasMetadata("CgMob")) {
      if (e.getTransformReason().equals(EntityTransformEvent.TransformReason.DROWNED)) {
      e.setCancelled(true);
      }
    }
  }
  //END

  //SIGN CREATE
  @EventHandler
  public void signCreate(SignChangeEvent e) {
    if (e.getLine(0).equals("Case Game")) {
      if (e.getPlayer().hasPermission("smartcraft.casegame.createSign")) {
        if (e.getLine(1).equals("join")) {
          if (!plugin.getGames().containsKey(e.getLine(2))) {
            e.setLine(0, "Case Game");
            e.setLine(1, "  ERROR  ");
            e.setLine(2, "");
            e.setLine(3, "");
            e.getPlayer().sendMessage(ChatColor.RED + "Игра " + e.getLine(2) + " не существует");
          }
        }
      } else {
        e.getPlayer().sendMessage(ChatColor.RED + "У вас нету прав для данного действия");
        e.setCancelled(true);
      }
    }
  }
  //END


  //SIGN CLICK
  @EventHandler
  public void onSignClick(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Material type = e.getClickedBlock().getType();
      if ((type.equals(Material.OAK_SIGN)) ||
          (type.equals(Material.OAK_WALL_SIGN))) {
        Sign s = (Sign) e.getClickedBlock().getState();
        if (s.getLine(1).equals("join")) {
          String gameName = s.getLine(2);
          if (plugin.getGames().containsKey(gameName)) {
            plugin.getGames().get(gameName).join(e.getPlayer());
          }
        }
      }
    }
  }
  //END

  //TEMPORARY BLOCK
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    if (e.getPlayer().hasMetadata("CaseGame")) {
      Game game = plugin.getGame(e.getPlayer());
      ItemStack itemStack = e.getItemInHand();
      if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.temporaryBlockkKey, PersistentDataType.SHORT)) {
        Block block = e.getBlock();
        if (block.getLocation().getBlockY() < game.getTemporaryBlock().getMaxHeight()) {
          int removeTime = game.getTemporaryBlock().getRemoveTime();
          Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            block.setType(e.getBlockReplacedState().getType(), false);
            game.getTemporaryBlock().give(e.getPlayer(), 1);
          }, removeTime * 20);
        } else {
          e.getPlayer().sendMessage(ChatColor.RED + "Максимальная высота " + game.getTemporaryBlock().getMaxHeight());
          e.setCancelled(true);
        }
      }
    }
  }

  //END
  //STOP ZOMBIE BURN
  @EventHandler
  public void onBurn(EntityCombustEvent e) {
    if (e.getEntity().hasMetadata("CgMob")) {
      e.setCancelled(true);
    }
  }
  //END
}
