package ru.edu.pr02;

public class TimesheetAnalyzer {

    private static final int DAYS_EXPECTED = 5;
    private static final int MAX_HOURS = 16;
    private static final int DAILY_NORMAL_HOURS = 10;
    private static final int WEEKLY_LIMIT_HOURS = 40;
    private static final int HEAVY_DAYS_HOURS = 12;
    public static int[] parseHours(String line) {
        if(line==null){
            throw new IllegalArgumentException ("На вход подан null");
        }
        String[] days = line.split(";", -1);
        if(days.length!=DAYS_EXPECTED){
            throw new IllegalArgumentException("Ошибка: структура – ожидалось " + DAYS_EXPECTED + " значений, получено " + days.length);
        }

        int[] resultList = new int[DAYS_EXPECTED];

        for(int i = 0; i<DAYS_EXPECTED; i++){
            boolean match = days[i].matches("0|[1-9][0-9]*");
            if(!match){
                throw new IllegalArgumentException ("Ошибка: позиция " + (i+1) + " – значение \"" + days[i] + "\" не является целым числом");
            }

            int hoursPerDay = Integer.parseInt(days[i]);
            if(hoursPerDay>MAX_HOURS){
                throw new IllegalArgumentException ("Ошибка: позиция " + (i+1) + " – значение " + days[i] +" вне диапазона [0, " + MAX_HOURS + "]");
            }
            resultList[i]=hoursPerDay;
        }
        
        return resultList;
    }

    //для проверок
    private static void checkHours(int[] hours){
        if (hours == null){
            throw new IllegalArgumentException("hours is null");
        }
        if (hours.length != DAYS_EXPECTED){
            throw new IllegalArgumentException("hours length must be " + DAYS_EXPECTED + ", got " + hours.length);
        }
    }

    public static int total(int[] hours) {
        checkHours(hours);
        int totalVal = 0;
        for(int h : hours){
            totalVal+=h;
        }
        return totalVal;
    }
    public static double average(int[] hours) {
        checkHours(hours);
        int totalHours = total(hours);
        double doubleTotalHours = (double)totalHours;

        double resultAvg = (double)Math.round(doubleTotalHours / DAYS_EXPECTED * 100) / 100;
        return resultAvg;
    }
    public static int overtimeDays(int[] hours) {
        checkHours(hours);
        int overtimeDays = 0;
        for(int h : hours){
            if(h>DAILY_NORMAL_HOURS){
                overtimeDays++;
            }
        }
        return overtimeDays;
    }
    public static int weeklyOvertime(int[] hours) {
        checkHours(hours);
        int totalHours = total(hours);
        int overtime = Math.max(0, totalHours - WEEKLY_LIMIT_HOURS);
        return overtime;
    }

    public static int addHeavyDaysCount(int[] hours){
        checkHours(hours);
        int heavyDays = 0;
        for(int h : hours){
            if(h>HEAVY_DAYS_HOURS){
                heavyDays++;
            }
        }
        return heavyDays;
    }

}
