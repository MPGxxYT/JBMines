package me.mortaldev.JBMines.modules.chamber;

import me.mortaldev.JBMines.modules.ChanceMap;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.crudapi.CRUD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;

public class Chamber implements CRUD.Identifiable {

  private String id;
  private BlockFace chamberFacing;
  private Map<String, Object> chestLocation = new HashMap<>();
  // String is BlockData, List is all locations where the block should be.
  private HashMap<String, Set<Vector>> blockList = new HashMap<>();
  private ChanceMap<String> lootTable = new ChanceMap<>();

  public Chamber(String id) {
    this.id = id;
  }

  public BlockFace getChamberFacing() {
    return chamberFacing;
  }

  public void setChamberFacing(BlockFace chamberFacing) {
    this.chamberFacing = chamberFacing;
  }

  public void setChamberFacing(Location chamberFacing) {
    System.out.println(chamberFacing.getDirection());
  }

  public Location getChestLocation() {
    return Location.deserialize(chestLocation);
  }

  public void setChestLocation(Location chestLocation) {
    this.chestLocation = chestLocation.serialize();
  }

  public void addToLootTable(ItemStack itemStack) {
    String serializedItemStack = ItemStackHelper.serialize(itemStack);
    lootTable.put(serializedItemStack, 50D, false);
  }

  public void removeFromLootTable(ItemStack itemStack) {
    String serializedItemStack = ItemStackHelper.serialize(itemStack);
    lootTable.remove(serializedItemStack, false);
  }

  public HashMap<ItemStack, BigDecimal> getLootTable() {
    HashMap<ItemStack, BigDecimal> convertedTable = new HashMap<>();
    for (Map.Entry<String, BigDecimal> entry : lootTable.getTable().entrySet()) {
      convertedTable.put(ItemStackHelper.deserialize(entry.getKey()), entry.getValue());
    }
    return convertedTable;
  }

  public void setLootTable(HashMap<ItemStack, BigDecimal> lootTable) {
    LinkedHashMap<String, BigDecimal> convertedTable = new LinkedHashMap<>();
    for (Map.Entry<ItemStack, BigDecimal> entry : lootTable.entrySet()) {
      convertedTable.put(ItemStackHelper.serialize(entry.getKey()), entry.getValue());
    }
    this.lootTable.setTable(convertedTable);
  }

  public void clearBlockList() {
    blockList.clear();
  }

  public void addToBlockList(Block block) {
    String blockDataAsString = block.getBlockData().getAsString();
    Vector vector = block.getLocation().toVector();
    blockList.computeIfAbsent(blockDataAsString, k -> new HashSet<>()).add(vector);
  }

  /**
   * Applies the given {@code blockDataConsumer} to all entries in the block list.
   * The consumer is given the {@link BlockData} and the set of all locations where
   * the block should be.
   *
   * @param blockDataConsumer the consumer to apply to all entries in the block list
   */
  public void processBlockList(BiConsumer<BlockData, Set<Vector>> blockDataConsumer) {
    for (Map.Entry<String, Set<Vector>> entry : blockList.entrySet()) {
      BlockData blockData = Bukkit.createBlockData(entry.getKey());
      blockDataConsumer.accept(blockData, entry.getValue());
    }
  }

  public HashMap<BlockData, Set<Vector>> getBlockList() {
    HashMap<BlockData, Set<Vector>> returnMap = new HashMap<>();
    for (Map.Entry<String, Set<Vector>> entry : blockList.entrySet()) {
      BlockData blockData = Bukkit.createBlockData(entry.getKey());
      returnMap.put(blockData, entry.getValue());
    }
    return returnMap;
  }

  // CRUD

  @Override
  public String getID() {
    return id;
  }

  public void save() {
    ChamberCRUD.getInstance().saveData(this);
  }

  public void delete() {
    ChamberCRUD.getInstance().deleteData(this);
  }
}
