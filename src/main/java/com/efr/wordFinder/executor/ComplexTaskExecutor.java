package com.efr.wordFinder.executor;

import com.efr.wordFinder.service.ComplexTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Класс для выполнения задач с подсчетом символов
public class ComplexTaskExecutor {
    private final char letter; // Буква для поиска
    private final ConcurrentLinkedQueue<String> queue; // Очередь задач

    private final CyclicBarrier barrier; // Барьер для синхронизации
    private final ExecutorService executorService; // Пул потоков

    public ComplexTaskExecutor(int numberOfThreads, char letter, ConcurrentLinkedQueue<String> queue) {
        this.letter = letter;
        this.queue = queue;
        this.barrier = new CyclicBarrier(numberOfThreads, () -> {
            System.out.println("Все потоки достигли барьера, продолжаем...");
        });
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    // Метод для выполнения задач
    public void executeTasks(int numberOfTasks) throws InterruptedException, BrokenBarrierException {
        List<Future<Integer>> futures = new ArrayList<>();

        while (!queue.isEmpty()) {
            for (int i = 0; i < numberOfTasks; i++) {
                String taskString = queue.poll();
                if (taskString != null) {
                    Callable<Integer> task = () -> {
                        int result = new ComplexTask(letter, taskString).execute();
                        try {
                            barrier.await(10, TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            System.err.println("Тайм-аут ожидания на барьере.");
                        } catch (BrokenBarrierException | InterruptedException e) {
                            System.err.println("Барьер был прерван или поток был прерван.");
                        }
                        return result;
                    };
                    futures.add(executorService.submit(task));
                }
            }
        }

        // Суммируем результаты после завершения всех задач
        int totalLetters = 0;
        for (Future<Integer> future : futures) {
            try {
                totalLetters += future.get();
            } catch (ExecutionException e) {
                System.err.println("Ошибка при выполнении задачи: " + e.getMessage());
            }
        }

        // Останавливаем пул потоков
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS); // Убедиться, что все потоки завершились
        System.out.println("Общее количество символов: " + totalLetters);
    }
}
