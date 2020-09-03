package smartcraft.casegame.inGameEntity.enemys;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

import java.util.concurrent.ThreadLocalRandom;

public class Child extends EnemyA implements IEnemy {
  private CaseGame plugin = CaseGame.getInstance();

  public Child(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location) {
      Zombie schoolBoy = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
      schoolBoy.setBaby(true);
      schoolBoy.setCustomName("Школьник");
      schoolBoy.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
      schoolBoy.setMetadata("CgMob", new FixedMetadataValue(plugin, 5));
      return schoolBoy;
  }
}
