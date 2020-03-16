/* FrequentItemsetDto.java */
package mining.data;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class FrequentItemsetDto {
  private final double minimumSupport;
  private final int numberOfTransactions;
  private int k;
  private final List<Set<String>> freqItemsetList;
  private final Map<Set<String>, Integer> supportCountMap;

  FrequentItemsetDto(List<Set<String>> frequentItemsetList,
      Map<Set<String>, Integer> supportCountMap, double minSupport, int transactionNumber, int k) {
    this.freqItemsetList = frequentItemsetList;
    this.supportCountMap = supportCountMap;
    this.minimumSupport = minSupport;
    this.numberOfTransactions = transactionNumber;
    this.k = k;
  }

  public List<Set<String>> getFrequentItemsetList() {
    return freqItemsetList;
  }

  public Map<Set<String>, Integer> getSupportCountMap() {
    return supportCountMap;
  }

  public double getMinimumSupport() {
    return minimumSupport;
  }

  public int getTransactionNumber() {
    return numberOfTransactions;
  }

  public double getSupport(Set<String> itemset) {
    return 1.0 * supportCountMap.get(itemset) / numberOfTransactions;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }
}

