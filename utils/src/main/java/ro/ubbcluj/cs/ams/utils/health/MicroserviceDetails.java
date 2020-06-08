package ro.ubbcluj.cs.ams.utils.health;

import lombok.Data;
import lombok.SneakyThrows;
import ro.ubbcluj.cs.ams.utils.config.TargetJarsProperties;

import java.lang.reflect.Field;

@Data
public class MicroserviceDetails {


    TargetJarsProperties jarsProps;

    private Class targ = TargetJarsProperties.class;
    private String name;
    private String healthPath;
    private String markPresencePath;
    private String jarPath;
    private int timesNoRun;
    private boolean calledMe;

    public MicroserviceDetails(String name, String thisAppName, TargetJarsProperties jarsProps) {

        this.name = name;
        this.jarsProps = jarsProps;
        this.healthPath = "http://" + name + "/" + name.split("[-]")[0] + "/health?service-name=" + thisAppName;
        this.markPresencePath = healthPath.replace("health", "present");
        this.jarPath = buildJarPath();
        this.calledMe=true;
    }

    @SneakyThrows
    private String buildJarPath() {

        Field field = targ.getField(name.replace('-', '_'));
        return (String) field.get(jarsProps);
    }
}
