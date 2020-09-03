package smartcraft.casegame.navigationItems;

import org.bukkit.inventory.Inventory;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.CGPlayer;

public interface Item {

  void give(Inventory inventory);
  void click(CGPlayer player);
}
