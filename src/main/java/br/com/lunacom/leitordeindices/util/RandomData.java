package br.com.lunacom.leitordeindices.util;

public class RandomData {
    public static int getRandomIntegerBetweenRange(double min, double max){
        int x = (int) ((int)(Math.random()*((max-min)+1))+min);
        return x;
    }
}
