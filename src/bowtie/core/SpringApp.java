package bowtie.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import bowtie.dbl.VoterUpdater;

/**
 * @author &#8904
 *
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = VoterUpdater.class)
public class SpringApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(SpringApp.class, args);
        new Main();
    }
}
