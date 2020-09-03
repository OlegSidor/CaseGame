package smartcraft.casegame.inGameEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.game.Kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Team {

  private static CaseGame plugin = CaseGame.getInstance();
  private List<CGPlayer> players = new ArrayList<>();
  private Teams name;
  private String colorName;
  private Scoreboard scoreboard;
  private int score = 0;
  private int scorePos;

  public Team(Teams team, Scoreboard scoreboard) {
    this.name = team;
    this.scoreboard = scoreboard;
    org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(team.toString());
    String teamName = plugin.getConfig().getString("teams." + team.toString(), "");
    if (teamName != null) {
      this.colorName = ChatColor.translateAlternateColorCodes('&', teamName);
      scoreboardTeam.setPrefix(ChatColor.getLastColors(colorName));
      scoreboardTeam.setAllowFriendlyFire(false);
      Objective objective = scoreboard.getObjective("CGScores");
      if (objective == null) return;
      int scorePos = objective.getScoreboard().getEntries().size() + 1;
      objective.getScore(colorName).setScore(scorePos + 1);
      objective.getScore(ChatColor.LIGHT_PURPLE + " Очки:" + ChatColor.getLastColors(colorName) + " " + this.score).setScore(scorePos);
      this.scorePos = scorePos;
    }
  }

  public Teams getName() {
    return name;
  }

  public String getColorName() {
    return colorName;
  }

  public enum Teams {
    RED, BLUE
  }

  public void addScore(int score) {
    Objective objective = scoreboard.getObjective("CGScores");
    if (objective == null) return;
    Scoreboard scoreboard = objective.getScoreboard();
    if (scoreboard == null) return;
    scoreboard.resetScores(ChatColor.LIGHT_PURPLE + " Очки:" + ChatColor.getLastColors(colorName) + " " + this.score);
    this.score += score;
    objective.getScore(ChatColor.LIGHT_PURPLE + " Очки:" + ChatColor.getLastColors(colorName) + " " + this.score).setScore(scorePos);
  }

  public void removeScore(int score) {
    if (this.score - score <= 0) {
      score = this.score;
    }
      Objective objective = scoreboard.getObjective("CGScores");
      if (objective == null) return;
      Scoreboard scoreboard = objective.getScoreboard();
      if (scoreboard == null) return;
      scoreboard.resetScores(ChatColor.LIGHT_PURPLE + " Очки:" + ChatColor.getLastColors(colorName) + " " + this.score);
      this.score -= score;
      objective.getScore(ChatColor.LIGHT_PURPLE + " Очки:" + ChatColor.getLastColors(colorName) + " " + this.score).setScore(scorePos);
  }

  public int getScore() {
    return score;
  }

  public boolean add(CGPlayer player) {
    if (players.size() < player.getGame().getMaxPlayers() / 2) {
      players.add(player);
      return true;
    }
    player.getPlayer().sendMessage(ChatColor.RED + "Команда заполнена!");
    return false;
  }

  public boolean isAvailable(Game game) {
    return players.size() < game.getMaxPlayers() / 2;
  }

  public void remove(CGPlayer player) {
    players.remove(player);
  }

  public static Teams getTeam(String name) {
    for (Teams team : Teams.values()) {
      if (name.equalsIgnoreCase(team.toString())) {
        return team;
      }
    }
    return null;
  }


  public void givePrize(Game game) {
    game.getPrize().forEach((score, commandList) -> {
      if (getScore() >= score) {
        for (CGPlayer p : players) {
          commandList.forEach(cmd ->
              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%p", p.getPlayer().getName()))
          );
        }
      }
    });
  }

  public static void open(CGPlayer p) {
    Inventory menu = Bukkit.createInventory(null, 18, "Команды");

    for (Teams team : Teams.values()) {
      ItemStack item = new ItemStack(Material.getMaterial(team.toString() + "_WOOL"), 1);
      ItemMeta itemMeta = item.getItemMeta();
      String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("teams." + team.toString(), team.toString()));

      itemMeta.getPersistentDataContainer().set(plugin.teamKey, PersistentDataType.STRING, team.toString());
      List<CGPlayer> players = p.getGame().getTeams().get(team).players;
      if (players.size() == p.getGame().getMaxPlayers() / 2) {
        name += ChatColor.RED + " (Полная)";
      }
      if (!players.isEmpty()) {
        List<String> lore = new ArrayList<>();
        for (CGPlayer player : players) {
          lore.add("- " + player.getPlayer().getName());
        }
        itemMeta.setLore(lore);
      }

      itemMeta.setDisplayName(name);
      item.setItemMeta(itemMeta);
      menu.addItem(item);
    }

    p.getPlayer().openInventory(menu);
  }

  public List<CGPlayer> getPlayers() {
    return players;
  }
}
