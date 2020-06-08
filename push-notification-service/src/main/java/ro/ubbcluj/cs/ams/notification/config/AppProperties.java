package ro.ubbcluj.cs.ams.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app")
@EnableConfigurationProperties
@Component
public class AppProperties {

    private String serverPublicKeyPath;

    private String serverPrivateKeyPath;

    public String getServerPublicKeyPath() {
        return this.serverPublicKeyPath;
    }

    public void setServerPublicKeyPath(String serverPublicKeyPath) {
        this.serverPublicKeyPath = serverPublicKeyPath;
    }

    public String getServerPrivateKeyPath() {
        return this.serverPrivateKeyPath;
    }

    public void setServerPrivateKeyPath(String serverPrivateKeyPath) {
        this.serverPrivateKeyPath = serverPrivateKeyPath;
    }
}
