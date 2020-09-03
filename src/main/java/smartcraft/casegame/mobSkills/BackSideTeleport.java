package smartcraft.casegame.mobSkills;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BackSideTeleport implements ISkill {
  @Override
  public void activate(Entity e, Player p) {
    Location location = generateLocation(p);
    if (location != null) {
      Creature mob = (Creature) e;
      mob.setTarget(p);
      e.teleport(location);
    }
  }

  @Override
  public void activate(Entity e) {
    return;
  }

  private Location generateLocation(Player p) {
    int x = p.getLocation().getBlockX();
    int y = p.getLocation().getBlockY();
    int z = p.getLocation().getBlockZ();
    Location location = new Location(p.getWorld(), x, y, z);
    Vector inverseDirectionVec = p.getLocation().getDirection().normalize().multiply(-1);
    location.add(inverseDirectionVec);
    return location;
  }
}
