package smartcraft.casegame.inGameEntity.enemys;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

public class AngryHighSchool extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public AngryHighSchool(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location){
    Zombie angryHighSchool = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    angryHighSchool.setCustomNameVisible(true);
    angryHighSchool.setCustomName("Злой Старшекласник");
    angryHighSchool.setMetadata("CgMob", new FixedMetadataValue(plugin, 25));
    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
    helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
    ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
    ItemStack leggins = new ItemStack(Material.DIAMOND_LEGGINGS);
    leggins.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
    boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7);
    angryHighSchool.getEquipment().setHelmet(helmet);
    angryHighSchool.getEquipment().setChestplate(chestplate);
    angryHighSchool.getEquipment().setLeggings(leggins);
    angryHighSchool.getEquipment().setBoots(boots);
    angryHighSchool.getEquipment().setItemInMainHand(sword);
    return angryHighSchool;
  }

}
