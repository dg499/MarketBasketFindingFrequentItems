package mining.data;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BruteForceAlgorithm {

  static List<String> items = new ArrayList<>(
      Arrays.asList("bread", "milk", "yogurt", "eggs", "cereal", "chips", "chocolates", "cookies",
          "butter", "cheese", "almonds", "cashews", "walnuts", "pistachios", "oats", "broccoli",
          "carrots", "spinach", "sweetcorn", "greenpeas", "tomato", "orange", "apple", "banana",
          "strawberry", "blueberry", "blackberry", "avacado", "onion", "potato"));

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    FrequentItemsetGenerator generator = new FrequentItemsetGenerator();
    List<Set<String>> itemsetList = new ArrayList<>();
    @SuppressWarnings("resource")
    Scanner scanner = new Scanner(System.in);
    try {
      System.out.print("Please provide file name: ");
      String fileName = scanner.next();
      System.out.print("please provide support value: ");
      Double support = scanner.nextDouble();
      if (support <= 0.0 || support >= 1.0) {
        System.out.println("support value should lie between 0 and 1: " + support);
        System.exit(0);
      }
      System.out.print("provide confidence value: ");
      Double confidence = scanner.nextDouble();
      if (confidence <= 0.0 || confidence >= 1.0) {
        System.out.println("confidence value should lie between 0 and 1: " + confidence);
        System.exit(0);
      }
      try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          line = line.toLowerCase();
          String[] values = line.split(" ");
          Set<String> transaction = new HashSet<String>();
          for (int i = 0; i < values.length; i++) {
            if (values[i].equals("1"))
              transaction.add(items.get(i));
          }
          itemsetList.add(transaction);
        }
        bufferedReader.close();
      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
      FrequentItemsetDto dataSet = generator.generate(itemsetList, support, items);
      int i = 1;

      System.out.println("computed top level associations:");
      for (Set<String> itemset : dataSet.getFrequentItemsetList()) {
        if (itemset.size() == dataSet.getK() - 1)
          System.out.printf("%2d: %10s \n", i++, itemset);
      }

      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out
          .println("Total time taken in seconds with brute force approach: " + (totalTime / 1000));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Please provide valid input.");
    }
  }
}


