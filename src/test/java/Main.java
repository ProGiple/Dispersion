import com.satellite.progiple.databases.Table;
import com.satellite.progiple.registration.DiRegister;
import com.satellite.progiple.test.databases.DataBaseTest;

public class Main {
    public static final String PATH = "C:/Users/VyachePC/IdeaProjects/Dispersion/";

    public static void main(String[] args) {
        DiRegister.initialize(PATH + "target/test-classes/");
    }
}
