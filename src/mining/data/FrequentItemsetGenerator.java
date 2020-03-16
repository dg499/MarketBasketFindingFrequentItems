/* FrequentItemsetGenerator.java */
package mining.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FrequentItemsetGenerator {

  public FrequentItemsetDto generate(List<Set<String>> transactionList, double minimumSupport,
      List<String> itemAttributes) {
    Objects.requireNonNull(transactionList, "The itemset list is empty.");
    checkSupport(minimumSupport);

    if (transactionList.isEmpty()) {
      return null;
    }

    Map<Set<String>, Integer> supportCountMap = new HashMap<>();

    List<Set<String>> frequentItemList =
        findFreqItems(transactionList, supportCountMap, minimumSupport, itemAttributes);

    Map<Integer, List<Set<String>>> map = new HashMap<>();
    map.put(1, frequentItemList);

    int k = 1;

    do {
      ++k;
      List<Set<String>> itemsSet = new ArrayList<>();
      getAllItemSets(itemAttributes, itemAttributes.size(), k, itemsSet);
      System.out.println("generated possible " + k + "-itemsets " + itemsSet.size());

      List<Set<String>> candidateList = itemsSet;

      for (Set<String> transaction : transactionList) {
        List<Set<String>> candidateList2 = subset(candidateList, transaction);

        for (Set<String> itemset : candidateList2) {
          supportCountMap.put(itemset, supportCountMap.getOrDefault(itemset, 0) + 1);
        }
      }



      map.put(k,
          getNextItemsets(candidateList, supportCountMap, minimumSupport, transactionList.size()));

      System.out.println("frequent Item sets List from possible " + k + "-item sets of "
          + itemsSet.size() + " are:");
      map.get(k).stream().forEach(s -> System.out.println(s));
      System.out.println();

    } while (!map.get(k).isEmpty());

    return new FrequentItemsetDto(extractFrequentItemsets(map), supportCountMap, minimumSupport,
        transactionList.size(), k);
  }

  private List<Set<String>> extractFrequentItemsets(Map<Integer, List<Set<String>>> map) {
    List<Set<String>> ret = new ArrayList<>();

    // ret.addAll(map.get(k));
    for (List<Set<String>> itemsetList : map.values()) {
      ret.addAll(itemsetList);
    }

    return ret;
  }

  private List<Set<String>> getNextItemsets(List<Set<String>> candidateList,
      Map<Set<String>, Integer> supportCountMap, double minimumSupport, int transactions) {
    List<Set<String>> ret = new ArrayList<>(candidateList.size());

    for (Set<String> itemset : candidateList) {
      if (supportCountMap.containsKey(itemset)) {
        int supportCount = supportCountMap.get(itemset);
        double support = 1.0 * supportCount / transactions;

        if (support >= minimumSupport) {
          ret.add(itemset);
        }
      }
    }

    return ret;
  }

  private List<Set<String>> subset(List<Set<String>> candidateList, Set<String> transaction) {
    List<Set<String>> ret = new ArrayList<>(candidateList.size());

    for (Set<String> candidate : candidateList) {
      if (transaction.containsAll(candidate)) {
        ret.add(candidate);
      }
    }

    return ret;
  }

  @SuppressWarnings({"unused"})
  private List<Set<String>> generateCandidates(List<Set<String>> itemsetList) {
    List<List<String>> list = new ArrayList<>(itemsetList.size());

    for (Set<String> itemset : itemsetList) {
      List<String> l = new ArrayList<>(itemset);
      Collections.<String>sort(l, ITEM_COMPARATOR);
      list.add(l);
    }

    int listSize = list.size();

    List<Set<String>> ret = new ArrayList<>(listSize);

    for (int i = 0; i < listSize; ++i) {
      for (int j = i + 1; j < listSize; ++j) {
        Set<String> candidate = mergeItemSets(list.get(i), list.get(j));

        if (candidate != null) {
          ret.add(candidate);
        }
      }
    }

    return ret;
  }



  private static final Comparator<Object> ITEM_COMPARATOR = new Comparator<Object>() {

    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object o1, Object o2) {
      return ((Comparable<Object>) o1).compareTo(o2);
    }

  };

  private List<Set<String>> findFreqItems(List<Set<String>> itemsetList,
      Map<Set<String>, Integer> supportCountMap, double minimumSupport,
      List<String> itemAttributes) {
    Map<String, Integer> map = new HashMap<>();

    List<Set<String>> itemsSet = new ArrayList<>();
    getAllItemSets(itemAttributes, itemAttributes.size(), 1, itemsSet);
    System.out.println("generated possible " + 1 + "-itemsets " + itemsSet.size());

    // Count the support counts of each item.
    for (Set<String> itemset : itemsetList) {
      for (String item : itemset) {
        Set<String> tmp = new HashSet<>(1);
        tmp.add(item);

        if (supportCountMap.containsKey(tmp)) {
          supportCountMap.put(tmp, supportCountMap.get(tmp) + 1);
        } else {
          supportCountMap.put(tmp, 1);
        }

        map.put(item, map.getOrDefault(item, 0) + 1);
      }
    }

    List<Set<String>> frequentItemsetList = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      if (1.0 * entry.getValue() / itemsetList.size() >= minimumSupport) {
        Set<String> itemset = new HashSet<>(1);
        itemset.add(entry.getKey());
        frequentItemsetList.add(itemset);
      }
    }

    System.out.println(
        "frequent Item sets List from possible " + 1 + "-item sets of " + itemsSet.size() + "are:");
    frequentItemsetList.stream().forEach(s -> System.out.println(s));
    System.out.println();
    return frequentItemsetList;
  }

  private void checkSupport(double support) {
    if (Double.isNaN(support)) {
      throw new IllegalArgumentException("The input support is NaN.");
    }

    if (support > 1.0) {
      throw new IllegalArgumentException(
          "The input support is too large: " + support + ", " + "should be at most 1.0");
    }

    if (support < 0.0) {
      throw new IllegalArgumentException(
          "The input support is too small: " + support + ", " + "should be at least 0.0");
    }
  }

  private void combinationUtil(List<String> arr, int n, int r, int index, String data[], int i,
      List<Set<String>> itemsSet) {
    if (index == r) {

      Set<String> items = new HashSet<>();
      for (int j = 0; j < r; j++) {
        items.add(data[j]);
      }
      itemsSet.add(items);
      return;
    }

    if (i >= n)
      return;

    data[index] = arr.get(i);
    combinationUtil(arr, n, r, index + 1, data, i + 1, itemsSet);

    combinationUtil(arr, n, r, index, data, i + 1, itemsSet);
  }

  private void getAllItemSets(List<String> arr, int n, int r, List<Set<String>> itemsSet) {
    String data[] = new String[r];
    combinationUtil(arr, n, r, 0, data, 0, itemsSet);
  }

  private Set<String> mergeItemSets(List<String> itemset1, List<String> itemset2) {
    int length = itemset1.size();

    for (int i = 0; i < length - 1; ++i) {
      if (!itemset1.get(i).equals(itemset2.get(i))) {
        return null;
      }
    }

    if (itemset1.get(length - 1).equals(itemset2.get(length - 1))) {
      return null;
    }

    Set<String> ret = new HashSet<>(length + 1);

    for (int i = 0; i < length - 1; ++i) {
      ret.add(itemset1.get(i));
    }

    ret.add(itemset1.get(length - 1));
    ret.add(itemset2.get(length - 1));
    return ret;
  }
}
