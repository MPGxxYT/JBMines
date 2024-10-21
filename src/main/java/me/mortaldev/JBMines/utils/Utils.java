package me.mortaldev.JBMines.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Utils {

  /**
   * Determines whether a given location is within a defined area.
   *
   * @param loc  The location to check.
   * @param loc1 The first corner of the defined area.
   * @param loc2 The second corner of the defined area.
   * @return {@code true} if the location is within the defined area, {@code false} otherwise.
   */
  public static boolean locationIsWithin(Location loc, Location loc1, Location loc2) {
    double x1 = Math.min(loc1.getX(), loc2.getX());
    double y1 = Math.min(loc1.getY(), loc2.getY());
    double z1 = Math.min(loc1.getZ(), loc2.getZ());
    double x2 = Math.max(loc1.getX(), loc2.getX());
    double y2 = Math.max(loc1.getY(), loc2.getY());
    double z2 = Math.max(loc1.getZ(), loc2.getZ());
    Location l1 = new Location(loc1.getWorld(), x1, y1, z1);
    Location l2 = new Location(loc1.getWorld(), x2, y2, z2);
    return loc.getBlockX() >= l1.getBlockX() && loc.getBlockX() <= l2.getBlockX()
        && loc.getBlockY() >= l1.getBlockY() && loc.getBlockY() <= l2.getBlockY()
        && loc.getBlockZ() >= l1.getBlockZ() && loc.getBlockZ() <= l2.getBlockZ();
  }

  /**
   * Returns the given value clamped between the minimum and maximum values.
   *
   * @param value The value to be clamped.
   * @param min The minimum value.
   * @param max The maximum value.
   * @return The clamped value. If the value is less than the minimum value, the minimum value is
   *     returned. If the value is greater than the maximum value, the maximum value is returned.
   *     Otherwise, the value itself is returned.
   */
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Converts the given ItemStack into a formatted item name string.
   *
   * @param itemStack The ItemStack to convert.
   * @return The formatted item name string.
   */
  public static String itemName(ItemStack itemStack) {
    return itemName(itemStack.getType());
  }

  public static <K, V> LinkedHashMap<K, V> reverseMap(LinkedHashMap<K, V> original) {
    LinkedHashMap<K, V> reversed = new LinkedHashMap<>();
    ListIterator<Map.Entry<K, V>> iterator =
        new ArrayList<>(original.entrySet()).listIterator(original.size());

    while (iterator.hasPrevious()) {
      Map.Entry<K, V> entry = iterator.previous();
      reversed.put(entry.getKey(), entry.getValue());
    }

    return reversed;
  }

  public static String itemName(Material material) {
    String name = material.getKey().getKey().replaceAll("_", " ").toLowerCase();
    // "lapis lazuli"

    String[] strings = name.split(" ");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < strings.length; i++) {
      String string = strings[i];
      if (string.length() > 1) {
        string = string.substring(0, 1).toUpperCase() + string.substring(1);
        if (i + 1 < strings.length) {
          stringBuilder.append(string).append(" ");
        } else {
          stringBuilder.append(string);
        }
      }
    }
    return stringBuilder.toString();
  }
}
