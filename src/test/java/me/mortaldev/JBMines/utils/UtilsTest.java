package me.mortaldev.JBMines.utils;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

  @org.junit.jupiter.api.Test
  void locationIsWithin() {

    Location location = new Location(null, 5, 5, 5);
    Location cornerOne = new Location(null, 0, 0, 0);
    Location cornerTwo = new Location(null, 10, 10, 10);
    assertTrue(Utils.locationIsWithin(location, cornerOne, cornerTwo));

    // Edge of the cube
    location = new Location(null, 5, 10, 5);
    assertTrue(Utils.locationIsWithin(location, cornerOne, cornerTwo));

    // Outside the cube
    location = new Location(null, -5, 5, 5);
    assertFalse(Utils.locationIsWithin(location, cornerOne, cornerTwo));

    // Outside the cube on z axis
    location = new Location(null, 5, 5, 11);
    assertFalse(Utils.locationIsWithin(location, cornerOne, cornerTwo));
  }

  @org.junit.jupiter.api.Test
  void clamp() {
    assertEquals(5, Utils.clamp(5, 0, 10)); // 5 is within 0 and 10
    assertEquals(0, Utils.clamp(0, 0, 10)); // 0 is within 0 and 10
    assertEquals(10, Utils.clamp(15, 0, 10)); // 15 is not within 0 and 10
    assertEquals(0, Utils.clamp(-5, 0, 10)); // -5 is not within 0 and 10
  }

  @org.junit.jupiter.api.Test
  void reverseMap() {

    LinkedHashMap<String, String> original = new LinkedHashMap<>();
    original.put("a", "b");
    original.put("c", "d");
    original.put("e", "f");

    LinkedHashMap<String, String> reversed = Utils.reverseMap(original);
    assertEquals("f", reversed.values().toArray()[0]);

  }

  @org.junit.jupiter.api.Test
  void itemName() {
    assertEquals("Stone", Utils.itemName(Material.STONE));
    assertEquals("Lapis Lazuli", Utils.itemName(Material.LAPIS_LAZULI));}
}
