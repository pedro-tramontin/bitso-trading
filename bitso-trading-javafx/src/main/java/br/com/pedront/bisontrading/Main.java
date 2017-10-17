package br.com.pedront.bisontrading;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import br.com.pedront.bitsotrading.core.CoreConfig;
import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Import({
        CoreConfig.class
})
@EnableAutoConfiguration
@SpringBootApplication
public class Main extends Application implements CommandLineRunner {

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        launch(args);
    }
}
