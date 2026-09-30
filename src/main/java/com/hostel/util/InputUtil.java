package com.hostel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt + " [Press Enter for '" + defaultValue + "']: ");
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer number.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.printf("Please enter a number between %d and %d.%n", min, max);
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            double val = readDouble(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.printf("Please enter a value between %.2f and %.2f.%n", min, max);
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format (e.g. 2026-08-15).");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.equals("Y") || line.equals("YES")) {
                return true;
            } else if (line.equals("N") || line.equals("NO")) {
                return false;
            }
            System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
        }
    }

    public static void pauseForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
