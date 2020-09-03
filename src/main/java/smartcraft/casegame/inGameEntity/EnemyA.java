package smartcraft.casegame.inGameEntity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import smartcraft.casegame.navigationItems.Item;

import java.lang.reflect.InvocationTargetException;

public abstract class EnemyA {

  private String enemyClass;

  public EnemyA(String enemyClass) {
    this.enemyClass = enemyClass;
  }

  public LivingEntity spawn(Location location) {
    try {
      Object enemy = Class.forName(enemyClass).getDeclaredConstructor().newInstance(enemyClass);
      return ((IEnemy) enemy).spawn(location);
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
