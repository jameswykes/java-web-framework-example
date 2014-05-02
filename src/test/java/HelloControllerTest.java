import junit.framework.TestCase;
import me.jw.mvc.test.TestClient;
import me.jw.mvc.test.TestClientResponse;
import me.jw.projects.example.HelloController;

public class HelloControllerTest extends TestCase {
    public void testHello() {
        try {
            TestClient client = new TestClient(HelloController.class);
            TestClientResponse response = client.get("/");
            assertEquals("test", response.getRaw());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
