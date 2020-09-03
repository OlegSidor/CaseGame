package smartcraft.casegame.inGameEntity.enemys;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.EnemyA;
import smartcraft.casegame.inGameEntity.IEnemy;

public class Gopar extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public Gopar(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location){
    Zombie gopar = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    gopar.setCustomNameVisible(true);
    if (gopar.isBaby()) {
      gopar.setCustomName("Гопарьок");
      gopar.setMetadata("CgMob", new FixedMetadataValue(plugin, 2));
    } else {
      gopar.setCustomName("Гопарь");
      gopar.setMetadata("CgMob", new FixedMetadataValue(plugin, 1));
    }
    return gopar;
  }
}
