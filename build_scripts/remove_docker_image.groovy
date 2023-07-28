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

String workingDirectory = String.format("%s/target/build_docker_image",
        project.basedir.toString())

if(new File(workingDirectory).exists()) {
    CommandExecution.execute(
            String.format("docker rmi -f %s/%s:%s",
                    project.groupId,
                    project.artifactId,
                    project.version),
            workingDirectory
    )
}