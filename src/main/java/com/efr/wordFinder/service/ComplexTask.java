package com.efr.wordFinder.service;

// Служебный класс для подсчета букв в строке
public class ComplexTask {
    private final char letter; // Буква для поиска
    private final String searchedString; // Строка для поиска

    public ComplexTask(char letter, String searchedString) {
        this.letter = letter;
        this.searchedString = searchedString;
    }

    public int execute() {
        int count = 0;
        for (char c : searchedString.toCharArray()) {
            if (c == letter) count++;
        }
        return count;
    }
}