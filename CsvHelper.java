package com.paydex.book_tracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvHelper {


    public static List<Book> loadBooks(String csvPath){
        List<Book> books = new ArrayList<>();

        System.out.println("Reading CSV: " + csvPath);

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip empty lines

                System.out.println("Line: " + line); // debug

                if (isHeader) {
                    isHeader = false;
                    continue; // skip header
                }

                // split by comma or semicolon
                String[] data = line.split("[,;]");

                if (data.length >= 4) {
                    String title = data[0].replace("\"", "").trim();
                    String author = data[1].replace("\"", "").trim();

                    // FIX: remove quotes, spaces and handle numbers safely
                    String totalPagesStr = data[2].replace("\"", "").trim();
                    String pagesReadStr = data[3].replace("\"", "").trim();

                    int totalPages = totalPagesStr.isEmpty() ? 0 : Integer.parseInt(totalPagesStr);
                    int pagesRead = pagesReadStr.isEmpty() ? 0 : Integer.parseInt(pagesReadStr);

                    books.add(new Book(title, author, totalPages, pagesRead));
                    System.out.println("Parsed: " + title + " - " + totalPages + " pages");
                } else {
                    System.out.println("Invalid column count: " + Arrays.toString(data));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public static void saveBooks(String csvPath, List<Book> books){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))){
            bw.write("Title,Author,TotalPages,ReadsPage");
            bw.newLine();

            for(Book book : books){
                bw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getTotalPage() + "," +
                        book.getPagesRead());
                bw.newLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
