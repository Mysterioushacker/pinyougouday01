package cn.itcast.springboot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  如果是spring boot工程必须要有引导类；该引导类上面需要添加@SpringBootApplication
 *  该注解是一个组合注解；组合了组件扫描，可以扫描到当前包及其子包中的注解
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //SpringApplication.run(Application.class,args);
        SpringApplication application = new SpringApplication(Application.class);

        //禁用启动时候的banner
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
