package smartcraft.casegame.handlers;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.mobSkills.ISkill;

import java.lang.reflect.InvocationTargetException;

public class MobSkillsHandler implements Listener {

  private static CaseGame plugin = CaseGame.getInstance();


  @EventHandler
  public void onShot(EntityShootBowEvent e) {
    Arrow arrow = (Arrow) e.getProjectile();
    Entity shooter = (Entity) arrow.getShooter();
    if (shooter == null) return;
    if (shooter.hasMetadata("CgBowShotSkill")) {
      arrow.setMetadata("CgAttackSkill", shooter.getMetadata("CgBowShotSkill").get(0));
    }
  }


  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player) {
      Entity mob = e.getEntity();
      if (mob.hasMetadata("CgDamageSkill")) {
        String className = mob.getMetadata("CgDamageSkill").get(0).asString();
        activateSkill(className, mob, (Player) e.getDamager());
      }

    } else {
      Entity mob = e.getDamager();
      if (mob.hasMetadata("CgAttackSkill") && e.getEntity() instanceof Player) {
        String className = mob.getMetadata("CgAttackSkill").get(0).asString();
        activateSkill(className, mob, (Player) e.getEntity());
      }
    }
  }

  @EventHandler
  public void onDeath(EntityDeathEvent e) {
    if (e.getEntity().getKiller() != null) {
      LivingEntity mob = e.getEntity();
      if (mob.hasMetadata("CgDeathSkill")) {
        String className = mob.getMetadata("CgDeathSkill").get(0).asString();
        activateSkill(className, mob, e.getEntity().getKiller());
      }
    }
    if(e.getEntity().hasMetadata("CgMob")){
      e.getDrops().clear();
    }
  }



  public void activateSkill(String className, Entity mob, Player p) {
    try {
      Class SkillClass = Class.forName(className);
      Object Skill = SkillClass.getDeclaredConstructor().newInstance();
      ((ISkill) Skill).activate(mob, p);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e1) {
      e1.printStackTrace();
    }
  }

}
