package smartcraft.casegame.handlers;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.enemys.Gromilla;
import smartcraft.casegame.mobSkills.SkullGun;
import smartcraft.casegame.mobSkills.SummonKamikaze;

import java.util.List;

public class BossHandlers implements Listener {

  private static CaseGame plugin = CaseGame.getInstance();

  //
  //  BOSS: Gromilla
  //

  private SkullGun skullGun = new SkullGun();
  private SummonKamikaze summonKamikaze = new SummonKamikaze();

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {

    if (e.getEntity().hasMetadata("CgBoss") && e.getEntity().getMetadata("CgBoss").get(0).asString().equals(Gromilla.class.getName())) {
      if (e.getDamager() instanceof Player || e.getDamager() instanceof Arrow) {
        Monster boss = (Monster) e.getEntity();

        double maxHealth = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        if (boss.getHealth() >= maxHealth * 0.75) {
          boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6000, 6));
        } else {
          boss.removePotionEffect(PotionEffectType.SPEED);
          if(e.getDamager() instanceof  Arrow){
            e.getDamager().remove();
          }
          e.setCancelled(true);
          boss.damage(e.getFinalDamage());
        }
        if (boss.getHealth() <= maxHealth * 0.25 && !summonKamikaze.isActive(e.getEntity())) {
          Player p = null;
          if (e.getDamager() instanceof Player) {
            p = (Player) e.getDamager();
          } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            p = (Player) arrow.getShooter();
          }
          summonKamikaze.activate(e.getEntity(), p);
        } else if (boss.getHealth() <= 20 * 0.5 && !skullGun.isActive(e.getEntity())) {
          skullGun.activate(e.getEntity());
        }
      } else {
        e.setCancelled(true);
      }
    }
  }

  //
  // END
  //
}
