package smartcraft.casegame.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import smartcraft.casegame.inGameEntity.CGPlayer;

import java.util.Map;

public class Kit {

  private ItemStack icon;
  private int slot;
  private String name;
  private ItemStack[] content;
  private ItemStack helmet;
  private ItemStack cheastplate;
  private ItemStack leggings;
  private ItemStack boots;

  Kit(String name, int slot, ItemStack icon, ItemStack[] content, ItemStack helmet, ItemStack cheastplate, ItemStack leggings, ItemStack boots){
    this.name = ChatColor.translateAlternateColorCodes('&', name);
    this.slot = slot;
    this.icon = icon;
    this.content = content;
    this.helmet = helmet;
    this.cheastplate = cheastplate;
    this.leggings = leggings;
    this.boots = boots;
  }

  public static void open(CGPlayer p){
    Inventory menu = Bukkit.createInventory(null, 9, "Наборы");
    for(Map.Entry<String, Kit> entry : p.getGame().getKits().entrySet()){
      ItemStack item = entry.getValue().icon.clone();
      if(p.getKit() != null && p.getKit().equals(entry.getValue())){
        Bukkit.broadcastMessage(p.getKit().name);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setDisplayName(p.getKit().getName() + ChatColor.GREEN+" (Выбран)");
        item.setItemMeta(itemMeta);
      }
      menu.setItem(entry.getValue().slot, item);
    }
    p.getPlayer().openInventory(menu);
  }

  public String getName() {
    return name;
  }

  public ItemStack[] getContent() {
    return content;
  }

  public ItemStack getHelmet() {
    return helmet;
  }

  public ItemStack getCheastplate() {
    return cheastplate;
  }

  public ItemStack getLeggings() {
    return leggings;
  }

  public ItemStack getBoots() {
    return boots;
  }
}
