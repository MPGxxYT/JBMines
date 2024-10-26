package me.mortaldev.JBMines.modules;

import me.mortaldev.JBMines.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ChanceMap<T> {
  LinkedHashMap<T, BigDecimal> table = new LinkedHashMap<>();

  /**
   * Returns the size of the table.
   *
   * @return the number of key-value mappings in the table
   */
  public int size() {
    return table.size();
  }

  /**
   * Updates the value associated with the given key in the ChanceMap table.
   *
   * @param key the key whose value is to be updated
   * @param newValue the new value to be associated with the key
   * @return true if the key existed and the update was successful, otherwise false
   */
  public boolean updateKey(T key, BigDecimal newValue){
    if (!table.containsKey(key)) {
      return false;
    }
    table.put(key, newValue);
    return true;
  }

  /**
   * Updates the value associated with the given key in the ChanceMap table.
   *
   * @param key the key whose value is to be updated
   * @param newValue the new value to be associated with the key
   * @return true if the key existed and the update was successful, otherwise false
   */
  public boolean updateKey(T key, Number newValue){
    return updateKey(key, new BigDecimal(newValue.toString()));
  }

  /**
   * Updates the value associated with the given key in the ChanceMap table.
   *
   * @param key the key whose value is to be updated
   * @param newValue the new value to be associated with the key
   * @return true if the key existed and the update was successful, otherwise false
   */
  public boolean updateKey(T key, String newValue){
    return updateKey(key, new BigDecimal(newValue));
  }

  public boolean balanceTable() {
    if (isBalanced()) {
      return false;
    }

    if (table.isEmpty()) {
      return false;
    }

    BigDecimal sum = BigDecimal.ZERO;
    for (BigDecimal value : table.values()) {
      sum = sum.add(value);
    }
    // Not Zero
    if (sum.compareTo(BigDecimal.ZERO) == 0) {
      Main.log("ERROR! '0' found in ChanceMap");
      return false;
    }
    LinkedHashMap<T, BigDecimal> newTable = new LinkedHashMap<>(table);
    int i = 0;
    int lastIteration = table.values().size() - 1;
    BigDecimal total = BigDecimal.ZERO;
    for (Map.Entry<T, BigDecimal> entry : newTable.entrySet()) {
      BigDecimal scaledValue;
      if (i == lastIteration) {
        scaledValue = new BigDecimal(1).subtract(total);
      } else {
        scaledValue = entry.getValue().divide(sum, 3, RoundingMode.HALF_UP);
      }
      total = total.add(scaledValue);
      entry.setValue(scaledValue.multiply(new BigDecimal(100)));
      i++;
    }
    table = newTable;
    return true;
  }

//  public boolean balanceTable() { // Optimized version for testing later
//    if (isBalanced()) {
//      return false;
//    }
//
//    if (table.isEmpty()) {
//      return false;
//    }
//
//    BigDecimal sum = BigDecimal.ZERO;
//    for (BigDecimal value : table.values()) {
//      sum = sum.add(value);
//    }
//
//    if (sum.compareTo(BigDecimal.ZERO) == 0) {
//      Main.log("ERROR! '0' found in ChanceMap");
//      return false;
//    }
//
//    BigDecimal scaleFactor = BigDecimal.valueOf(100).divide(sum, 2, RoundingMode.HALF_UP);
//    LinkedHashMap<T, BigDecimal> newTable = new LinkedHashMap<>();
//    BigDecimal remainingValue = BigDecimal.valueOf(100);
//    int size = table.size();
//    for (Map.Entry<T, BigDecimal> entry : table.entrySet()) {
//      if (size == 1) {
//        newTable.put(entry.getKey(), remainingValue);
//      } else {
//        BigDecimal newValue = entry.getValue().multiply(scaleFactor).setScale(2, RoundingMode.HALF_UP);
//        newTable.put(entry.getKey(), newValue);
//        remainingValue = remainingValue.subtract(newValue);
//      }
//      size--;
//    }
//
//    table = newTable;
//    return true;
//  }



  public synchronized void sort() {
    if (table.isEmpty()) {
      return;
    }

    List<Map.Entry<T, BigDecimal>> entries = new ArrayList<>(table.entrySet());
    entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

    LinkedHashMap<T, BigDecimal> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<T, BigDecimal> entry : entries) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    table = sortedMap;
  }

  /**
   * Checks if the total sum of values in the table is equal to 100.
   *
   * @return true if the sum of values is equal to 100, false otherwise
   */
  public boolean isBalanced() {
    return getTotal().compareTo(new BigDecimal(100)) == 0;
  }

  /**
   * Retrieves the total sum of values in the table, rounding to 2 decimal places using HALF_UP rounding mode.
   *
   * @return the total sum of values in the table
   */
  public synchronized BigDecimal getTotal() {
    if (table.isEmpty()) {
      return new BigDecimal("-1");
    }

    BigDecimal total = BigDecimal.ZERO;
    for (BigDecimal value : table.values()) {
      if (value == null) {
        continue;
      }
      total = total.add(value);
    }
    return total.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Puts the specified key and amount into the map, optionally balancing the table afterward.
   *
   * @param key the key to be inserted
   * @param amount the amount associated with the key
   * @param balanceAfter true to balance the table after insertion, false otherwise
   */
  public synchronized void put(T key, Number amount, boolean balanceAfter) {
    put(key, new BigDecimal(amount.toString()), balanceAfter);
  }

  /**
   * Puts the specified key and amount into the map, optionally balancing the table afterward.
   *
   * @param key the key to be inserted
   * @param amount the amount associated with the key
   * @param balanceAfter true to balance the table after insertion, false otherwise
   */
  public synchronized void put(T key, BigDecimal amount, boolean balanceAfter) {
    table.put(key, amount);
    if (balanceAfter) {
      balanceTable();
    }
  }

  /**
   * Removes the entry with the specified key from the table.
   *
   * @param key the key of the entry to be removed
   * @param balanceAfter true to balance the table after removal, false otherwise
   */
  public synchronized void remove(T key, boolean balanceAfter) {
    if (table.isEmpty()) {
      return;
    }
    table.remove(key);
    if (balanceAfter) {
      balanceTable();
    }
  }

  /**
   * Retrieves the key-value mappings in the table.
   *
   * @return a LinkedHashMap representing the key-value mappings in the table
   */
  public LinkedHashMap<T, BigDecimal> getTable() {
    return table;
  }

  /**
   * Sets the table with the given key-value mappings. This operation is synchronized to ensure thread safety.
   *
   * @param table a LinkedHashMap containing the key-value mappings to set in the table
   */
  public synchronized void setTable(LinkedHashMap<T, BigDecimal> table) {
    this.table = table;
  }
}
