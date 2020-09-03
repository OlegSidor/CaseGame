package smartcraft.casegame.mobSkills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class Explode implements ISkill {
    @Override
    public void activate(Entity e, Player p) {
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        e.getLocation().getWorld().createExplosion(x, y, z, 5, false, false);
        ((Monster)e).damage(9999);
    }
    @Override
    public void activate(Entity e) {}
}
