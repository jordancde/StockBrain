package org.neuroph.contrib.stockBrain;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


/**
 *
 * @author Jordan Dearsley
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        Scanner in = new Scanner(System.in);

        int stockCount = 30;
        double error = 7;
        System.out.print("Stock Name: ");
        String stockName = in.nextLine();
        Stock stock = new Stock(stockName,error);
        //ArrayList<Stock> stocks = pickStocks(stockCount,error);

    }
    
    
    public static ArrayList<Stock> pickStocks(int amount,double error) throws IOException, InterruptedException{
        
        //String[] stockNames = {"GOOG","AAPL","AXP","BA","CAT","CSCO","CVX","DD","DIS","GE","GS","HD","IBM","INTC","JNJ","JPM","KO","MCD","MMM","MRK","MSFT","NKE","PFE","PG","TRV","UNH","UTX","V","VZ","WMT","XOM"};
        List<String> stockNames500  = Files.readAllLines(Paths.get("Stocks.txt"));
        String[] stockNames = new String[stockNames500.size()];
        int p = 0;
        for(String stockName: stockNames500){
            stockNames[p] = stockName;
            p++;
        }
        double percentage;
        double [] percentages = new double[stockNames.length];
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        int i = 0;
        for(String name: stockNames){
            Stock stock = new Stock(name,error);
            stocks.add(stock);
            i++;
        }
        stocks = bubble_srt(stocks);
        for(int j = 0;j<stocks.size();j++){
            System.out.println((j+1)+". "+stocks.get(j).getName()+": "+round(stocks.get(j).getPercent())+"%        "+round(stocks.get(j).getRangeBottom())+" - "+round(stocks.get(j).getRangeTop()));
        }
        ArrayList<Stock> stockChoices = new ArrayList<Stock>();
        for(int j = 0;j<amount;j++){
            stockChoices.add(stocks.get(j));
        }
        return stockChoices;
    }
    
    public static ArrayList<Stock> bubble_srt(ArrayList<Stock> array) {   
        int n = array.size();
        int k;
        for (int m = n; m >= 0; m--) {
            for (int i = 0; i < n - 1; i++) {
                k = i + 1;
                if (array.get(i).getPercent() < array.get(k).getPercent()) {
                    Collections.swap(array, i, k);
                }
            }   
        }
        return array;
    }
  public static double round(double input){
    return (double) Math.round(input * 10000) / 10000;
  }

}
