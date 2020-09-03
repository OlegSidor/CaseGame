package smartcraft.casegame.inGameEntity.enemys;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class Gromilla extends EnemyA implements IEnemy {

  private CaseGame plugin = CaseGame.getInstance();

  public Gromilla(String enemyClass) {
    super(enemyClass);
  }

  private int count = 0;

  @Override
  public LivingEntity spawn(Location location) {
    Monster gromilla = (Monster) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
    helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
    ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
    chestplate.addUnsafeEnchantment(Enchantment.THORNS, 3);
    ItemStack leggins = new ItemStack(Material.DIAMOND_LEGGINGS);
    leggins.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
    leggins.addUnsafeEnchantment(Enchantment.THORNS, 3);
    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
    boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);

    gromilla.setCustomNameVisible(true);
    gromilla.setCustomName("Громила");

    gromilla.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6000, 6));
    gromilla.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6000, 4));
    gromilla.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6000, 10));

    gromilla.setMetadata("CgMob", new FixedMetadataValue(this.plugin, 200));
    gromilla.setMetadata("CgBoss", new FixedMetadataValue(this.plugin, this.getClass().getName()));

    gromilla.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 9999));
    gromilla.setInvulnerable(true);
    gromilla.setGravity(false);
    gromilla.setCollidable(false);


    for (int i = 0; i < 40 * 5; i += 20) {
      new BukkitRunnable() {
        double y = 0;

        public void run() {
          Location particle = location.clone();
          y += 0.5;
          double x = 2 * cos(y);
          double z = 2 * sin(y) + 1;
          particle.add(x, y, z);
          location.getWorld().spawnParticle(Particle.SMOKE_LARGE, particle, 20, (double) 1, (double) 1, (double) 1, 0.1);
          location.getWorld().spawnParticle(Particle.FLAME, particle, 20, (double) 1, (double) 1, (double) 1, 0.3);
          particle.subtract(x, y, z);
          if (y >= 5) {
            this.cancel();
            if (count == 5) {
              location.getWorld().strikeLightningEffect(location);
              count = 0;
              gromilla.removePotionEffect(PotionEffectType.INVISIBILITY);
              gromilla.setInvulnerable(false);
              gromilla.setGravity(true);
              gromilla.setCollidable(true);

              gromilla.getEquipment().setHelmet(helmet);
              gromilla.getEquipment().setChestplate(chestplate);
              gromilla.getEquipment().setLeggings(leggins);
              gromilla.getEquipment().setBoots(boots);
              gromilla.getEquipment().setItemInMainHand(sword);


            }
            count++;
          }
        }
      }.runTaskTimer(plugin, i, 3);
    }

    return gromilla;
  }
}
