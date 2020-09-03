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

public class Ready implements Item {
  private static CaseGame plugin = CaseGame.getInstance();
  private final int slot = 4;

  @Override
  public void give(Inventory inventory) {
    ItemStack item = new ItemStack(Material.DIAMOND);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(ChatColor.GREEN + "Готов!");
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.getPersistentDataContainer().set(plugin.navigationKey, PersistentDataType.STRING, "Ready");
    List<String> lore = new ArrayList();
    lore.add("Если Вы готовы начать, нажимай!");
    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    inventory.setItem(slot, item);
  }

  @Override
  public void click(CGPlayer player) {
    player.setReady(true);
    player.getPlayer().getInventory().setItemInMainHand(null);
    if (player.getGame().getLobby().isAllReady()) {
      player.getGame().start();
    } else
      player.getPlayer().sendMessage(ChatColor.GREEN + "Вы готовы, ждите остальных игроков");
  }
}
