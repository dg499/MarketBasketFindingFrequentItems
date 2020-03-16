package mining.data;

import java.io.*;
import java.util.*;

public class AprioriAlgorithm {
  List<String> items = new ArrayList<>(
      Arrays.asList("bread", "milk", "yogurt", "eggs", "cereal", "chips", "chocolates", "cookies",
          "butter", "cheese", "almonds", "cashews", "walnuts", "pistachios", "oats", "broccoli",
          "carrots", "spinach", "sweetcorn", "greenpeas", "tomato", "orange", "apple", "banana",
          "strawberry", "blueberry", "blackberry", "avacado", "onion", "potato"));

  List<String> totalItems = new ArrayList<String>();
  String splitter = " ";
  String fileName;
  double support;
  double confidence;
  String inputData[];
  Hashtable<String, Integer> supportportData = new Hashtable<String, Integer>();

  private void candidateGeneration(int transactionCount) {
    String temp1, temp2;
    StringTokenizer token1, token2;
    ArrayList<String> tempItems = new ArrayList<String>();

    if (transactionCount == 1)
      for (int item = 1; item <= items.size(); item++)
        tempItems.add("" + item);
    else if (transactionCount == 2)
      for (int i = 0; i < totalItems.size(); i++) {
        token1 = new StringTokenizer(totalItems.get(i));
        temp1 = token1.nextToken();
        for (int j = i + 1; j < totalItems.size(); j++) {
          token2 = new StringTokenizer(totalItems.get(j));
          temp2 = token2.nextToken();
          tempItems.add(temp1 + " " + temp2);
        }
      }
    else
      for (int i = 0; i < totalItems.size(); i++) {
        for (int j = i + 1; j < totalItems.size(); j++) {
          temp1 = "";
          temp2 = "";
          token1 = new StringTokenizer(totalItems.get(i));
          token2 = new StringTokenizer(totalItems.get(j));
          for (int s = 0; s < transactionCount - 2; s++) {
            temp1 = temp1 + " " + token1.nextToken();
            temp2 = temp2 + " " + token2.nextToken();
          }
          if (temp2.compareToIgnoreCase(temp1) == 0)
            tempItems.add((temp1 + " " + token1.nextToken() + " " + token2.nextToken()).trim());
        }
      }
    totalItems.clear();
    totalItems = new ArrayList<String>(tempItems);
  }

  private void frequencyItemGenerator(int a) {
    StringTokenizer tokenIt, tokenFil;
    String line = null;
    boolean fl;
    boolean transactions[] = new boolean[items.size()];
    Vector<String> frequentItems = new Vector<String>();
    BufferedReader br = null;
    int item_counter[] = new int[totalItems.size()];
    int lines_count = 0;
    try {
      br = new BufferedReader(new FileReader(fileName));
      while ((line = br.readLine()) != null) {
        tokenFil = new StringTokenizer(line, splitter);
        for (int j = 0; j < items.size(); j++) {
          transactions[j] = (tokenFil.nextToken().compareToIgnoreCase(inputData[j]) == 0);
        }
        for (int i = 0; i < totalItems.size(); i++) {
          fl = false;
          tokenIt = new StringTokenizer(totalItems.get(i));
          while (tokenIt.hasMoreTokens()) {
            fl = (transactions[Integer.valueOf(tokenIt.nextToken()) - 1]);
            if (!fl)
              break;
          }
          if (fl)
            item_counter[i]++;
        }
        lines_count++;
      }
      for (int i = 0; i < totalItems.size(); i++) {
        if ((item_counter[i] / (double) lines_count) >= support) {
          frequentItems.add(totalItems.get(i));
          supportportData.put(totalItems.get(i), item_counter[i]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e);
    }
    totalItems.clear();
    totalItems = new ArrayList<String>(frequentItems);
  }

  public void initialize() {
    inputData = new String[items.size()];
    for (int i = 0; i < inputData.length; i++)
      inputData[i] = "1";

    totalItems = new ArrayList<String>();

    int freq_count = 0;
    do {
      String itemsData = null;
      String[] input_split_data = null;

      freq_count++;
      candidateGeneration(freq_count);
      frequencyItemGenerator(freq_count);

      if (totalItems.size() != 0) {
        System.out.println("Frequent " + freq_count + " set of items are: ");
        System.out.println("=======================");
        for (int i = 0; i < totalItems.size(); i++) {
          itemsData = totalItems.get(i);
          input_split_data = itemsData.split(splitter);
          int tot_confidence = supportportData.get(itemsData);
          int ind_confidence = 0;

          for (int j = 0; j < input_split_data.length; j++) {
            if (j == 0) {
              ind_confidence = supportportData.get(input_split_data[j]);
              if ((ind_confidence / tot_confidence) >= confidence) {
                System.out.print(" " + items.get(Integer.parseInt(input_split_data[j]) - 1));
                if (freq_count != 1)
                  System.out.print(" ->");
              }
            } else {
              if ((ind_confidence / tot_confidence) >= confidence) {
                System.out.print(" " + items.get(Integer.parseInt(input_split_data[j]) - 1));
              }
              if (j == input_split_data.length - 1)
                System.out.print(" - confidence: " + (double) ind_confidence / 4);
            }
          }
          System.out.println();
        }
        System.out.println();
      }
    } while (totalItems.size() > 1);
  }

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    AprioriAlgorithm algorithm = new AprioriAlgorithm();
    Scanner scn = new Scanner(System.in);
    try {
      System.out.print("Please provide file name: ");
      algorithm.fileName = scn.next();
      System.out.print("provide support value: ");
      algorithm.support = scn.nextDouble();
      if (algorithm.support <= 0.0 || algorithm.support >= 1.0) {
        System.out.println("support value should lie between 0 and 1: " + algorithm.support);
        System.exit(0);
      }
      System.out.print("provide confidence value: ");
      algorithm.confidence = scn.nextDouble();
      if (algorithm.confidence <= 0.0 || algorithm.confidence >= 1.0) {
        System.out.println("confidence value should lie between 0 and 1: " + algorithm.confidence);
        System.exit(0);
      }
      System.out.println();
      algorithm.initialize();
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println("Total time taken in seconds: " + (totalTime / 1000));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Please provide valid input.");
    }
  }
}

