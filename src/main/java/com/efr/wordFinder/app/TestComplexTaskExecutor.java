package com.efr.wordFinder.app;

import com.efr.wordFinder.executor.ComplexTaskExecutor;
import com.efr.wordFinder.service.TextFileSplitter;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestComplexTaskExecutor {

    public static void main(String[] args) throws InterruptedException {
        final int LINES_PER_TASK = 100;
        final int NUMBER_OF_THREAD = 5;
        final  int NUMBER_OF_TASKS = 5;

        ConcurrentLinkedQueue<String> queue =
                new TextFileSplitter("src/main/resources/text/pg2600.txt", LINES_PER_TASK).splitFileToQueue();

        System.out.println("Количество строк в очереди: " + queue.size());

        ComplexTaskExecutor taskExecutor = new ComplexTaskExecutor(NUMBER_OF_THREAD, 'a', queue); // Количество потоков

        Runnable testRunnable = () -> {
            System.out.println(Thread.currentThread().getName() + " начал выполнение.");

            try {
                taskExecutor.executeTasks(NUMBER_OF_TASKS);
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException("Ошибка выполнения: " + e.getMessage(), e);
            }

            System.out.println(Thread.currentThread().getName() + " завершил выполнение.");
        };

        // Запускаем тестовые потоки
        Thread thread1 = new Thread(testRunnable, "TestThread-1");
        Thread thread2 = new Thread(testRunnable, "TestThread-2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
