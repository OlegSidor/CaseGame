package smartcraft.casegame.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CGScoreboard {
  private Scoreboard scoreboard;

  public CGScoreboard(){
    ScoreboardManager scoreBoardManager = Bukkit.getScoreboardManager();
    scoreboard = scoreBoardManager.getNewScoreboard();
    Objective objective = scoreboard.registerNewObjective("CGScores", "dummy", ChatColor.GOLD+"Case Game");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

  }

  public void set(Player p){
    p.setScoreboard(scoreboard);
  }

  public Scoreboard getScoreboard() {
    return scoreboard;
  }
}
