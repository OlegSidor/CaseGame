package smartcraft.casegame.game;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.navigationItems.*;

import java.lang.reflect.InvocationTargetException;

public class Navigation {


  private static CaseGame plugin = CaseGame.getInstance();
  private static final Class[] items = new Class[]{
      KitSelect.class, TeamSelect.class, Ready.class, Exit.class
  };

  public static void give(Inventory inventory) {
    for (Class itemClass : items) {
      try {
        Object item = itemClass.getDeclaredConstructor().newInstance();
        ((Item) item).give(inventory);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean isNavigation(ItemStack item){
    if (item != null) {
      if (item.hasItemMeta()) {
        return item.getItemMeta().getPersistentDataContainer().has(plugin.navigationKey, PersistentDataType.STRING);
      }
    }
    return false;
  }

}
