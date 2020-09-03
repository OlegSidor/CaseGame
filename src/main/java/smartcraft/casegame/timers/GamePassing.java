package smartcraft.casegame.timers;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.CGPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePassing extends BukkitRunnable {
    private Game game;
    private int time;
    private static CaseGame plugin = CaseGame.getInstance();
    private boolean started = false;

    private Random random = new Random();
    private List<Integer> randomTime = new ArrayList<>();

    public GamePassing(Game game) {
        this.game = game;
        time = game.getTime();
    }


    @Override
    public void run() {
        started = true;
        if (time != 0) {
            for (CGPlayer p : game.getPlayers().values()) {
                Objective scores = p.getPlayer().getScoreboard().getObjective("CGScores");
                if (scores != null) {
                    int seconds = time % 60;
                    int minutes = time / 60;

                    scores.setDisplayName(ChatColor.GOLD + "Case Game - " + ChatColor.RED + "" + ChatColor.BOLD + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                }
            }
            if(randomTime.contains(time)){
                game.getAirDrop().spawn(game.getArena().getSpawnTimer().randomLocation());
            }
            time--;
        } else {
            stop();
        }
    }

    public void start() {
        if(game.getTime() > 120) {
            for (int i = 0; i < game.getAirDrop().getMaxCount(); i++) {
                randomTime.add(random.ints(60, game.getTime()-60).findFirst().getAsInt());
            }
        }
        this.runTaskTimer(plugin, 0, 20);
    }

    public void stop() {
        this.cancel();
        started = false;
        game.results();
        game.stop();
    }

  public Boolean isStarted() {
    return started;
  }
}
