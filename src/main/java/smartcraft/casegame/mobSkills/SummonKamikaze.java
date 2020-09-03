package smartcraft.casegame.mobSkills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SummonKamikaze implements ISkill {


  private static CaseGame plugin = CaseGame.getInstance();
  private List<Entity> active = new ArrayList<>();
  private final int time = 30;

  @Override
  public void activate(Entity e, Player p) {
    active.add(e);
    new BukkitRunnable() {
      int coolDown = time;
      @Override
      public void run() {
        if (e.isDead()) {
          cancel();
          active.remove(e);
        }
        int min = 1;
        int max = 3;
        int count = ThreadLocalRandom.current().nextInt(min, max + 1);
        for (int i = 0; i < count; i++) {
          double biasX = ThreadLocalRandom.current().nextInt(0, 2);
          double biasY = ThreadLocalRandom.current().nextInt(0, 2);
          double biasZ = ThreadLocalRandom.current().nextInt(0, 2);
          Location loc = e.getLocation().add(biasX, biasY, biasZ);
          Zombie camicadze = (Zombie) e.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
          camicadze.setBaby(true);
          camicadze.setCustomName("Камикадзе");
          ItemStack chesptlate = new ItemStack(Material.LEATHER_CHESTPLATE);
          chesptlate.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 20);
          camicadze.getEquipment().setChestplate(chesptlate);
          camicadze.getEquipment().setHelmet(new ItemStack(Material.TNT));
          camicadze.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
          camicadze.setTarget(p);
          camicadze.setMetadata("CgMob", new FixedMetadataValue(plugin, 2));
          camicadze.setMetadata("CgAttackSkill", new FixedMetadataValue(plugin, Explode.class.getName()));
          Game game = plugin.getGame(p);
          game.getArena().getSpawnTimer().addMob(camicadze);
        }
        coolDown--;
        if (coolDown == 0) {
          cancel();
          active.remove(e);
        }
      }
    }.runTaskTimer(plugin, 0, 20 * 2);
  }

  @Override
  public void activate(Entity e) { }

  public boolean isActive(Entity e) {
    return active.contains(e);
  }
}
