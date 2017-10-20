package br.com.pedront.bitsotrading;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import br.com.pedront.bitsotrading.core.CoreConfig;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@Import({
        CoreConfig.class
})
@EnableAutoConfiguration
@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launchApp(Main.class, DashboardView.class, args);
    }

    /*
     * 
     * @Autowired private BitsoApiIntegration bitsoApiIntegration;
     * 
     * @Override public void start(Stage primaryStage) throws Exception { Parent root =
     * FXMLLoader.load(getClass().getResource("sample.fxml")); primaryStage.setTitle("Hello World");
     * primaryStage.setScene(new Scene(root, 300, 275)); primaryStage.show(); }
     * 
     * public static void main(String[] args) { SpringApplication.run(Main.class, args); }
     * 
     * @Override public void run(final String... args) throws Exception {
     * System.out.println(bitsoApiIntegration.getAvailableBooks());
     * 
     * launch(args); }
     */
}
