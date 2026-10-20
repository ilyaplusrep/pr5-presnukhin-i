package ru.edu.pr02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SelfCheck {

    private static final double EPS = 0.0001;

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        runUnitChecks();
        System.out.println("Unit: " + passed + "/" + (passed + failed) + " passed");

        int passedBefore = passed;
        int failedBefore = failed;
        runDataChecks();
        int dataPassed = passed - passedBefore;
        int dataTotal = dataPassed + failed - failedBefore;
        System.out.println("Data-driven: " + dataPassed + "/" + dataTotal + " passed");

        // интерактив только по флагу -i, иначе run_selfcheck.bat повиснет в ожидании ввода
        if (args.length > 0 && args[0].equals("-i")) {
            runInteractive();
        }
    }

    private static void runUnitChecks() {
        // parseHours: корректная строка и все виды ошибок
        checkArray("parseHours: корректная строка", "8;9;10;8;8", new int[]{8, 9, 10, 8, 8});
        checkError("parseHours: пустая строка", "", "структура");
        checkError("parseHours: значение 17", "8;8;17;8;8", "позиция 3");
        checkError("parseHours: не-число", "8;abc;8;8;8", "позиция 2");
        checkError("parseHours: 4 значения", "8;8;8;8", "структура");
        checkError("parseHours: trailing ;", "8;8;8;8;8;", "структура");
        checkError("parseHours: ведущий ноль", "08;8;8;8;8", "позиция 1");

        // total: типовое, нижняя и верхняя граница
        checkInt("total: типовое", TimesheetAnalyzer.total(new int[]{8, 9, 10, 8, 8}), 43);
        checkInt("total: нижняя граница", TimesheetAnalyzer.total(new int[]{0, 0, 0, 0, 0}), 0);
        checkInt("total: верхняя граница", TimesheetAnalyzer.total(new int[]{16, 16, 16, 16, 16}), 80);

        // average: типовое, целое, дробное
        checkDouble("average: типовое", TimesheetAnalyzer.average(new int[]{8, 9, 10, 8, 8}), 8.6);
        checkDouble("average: целое", TimesheetAnalyzer.average(new int[]{8, 8, 8, 8, 8}), 8.0);
        checkDouble("average: дробное", TimesheetAnalyzer.average(new int[]{7, 8, 9, 8, 7}), 7.8);

        // overtimeDays (вариант 16: переработка = v > 10, поэтому 9 и 10 уже не считаются)
        checkInt("overtimeDays: нет переработки", TimesheetAnalyzer.overtimeDays(new int[]{8, 8, 8, 8, 8}), 0);
        checkInt("overtimeDays: 9 и 10 не переработка", TimesheetAnalyzer.overtimeDays(new int[]{9, 8, 7, 10, 8}), 0);
        checkInt("overtimeDays: 9 не переработка", TimesheetAnalyzer.overtimeDays(new int[]{8, 9, 8, 8, 8}), 0);

        // weeklyOvertime: сумма 40, 50, меньше 40
        checkInt("weeklyOvertime: сумма 40", TimesheetAnalyzer.weeklyOvertime(new int[]{8, 8, 8, 8, 8}), 0);
        checkInt("weeklyOvertime: сумма 50", TimesheetAnalyzer.weeklyOvertime(new int[]{10, 10, 10, 10, 10}), 10);
        checkInt("weeklyOvertime: сумма < 40", TimesheetAnalyzer.weeklyOvertime(new int[]{6, 6, 6, 6, 6}), 0);

        // вариант 16, часть А: overtimeDays считает v > 10
        checkInt("вариант А: граница 10 не считается", TimesheetAnalyzer.overtimeDays(new int[]{10, 10, 10, 10, 10}), 0);
        checkInt("вариант А: 11 считается", TimesheetAnalyzer.overtimeDays(new int[]{11, 8, 8, 8, 8}), 1);
        checkInt("вариант А: три дня > 10", TimesheetAnalyzer.overtimeDays(new int[]{11, 12, 13, 8, 8}), 3);

        // вариант 16, часть Б: addHeavyDaysCount считает дни > 12
        checkInt("вариант Б: два тяжёлых дня", TimesheetAnalyzer.addHeavyDaysCount(new int[]{13, 8, 8, 16, 8}), 2);
        checkInt("вариант Б: граница 12 не считается", TimesheetAnalyzer.addHeavyDaysCount(new int[]{12, 12, 12, 12, 12}), 0);
        checkInt("вариант Б: нет тяжёлых", TimesheetAnalyzer.addHeavyDaysCount(new int[]{8, 8, 8, 8, 8}), 0);
    }

    private static void checkInt(String name, int actual, int expected) {
        report(name, actual == expected, "ожидалось " + expected + ", получено " + actual);
    }

    private static void checkDouble(String name, double actual, double expected) {
        report(name, Math.abs(actual - expected) < EPS, "ожидалось " + expected + ", получено " + actual);
    }

    private static void checkArray(String name, String line, int[] expected) {
        try {
            int[] actual = TimesheetAnalyzer.parseHours(line);
            report(name, Arrays.equals(actual, expected),
                    "ожидалось " + Arrays.toString(expected) + ", получено " + Arrays.toString(actual));
        } catch (Exception e) {
            report(name, false, "неожиданное исключение " + e.getMessage());
        }
    }

    // ожидается IllegalArgumentException, в тексте которой есть expectedPart
    private static void checkError(String name, String line, String expectedPart) {
        try {
            TimesheetAnalyzer.parseHours(line);
            report(name, false, "ожидалась ошибка, но её не было");
        } catch (IllegalArgumentException e) {
            report(name, e.getMessage().contains(expectedPart),
                    "в сообщении нет \"" + expectedPart + "\": " + e.getMessage());
        } catch (Exception e) {
            report(name, false, "не тот тип исключения: " + e);
        }
    }

    private static void report(String name, boolean ok, String reason) {
        if (ok) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name + ": " + reason);
        }
    }

    //----------------------------------------
    private static void runDataChecks(){
        List<String> lines;
        try {
        lines = Files.readAllLines(Path.of("data/sample.csv"));
        } catch (IOException e){
            System.out.println("FAIL data: не удалось прочитать файл " + e.getMessage());
            return;
        }

        int n = 0;
        for (String row : lines) {
            if(row.isEmpty() || row.startsWith("#")){
                continue;
            }
            n++;
            // строка файла: вход|ожидание|комментарий
            String[] parts = row.split("\\|");
            String input = parts[0];
            if (input.equals("EMPTY")) {
                input = "";
            }
            String expected = parts[1];
            String name = "data/" + n + " (" + parts[2] + ")";

            if (expected.startsWith("ERROR:")) {
                checkError(name, input, expected.substring("ERROR:".length()));
            } else {
                checkOk(name, input, expected.substring("OK:".length()));
            }
        }
    }

    // ожидается корректная строка: считаем 4 метрики и собираем их в текст
    // того же вида, что в файле (sum=40,avg=8.00,od=0,wo=0), потом сравниваем тексты
    private static void checkOk(String name, String input, String expected) {
        try {
            int[] hours = TimesheetAnalyzer.parseHours(input);
            int sum = TimesheetAnalyzer.total(hours);
            String avg = formatAverage(TimesheetAnalyzer.average(hours));
            int od = TimesheetAnalyzer.overtimeDays(hours);
            int wo = TimesheetAnalyzer.weeklyOvertime(hours);

            String actual = "sum=" + sum + ",avg=" + avg + ",od=" + od + ",wo=" + wo;
            report(name, actual.equals(expected), "ожидалось " + expected + ", получено " + actual);
        } catch (Exception e) {
            report(name, false, "неожиданное исключение " + e.getMessage());
        }
    }

    // 8.4 -> "8.40": всегда 2 знака после точки (Locale.ROOT, чтобы была точка, а не запятая)
    private static String formatAverage(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    //----------------------------------------
    // ручной ввод 
    private static void runInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите строку табеля (exit или пустая строка - выход):");
        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) {
                break;
            }
            String line = sc.nextLine();
            if (line.isEmpty() || line.equals("exit")) {
                break;
            }
            try {
                int[] hours = TimesheetAnalyzer.parseHours(line);
                System.out.println("Сумма: " + TimesheetAnalyzer.total(hours));
                System.out.println("Среднее: " + formatAverage(TimesheetAnalyzer.average(hours)));
                System.out.println("Дней с переработкой: " + TimesheetAnalyzer.overtimeDays(hours));
                System.out.println("Недельная переработка: " + TimesheetAnalyzer.weeklyOvertime(hours));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
