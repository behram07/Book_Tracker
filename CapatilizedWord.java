package com.paydex.book_tracker;


public class CapatilizedWord {
    public static String capitalizeWords(String text) {
        if(text == null || text.isEmpty()) return text;

        String[] words = text.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < words.length; i++){
            String word = words[i];
            if(word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
            if(i < words.length - 1){
                result.append(" ");
            }
        }
        return result.toString().trim();
    }
}
