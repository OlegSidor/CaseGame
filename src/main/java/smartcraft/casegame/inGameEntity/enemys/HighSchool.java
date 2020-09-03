package smartcraft.casegame.inGameEntity.enemys;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

public class HighSchool extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public HighSchool(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location){
    Zombie hightSchool = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    hightSchool.setCustomNameVisible(true);
    hightSchool.setCustomName("Старшекласник");
    hightSchool.setMetadata("CgMob", new FixedMetadataValue(plugin, 25));
    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
    helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
    ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
    ItemStack leggins = new ItemStack(Material.DIAMOND_LEGGINGS);
    leggins.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
    boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
    hightSchool.getEquipment().setHelmet(helmet);
    hightSchool.getEquipment().setChestplate(chestplate);
    hightSchool.getEquipment().setLeggings(leggins);
    hightSchool.getEquipment().setBoots(boots);
    hightSchool.getEquipment().setItemInMainHand(sword);
    return hightSchool;
  }

}
