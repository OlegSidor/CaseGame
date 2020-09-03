package smartcraft.casegame.navigationItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.inGameEntity.CGPlayer;

import java.util.ArrayList;
import java.util.List;

public class Exit implements Item {
  private static CaseGame plugin = CaseGame.getInstance();
  private final int slot = 8;

  @Override
  public void give(Inventory inventory) {
    ItemStack item = new ItemStack(Material.IRON_DOOR);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(ChatColor.RED + "Выход");
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.getPersistentDataContainer().set(plugin.navigationKey, PersistentDataType.STRING, "Exit");
    List<String> lore = new ArrayList();
    lore.add("Сам знаеш для чего...");
    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    inventory.setItem(slot, item);
  }

  @Override
  public void click(CGPlayer player) {
    player.leave();
  }
}
