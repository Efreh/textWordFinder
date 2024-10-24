package com.efr.wordFinder.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

// Класс для разбиения файла на строки с указанным количеством строк
public class TextFileSplitter {
    private final String filePath; // Путь к файлу
    private final int linesPerTask; // Количество строк для каждой задачи

    public TextFileSplitter(String filePath, int linesPerTask) {
        this.filePath = filePath;
        this.linesPerTask = linesPerTask;
    }

    // Разбиваем файл на блоки строк и сохраняем их в очередь
    public ConcurrentLinkedQueue<String> splitFileToQueue() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                count++;
                if (count == linesPerTask) {
                    queue.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    count = 0;
                }
            }
            if (!stringBuilder.isEmpty()) {
                queue.add(stringBuilder.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading the file: " + e.getMessage(), e);
        }

        return queue;
    }
}