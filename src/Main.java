import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        List<Thread> threads = new ArrayList<>();

        Thread textThread = new Thread(() -> {
            String[] texts = new String[10_000];
            for (int i = 0; i < texts.length; i++) {
                try {
                    Random random = new Random();
                    String text = generateText("abc", 100_000 + random.nextInt(3));
                    aQueue.put(text);
                    bQueue.put(text);
                    cQueue.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread analizerA = new Thread(() -> {
            calculate(aQueue, 'a');
        });

        Thread analizerB = new Thread(() -> {
            calculate(bQueue, 'b');
        });

        Thread analizerC = new Thread(() -> {
            calculate(cQueue, 'c');
        });

        threads.add(textThread);
        threads.add(analizerA);
        threads.add(analizerB);
        threads.add(analizerC);

        for (Thread thread : threads) {
            thread.start();
        }

        System.out.println("start");
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("finish");
    }

    // Подсчет максимальной длины одинаковых символов в строке
    public static void calculate(BlockingQueue<String> queue, char symbol) {
        int max = 0; // максимальное кол-во одинаковых символов в строке
        for (int i = 0; i < 10_000; i++) {
            try {
                String text = queue.take();
                int sum = 0; // количество одинаковых символов в строке
                for (int j = 0; j < text.length(); j++) {
                    if (text.charAt(j) == symbol) {
                        sum++;
                    }
                }
                if (max < sum) {
                    max = sum;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("max " + symbol + ": " + max);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}