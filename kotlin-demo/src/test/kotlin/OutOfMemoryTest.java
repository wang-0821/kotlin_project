import java.util.ArrayList;
import java.util.List;

/**
 * @author lix wang
 */
public class OutOfMemoryTest {
    public static void main(String[] args) throws InterruptedException {
        List<List<Integer>> lists = new ArrayList<>();
        try {
            while (true) {
                lists.add(new ArrayList<>(1024));
            }
        } catch (Error error) {
            while (true) {
                lists.size();
                Thread.sleep(300);
            }
        }
    }
}
