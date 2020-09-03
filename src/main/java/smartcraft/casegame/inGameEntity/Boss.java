package smartcraft.casegame.inGameEntity;


import org.bukkit.Bukkit;
import smartcraft.casegame.inGameEntity.enemys.Gromilla;

import java.lang.reflect.InvocationTargetException;

public enum Boss {
  GROMILLA(Gromilla.class);


  private final Class object;

  Boss(Class object) {
    this.object = object;
  }


  public Object getObject() {
    try {
      return object.getDeclaredConstructor(String.class).newInstance(object.getClass().getName());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
}
