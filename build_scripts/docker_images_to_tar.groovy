import java.nio.file.Files
import java.nio.file.Paths

class CommandExecution {
    static def execute(String command, String workingDirectory) {
        Files.createDirectories(Paths.get(workingDirectory));

        println String.format("Executing command: %s; in %s", command, workingDirectory)

        def commandStart = ""
        if (System.properties['os.name'].toLowerCase().contains('windows')) {
            commandStart = "cmd /c "
        } else {
            commandStart = ""
        }

        def proc = String.format("%s%s", commandStart, command).execute(null, new File(workingDirectory))
        proc.waitForProcessOutput(System.out, System.err)

        println "Exit code: " + proc.exitValue()
        if(proc.exitValue() != 0) {
            throw new RuntimeException('Call failed!')
        }
    }
}



def images = session.getProjectDependencyGraph()
        .getUpstreamProjects(project, false)
        .findAll { Files.exists(Paths.get(it.basedir.toString() + "/src/main/build_docker_image")) }
        .collect { "$it.groupId/$it.artifactId:$it.version" }
        .join(" ")

/*def excludeAdditionalImages = session.getSystemProperties().get("exclude-additional-images")
println "excludeAdditionalImages: " + excludeAdditionalImages
if(excludeAdditionalImages == null || excludeAdditionalImages.equals("false")) {
    def additionalImages = [
            "docker.io/calico/cni:v3.23.3",
            "docker.io/calico/kube-controllers:v3.23.3",
            "docker.io/calico/node:v3.23.3",
            "docker.io/coredns/coredns:1.9.3",
            "k8s.gcr.io/ingress-nginx/controller:v1.2.0",
            "k8s.gcr.io/pause:3.7",
            "cdkbot/hostpath-provisioner:1.4.0",
            "busybox:1.28.4",
            "redis:6.2.1-alpine3.13",
            "docker.io/grafana/grafana:9.1.7",
            "docker.io/grafana/promtail:2.6.1",
            "quay.io/kiwigrid/k8s-sidecar:1.19.2",
            "quay.io/prometheus-operator/prometheus-config-reloader:v0.60.0",
            "quay.io/prometheus-operator/prometheus-operator:v0.60.0",
            "quay.io/prometheus/alertmanager:v0.24.0",
            "quay.io/prometheus/blackbox-exporter:v0.22.0",
            "quay.io/prometheus/node-exporter:v1.4.0",
            "quay.io/prometheus/prometheus:v2.39.1",
            "quay.io/brancz/kube-rbac-proxy:v0.13.1",
            "k8s.gcr.io/kube-state-metrics/kube-state-metrics:v2.6.0",
            "jimmidyson/configmap-reload:v0.5.0",
            "registry.k8s.io/prometheus-adapter/prometheus-adapter:v0.10.0"
    ]

    for(additionalImage in additionalImages){
        CommandExecution.execute(
                String.format("docker pull %s", additionalImage),
                project.basedir.toString()
        )
    }

    images = images + " " + additionalImages.join(" ")
}*/

return CommandExecution.execute(
        String.format("docker save -o docker_images.tar %s",
                images),
        project.basedir.toString() + "/roles/microk8s/files/"
)
