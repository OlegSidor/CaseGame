package smartcraft.casegame.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import smartcraft.casegame.CaseGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class AirDrop {

  private CaseGame plugin = CaseGame.getInstance();
  private Game game;
  private EditSession airdropSession = null;
  private Map<String, List<ItemStack>> packs = new HashMap<>();
  private Random random = new Random();
  private int maxCount = 0;
  private List<String> allowedAirDrops = new ArrayList<>();

  public AirDrop(Game game) {
    this.game = game;
  }

  public void spawn(Location loc) {
    String name = allowedAirDrops.get(random.nextInt(allowedAirDrops.size()));
    spawn(loc, name);
  }
  public void spawn(Location loc, String name) {
    remove();
    try {
      File schematic = new File(plugin.getDataFolder() + File.separator + "Airdrops/" + name + ".schem");
      ClipboardFormat format = ClipboardFormats.findByFile(schematic);
      ClipboardReader reader = format.getReader(new FileInputStream(schematic));
      Clipboard clipboard = reader.read();

      ClipboardHolder holder = new ClipboardHolder(clipboard);
      EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
      Operation operation = holder.createPaste(editSession).to(BlockVector3.at(loc.getBlockX(), loc.getBlockY() - 2, loc.getBlockZ())).ignoreAirBlocks(true).build();
      Operations.complete(operation);
      editSession.flushSession();
      airdropSession = editSession;
      Location topLeft = new Location(loc.getWorld(), loc.getBlockX() + clipboard.getWidth(), loc.getBlockY() + clipboard.getHeight(), loc.getBlockZ() + clipboard.getLength());
      Location bottomRight = new Location(loc.getWorld(), loc.getBlockX() - clipboard.getWidth(), loc.getBlockY() - clipboard.getHeight(), loc.getBlockZ() - clipboard.getLength());
      fillChests(topLeft, bottomRight);
      game.getPlayers().values().forEach(player -> {
        player.getPlayer().sendMessage(ChatColor.GOLD + "Аирдроп появился на координатах: ");
        player.getPlayer().sendMessage(ChatColor.RED + "X: " + loc.getBlockX() + " Z: " + loc.getBlockZ());
      });
      plugin.getLogger().info("AirDrop spawned W: " + loc.getWorld().getName() + " X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
    } catch (WorldEditException | IOException e) {
      plugin.getLogger().warning("Can\'t spawn AirDrop");
      e.printStackTrace();
    }
  }

  public void remove() {
    if (airdropSession != null) {
      EditSession newEditSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(airdropSession.getWorld(), -1, null, null);
      airdropSession.undo(newEditSession);
      airdropSession.flushQueue();
      game.getPlayers().values().forEach(player -> player.getPlayer().sendMessage(ChatColor.RED+"Последний аирдроп уничтожен!"));
      airdropSession = null;
    }
  }

  public void fillChests(Location loc1, Location loc2) {
    int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
    int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

    int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
    int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

    int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
    int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

    for (int x = bottomBlockX; x <= topBlockX; x++) {
      for (int z = bottomBlockZ; z <= topBlockZ; z++) {
        for (int y = bottomBlockY; y <= topBlockY; y++) {
          Block block = loc1.getWorld().getBlockAt(x, y, z);
          if (block instanceof Chest || block.getType() == Material.CHEST || block.getType().equals(Material.CHEST)) {
            Chest chest = (Chest) block.getState();
            Object[] pack = packs.values().toArray();
            int random_ = random.nextInt(pack.length);
            Object randomPack = pack[random_];
            ((List<ItemStack>) randomPack).forEach(itemStack -> chest.getBlockInventory().addItem(itemStack));
          }
        }
      }
    }
  }

  public Map<String, List<ItemStack>> getPacks() {
    return packs;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }

  public List<String> getAllowedAirDrops() {
    return allowedAirDrops;
  }
}
