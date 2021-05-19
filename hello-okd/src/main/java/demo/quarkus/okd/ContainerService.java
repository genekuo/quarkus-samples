package demo.quarkus.okd;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContainerService {

    public String getContainerId() {
        return System.getenv().getOrDefault("HOSTNAME", "unknown");
    }
}
