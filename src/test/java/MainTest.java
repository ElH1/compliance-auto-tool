import compliance.framework.core.Main;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class MainTest {
    Main main = new Main();

    @Before
    public void init() {
        main = new Main();
    }

    @Test(expected = IOException.class)
    public void MainTest1() throws IOException {
        String[] args = new String[2];
        args[0] = "pathToInstanceFile";
        args[1] = "pathToRule";
        main.main(args);
    }

    @Test(expected = NullPointerException.class)
    public void applicableCompliant() throws IOException {
        String[] args = new String[2];
        main.main(args);
    }

}
