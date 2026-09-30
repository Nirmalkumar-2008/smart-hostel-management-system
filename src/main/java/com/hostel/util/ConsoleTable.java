package com.hostel.util;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTable {

    private final List<String> headers = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public ConsoleTable(String... headers) {
        for (String h : headers) {
            this.headers.add(h);
        }
    }

    public void addRow(String... cells) {
        List<String> row = new ArrayList<>();
        for (String c : cells) {
            row.add(c == null ? "" : c);
        }
        rows.add(row);
    }

    public void print() {
        if (headers.isEmpty()) return;

        int numCols = headers.size();
        int[] colWidths = new int[numCols];

        for (int i = 0; i < numCols; i++) {
            colWidths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < numCols && i < row.size(); i++) {
                colWidths[i] = Math.max(colWidths[i], row.get(i).length());
            }
        }

        StringBuilder lineBuilder = new StringBuilder("+");
        for (int w : colWidths) {
            lineBuilder.append("-".repeat(w + 2)).append("+");
        }
        String line = lineBuilder.toString();

        System.out.println(line);

        // Print header
        System.out.print("|");
        for (int i = 0; i < numCols; i++) {
            System.out.printf(" %-" + colWidths[i] + "s |", headers.get(i));
        }
        System.out.println();
        System.out.println(line);

        // Print rows
        if (rows.isEmpty()) {
            System.out.print("|");
            int totalWidth = line.length() - 2;
            String noData = "No records found.";
            System.out.printf(" %-" + (totalWidth - 1) + "s|%n", noData);
        } else {
            for (List<String> row : rows) {
                System.out.print("|");
                for (int i = 0; i < numCols; i++) {
                    String val = i < row.size() ? row.get(i) : "";
                    System.out.printf(" %-" + colWidths[i] + "s |", val);
                }
                System.out.println();
            }
        }
        System.out.println(line);
    }
}
