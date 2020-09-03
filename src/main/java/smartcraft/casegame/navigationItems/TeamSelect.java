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
import smartcraft.casegame.inGameEntity.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamSelect implements Item {
  private static CaseGame plugin = CaseGame.getInstance();
  private final int slot = 1;

  @Override
  public void give(Inventory inventory) {
    ItemStack item = new ItemStack(Material.WHITE_WOOL);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(ChatColor.WHITE + "Команда");
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.getPersistentDataContainer().set(plugin.navigationKey, PersistentDataType.STRING, "TeamSelect");
    List<String> lore = new ArrayList();
    lore.add("Предмет предназначен для");
    lore.add("выброра команды за которую вы будете играть");
    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    inventory.setItem(slot, item);
  }

  @Override
  public void click(CGPlayer player) {
    Team.open(player);
  }
}
