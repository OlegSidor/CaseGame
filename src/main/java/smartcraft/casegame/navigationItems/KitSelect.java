package smartcraft.casegame.navigationItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Kit;
import smartcraft.casegame.inGameEntity.CGPlayer;

import java.util.ArrayList;
import java.util.List;

public class KitSelect implements Item {

  private static CaseGame plugin = CaseGame.getInstance();
  private final int slot = 0;

  @Override
  public void give(Inventory inventory) {
    ItemStack item = new ItemStack(Material.STONE_SWORD);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(ChatColor.BLUE + "Наборы");
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.getPersistentDataContainer().set(plugin.navigationKey, PersistentDataType.STRING, "KitSelect");
    List<String> lore = new ArrayList();
    lore.add("Предмет предназначем для");
    lore.add("выбора стартового набора");
    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    inventory.setItem(slot, item);
  }

  @Override
  public void click(CGPlayer player) {
    Kit.open(player);
  }
}
