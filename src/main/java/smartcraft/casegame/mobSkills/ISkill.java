package smartcraft.casegame.mobSkills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface ISkill {

  public void activate(Entity e, Player p);
  public void activate(Entity e);

}
