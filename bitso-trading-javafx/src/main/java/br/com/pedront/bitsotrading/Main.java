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

}
