package smartcraft.casegame.mobSkills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;
import smartcraft.casegame.inGameEntity.enemys.Child;

import java.util.concurrent.ThreadLocalRandom;

public class SummonChild implements ISkill {
  private CaseGame plugin = CaseGame.getInstance();

  @Override
  public void activate(Entity e, Player p) {

    if(p.hasMetadata("CaseGame")) {
      int min = 5;
      int max = 8;
      String arenaName = p.getMetadata("CaseGame").get(0).asString();
      Game game = plugin.getGames().get(arenaName);
      int count = ThreadLocalRandom.current().nextInt(min, max + 1);
      for (int i = 0; i < count; i++) {
        double biasX = ThreadLocalRandom.current().nextInt(0, 2);
        double biasY = ThreadLocalRandom.current().nextInt(0, 2);
        double biasZ = ThreadLocalRandom.current().nextInt(0, 2);
        Location loc = e.getLocation().add(biasX, biasY, biasZ);
        LivingEntity child = new Child(Child.class.getName()).spawn(loc);
        if (child instanceof Zombie) {
          ((Zombie) child).setTarget(p);
        }
        game.getArena().getSpawnTimer().addMob(child);
      }
    }
  }

  @Override
  public void activate(Entity e) {
  }
}
