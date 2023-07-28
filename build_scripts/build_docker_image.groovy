import java.nio.file.Files
import java.nio.file.Paths

class CommandExecution {
    static def execute(String command, String workingDirectory, boolean returnOutput) {
        Files.createDirectories(Paths.get(workingDirectory));

        println String.format("Executing command: %s; in %s", command, workingDirectory)

        def commandStart = ""
        if (System.properties['os.name'].toLowerCase().contains('windows')) {
            commandStart = "cmd /c "
        } else {
            commandStart = ""
        }

        def proc = String.format("%s%s", commandStart, command).execute(null, new File(workingDirectory))
        if(!returnOutput) {
            proc.waitForProcessOutput(System.out, System.err)
        } else {
            proc.waitForOrKill(10000)
        }

        println "Exit code: " + proc.exitValue()
        if(proc.exitValue() != 0) {
            throw new RuntimeException('Call failed!')
        }
        if(returnOutput) {
            return proc.getText()
        }
    }
}

CommandExecution.execute(
        String.format("docker build -t %s/%s:%s .",
                project.groupId,
                project.artifactId,
                project.version),
        String.format("%s/target/build_docker_image",
                project.basedir.toString()),
        false
)

def imageDigest = CommandExecution.execute(
        String.format("docker inspect %s/%s:%s --format='{{.Id}}'",
                project.groupId,
                project.artifactId,
                project.version),
        String.format("%s/target/build_docker_image",
                project.basedir.toString()),
        true
)
project.properties.setProperty(String.format(
            "%s_image_digest",
            project.artifactId
        ),
        imageDigest
)
