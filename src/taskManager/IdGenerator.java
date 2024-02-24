package taskManager;
import java.util.Random;

public class IdGenerator {
    private static final Random RANDOM = new Random();

    public static int generateId() {
        return RANDOM.nextInt(Integer.MAX_VALUE);
    }
}