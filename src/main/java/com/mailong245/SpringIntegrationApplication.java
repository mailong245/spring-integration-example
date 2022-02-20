package com.mailong245;

import com.mailong245.config.BasicIntegrationConfig;
import com.mailong245.config.IntegrationFlowConfig;
import com.mailong245.config.JdbcIntegrationFlowConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpringIntegrationApplication {
    private static final List<Class<?>> classes = new ArrayList<>();

    private static final int CHOICE = 2;

    static {
        classes.add(0, BasicIntegrationConfig.class);
        classes.add(1, IntegrationFlowConfig.class);
        classes.add(2, JdbcIntegrationFlowConfig.class);
    }

    public static void main(String... args) {

        Class<?> clazz = classes.get(CHOICE);

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(clazz);
        context.registerShutdownHook();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter q and press <enter> to exit the program: ");

        while (true) {
            String input = scanner.nextLine();
            if ("q".equals(input.trim())) {
                break;
            }
        }
        System.exit(0);
    }

}
