package smartcraft.casegame.inGameEntity.enemys;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;
import smartcraft.casegame.mobSkills.BackSideTeleport;
import smartcraft.casegame.mobSkills.BlindShot;

public class Rat extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public Rat(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location){
    Skeleton rat = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
    rat.setCustomNameVisible(true);
    rat.setCustomName("Крыса");
    rat.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
    rat.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
    rat.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    rat.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    rat.setMetadata("CgMob", new FixedMetadataValue(plugin, 8));
    rat.setMetadata("CgDamageSkill", new FixedMetadataValue(plugin, BackSideTeleport.class.getName()));
    rat.setMetadata("CgBowShotSkill", new FixedMetadataValue(plugin, BlindShot.class.getName()));

    return rat;
  }

}
