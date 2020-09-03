package smartcraft.casegame.inGameEntity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface IEnemy {
  public LivingEntity spawn(Location location);
}
