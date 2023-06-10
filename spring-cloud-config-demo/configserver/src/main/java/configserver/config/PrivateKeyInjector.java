package configserver.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PrivateKeyInjector implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
        String keyPath = (String) systemEnvironment.get("GIT_PRIVATE_KEY_PATH");
        if (keyPath == null){
            return;
        }
        File privateKeyFile = new File(keyPath);
        Assert.isTrue(privateKeyFile.isFile(), "Private key file does not exist");
        try (BufferedReader br = new BufferedReader(new FileReader(privateKeyFile))){
            StringBuilder privateKey = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                privateKey.append(line).append("\n");
            }
            MutablePropertySources propertySources = environment.getPropertySources();
            HashMap<String, Object> map = new HashMap<>();
            System.out.println("Private key: \n" + privateKey.toString());
            map.put("spring.cloud.config.server.git.private-key", privateKey.toString());
            map.put("spring.cloud.config.server.git.ignore-local-ssh-settings", true);
            propertySources.addFirst(new MapPropertySource("systemEnvironment", map));
        } catch (Exception e) {

        }

    }
}
