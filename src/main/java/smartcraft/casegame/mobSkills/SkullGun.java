package smartcraft.casegame.mobSkills;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import smartcraft.casegame.CaseGame;

import java.util.ArrayList;
import java.util.List;

public class SkullGun implements ISkill {


  private static CaseGame plugin = CaseGame.getInstance();
  private final int shotCount = 25;
  private List<Entity> active = new ArrayList<>();

  @Override
  public void activate(Entity e, Player p) {

  }

  @Override
  public void activate(Entity e) {
    active.add(e);
    new BukkitRunnable() {
      int count = 0;

      @Override
      public void run() {
        if (e.isDead()) {
          cancel();
          active.remove(e);
        }
        Player pl = plugin.getNearPlayer(e.getLocation());
        if (pl == null) {
          return;
        }

        double deltaX = pl.getLocation().getX() - e.getLocation().getX();
        double deltaY = pl.getLocation().getY() - e.getLocation().getY();
        double deltaZ = pl.getLocation().getZ() - e.getLocation().getZ();

        Vector vec = new Vector(deltaX, deltaY, deltaZ + 0.5);
        vec.normalize();
        vec.multiply(3);

        WitherSkull w = ((Monster) e).launchProjectile(WitherSkull.class);
        w.setVelocity(vec);

        w.setMetadata("CgBossBullet", new FixedMetadataValue(plugin, true));

        e.getLocation().getWorld().playSound(e.getLocation(), Sound.ENTITY_WITHER_SHOOT, 10, 10);
        pl.spawnParticle(Particle.FLAME, e.getLocation(), 50, (double) 1, (double) 1, (double) 1, 0.5);
        count++;
        if (count == shotCount) {
          cancel();
          active.remove(e);
          count = 0;
        }
      }
    }.runTaskTimer(plugin, 0, 20);
  }

  public boolean isActive(Entity e) {
    return active.contains(e);
  }
}
