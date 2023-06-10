package configserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @Autowired
    ConfigurableEnvironment environment;

    @GetMapping("/test")
    public String test(){
        Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
        String keyPath = (String) systemEnvironment.get("GIT_PRIVATE_KEY_PATH");

        File privateKeyFile = new File(keyPath);
        Assert.isTrue(!privateKeyFile.isFile(), "Private key file does not exist");
        try (BufferedReader br = new BufferedReader(new FileReader(privateKeyFile))) {
            StringBuilder privateKey = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                privateKey.append(line).append("\n");
            }
            MutablePropertySources propertySources = environment.getPropertySources();
            HashMap<String, Object> map = new HashMap<>();
            System.out.println("Private key: \n" + privateKey.toString());
        } catch (Exception e) {

        }

        return "test";
    }
}
