package smartcraft.casegame.mobSkills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindShot implements ISkill {
    @Override
    public void activate(Entity e, Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
    }
    @Override
    public void activate(Entity e) {}
}
