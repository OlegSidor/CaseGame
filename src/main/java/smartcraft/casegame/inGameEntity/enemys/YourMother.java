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
import smartcraft.casegame.mobSkills.SummonChild;

public class YourMother extends EnemyA implements IEnemy {


  private CaseGame plugin = CaseGame.getInstance();

  public YourMother(String enemyClass) {
    super(enemyClass);
  }

  @Override
  public LivingEntity spawn(Location location){
    Zombie yourMother = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    yourMother.setCustomNameVisible(true);
    yourMother.setCustomName("Яжемать");
    yourMother.setMetadata("CgMob", new FixedMetadataValue(plugin, 1));
    yourMother.setMetadata("CgDeathSkill", new FixedMetadataValue(plugin, SummonChild.class.getName()));
    return yourMother;
  }

}
