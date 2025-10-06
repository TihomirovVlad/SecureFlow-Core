import config.DatabaseConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
        JdbcTemplate template = context.getBean(JdbcTemplate.class);
        Integer count = template.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        System.out.println("Users count: " + count);
    }
}
