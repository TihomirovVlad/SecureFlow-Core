import config.DatabaseConfig;
import console.OperationsConsoleListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);

        OperationsConsoleListener consoleListener = context.getBean(OperationsConsoleListener.class);
        consoleListener.start();

        context.close();
    }
}
