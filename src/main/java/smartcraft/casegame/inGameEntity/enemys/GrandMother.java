package smartcraft.casegame.inGameEntity.enemys;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

public class GrandMother extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public GrandMother(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location) {
    Zombie grandmother = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    grandmother.setCustomNameVisible(true);
    grandmother.setCustomName("Бабка из подъезда");
    grandmother.setBaby(false);
    grandmother.setMetadata("CgMob", new FixedMetadataValue(plugin, 5));
    grandmother.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
    grandmother.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
    grandmother.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
    grandmother.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
    grandmother.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
    return grandmother;
  }

}
