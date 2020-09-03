package smartcraft.casegame.inGameEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.game.Kit;
import smartcraft.casegame.game.Navigation;

import java.util.Map;

public class CGPlayer {

  private CaseGame plugin = CaseGame.getInstance();
  private Player player;
  private Game game;
  private Team team;
  private Kit kit;
  private Location lastLocation;
  private Location spawnLocation;
  private boolean ready;

  public CGPlayer(Player p, Game game) {
    this.player = p;
    this.game = game;
  }

  public Player getPlayer() {
    return player;
  }

  public void giveKit(){
    if(kit == null){
      String kitName = plugin.getConfig().getString("defaultKit");
      kit = game.getKits().get(kitName);
    }
    player.getInventory().clear();
    player.getInventory().setContents(kit.getContent());
    player.getInventory().setHelmet(kit.getHelmet());
    player.getInventory().setChestplate(kit.getCheastplate());
    player.getInventory().setLeggings(kit.getLeggings());
    player.getInventory().setBoots(kit.getBoots());
  }

  public void forceTeam(){

    for (Map.Entry<Team.Teams, Team> entry : game.getTeams().entrySet()) {
      if(entry.getValue().isAvailable(game)){
        entry.getValue().add(this);
        setTeam(entry.getValue());
        break;
      }
    }
  }

  public void join() {
    lastLocation = player.getLocation();
    clearInventory();
    player.setMetadata("CaseGame", new FixedMetadataValue(plugin, game.getName()));
    player.setGameMode(GameMode.SURVIVAL);
    player.setInvulnerable(true);
    player.teleport(game.getLobby().getSpawnLocation());
    Navigation.give(player.getInventory());
    
  }

  public Location getSpawnLocation() {
    return spawnLocation;
  }

  public void leave() {
    leave(true);
  }
  public void leave(boolean remove) {
    player.removeMetadata("CaseGame", plugin);
    if (remove) game.getPlayers().remove(player.getName());
    game.getPlayers().forEach((key, value) -> value.getPlayer().sendMessage(ChatColor.RED + player.getName() + " вышел"));
    if(team != null){
      team.remove(this);
    }
    if(game.getLobby().isKnockOut()){
      game.getLobby().stopKnockOut();
    }
    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    clearInventory();
    player.teleport(lastLocation);

    if(game.getPlayers().isEmpty() && game.isStarted()){
      game.stop();
    }
  }

  private void clearInventory() {
    player.getInventory().clear();
    player.setLevel(0);
    player.setExp(0);
  }

  public Game getGame() {
    return game;
  }

  public void setKit(Kit kit) {
    this.kit = kit;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  public Kit getKit() {
    return kit;
  }

  public void setSpawnLocation(Location spawnLocation) {
    this.spawnLocation = spawnLocation;
  }
}
