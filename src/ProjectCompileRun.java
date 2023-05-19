import java.io.BufferedReader;
        import java.io.File;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectCompileRun {

    public static void main(String[] args) throws IOException {
        // Set the path to the directory containing the projects to run
        String projectsDirectoryPath = "assets/submissionProjFolders";
        File projectsDirectory = new File(projectsDirectoryPath);

        // Get a list of all subdirectories (i.e., projects) in the directory
        File[] projectDirectories = projectsDirectory.listFiles(File::isDirectory);

        // Compile and run each project
        for (File projectDirectory : projectDirectories) {
            try{
                String projectName = projectDirectory.getName();
                System.out.println("\n ==============================================");
                System.out.println("Compiling and running project: " + projectName);

                // Compile the project
                List<String> compilationCommands = new ArrayList<>();
                compilationCommands.add("javac");
                compilationCommands.add("-d");
                compilationCommands.add(projectDirectory.getAbsolutePath() + "/bin");
                compilationCommands.addAll(getJavaFiles(projectDirectory));
                System.out.println("Compiling... \n");

                ProcessBuilder compilationProcessBuilder = new ProcessBuilder(compilationCommands);
                compilationProcessBuilder.redirectErrorStream(true);
                Process compilationProcess = compilationProcessBuilder.start();

                // Log any compilation errors
                BufferedReader compilationOutputReader = new BufferedReader(new InputStreamReader(compilationProcess.getInputStream()));
                String line;
                while ((line = compilationOutputReader.readLine()) != null) {
                    System.out.println(line);
                }

                // Run the project
                System.out.println("Running... \n");
                List<String> runCommands = new ArrayList<>();
                runCommands.add("java");
                runCommands.add("-cp");
                runCommands.add(projectDirectory.getAbsolutePath() + "/bin");
                runCommands.add(getMainClass(projectDirectory));
                ProcessBuilder runProcessBuilder = new ProcessBuilder(runCommands);
                runProcessBuilder.redirectErrorStream(true);
                Process runProcess = runProcessBuilder.start();

                // Log the output of the project
                BufferedReader runOutputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                while ((line = runOutputReader.readLine()) != null) {
//                    System.out.println(line);
                }
            }
            catch (Exception e){
//                System.out.println(e.toString());
            }

        }
    }

    private static List<String> getJavaFiles(File directory) {
        List<String> javaFiles = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                javaFiles.addAll(getJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file.getAbsolutePath());
            }
        }
//        System.out.println(javaFiles.toString());
        return javaFiles;
    }


    private static String getMainClass(File directory) throws IOException {
        String mainClassName = null;
        System.out.println(Arrays.toString(directory.listFiles()));

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".java")) {
                System.out.println(file.getName());

                BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("public static void main")) {
                        mainClassName = file.getName().replace(".java", "");
                        break;
                    }
                }
                reader.close();
            }
        }
        if (mainClassName == null) {
            throw new RuntimeException("Could not find main class in project: " + directory.getName());
        }
        return mainClassName;
    }

}
