package smartcraft.casegame.Tools;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import smartcraft.casegame.CaseGame;
import smartcraft.casegame.game.Game;

import java.util.ArrayList;

public class TemporaryBlock {

    private CaseGame plugin = CaseGame.getInstance();
    private ItemStack BlockItem = new ItemStack(Material.GLASS);
    private int removeTime = 0;
    private int count = 0;
    private int maxHeight = 0;

    public TemporaryBlock() {
        ItemMeta BlockItemMeta = BlockItem.getItemMeta();
        BlockItemMeta.setDisplayName(ChatColor.AQUA + "Временный блок");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Данный блок исчезнет через несколько секунд");
        BlockItemMeta.setLore(lore);
        BlockItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        BlockItemMeta.getPersistentDataContainer().set(plugin.temporaryBlockkKey, PersistentDataType.SHORT, (short) 1);
        BlockItem.setItemMeta(BlockItemMeta);
    }

    public void give(Player p, int count){
        for (int i = 0; i < count; i++) {
            p.getInventory().addItem(BlockItem);
        }
    }

    public ItemStack getBlockItem(){
        return BlockItem;
    }

    public int getRemoveTime() {
        return removeTime;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRemoveTime(int removeTime) {
        this.removeTime = removeTime;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getCount() {
        return count;
    }
}
