import com.opencsv.CSVWriter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

public class CodeNormaliser extends ASTVisitor {

    public static void main(String[] args) throws IOException {

        List<String[]> normalisedCodeList = new ArrayList<>();
        // Java source code CSV file path

        // Loop thoirugh Java files
        File dir = new File("assets/EMPLOYEE");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File javaFile : directoryListing) {
                System.out.println(javaFile.getName());
                Integer fileName = Integer.parseInt((javaFile.getName().replace(".java","")).replace("employee_",""));
                char[] javaCodeFile = null;
                try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        builder.append(line);
                        builder.append(System.lineSeparator());
                        line = reader.readLine();
                    }
                    javaCodeFile = builder.toString().toCharArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String normalisedCode = normaliseCode(javaCodeFile);
//                System.out.println(normalisedCode);
//                normalisedCodeList.add(new String[]{String.valueOf(javaCodeFile), Integer.toString(fileName)});
                normalisedCodeList.add(new String[]{normalisedCode, Integer.toString(fileName)});
            }


        }

        // Write the normalised code list to a CSV file
//        try (CSVWriter writer = new CSVWriter(new FileWriter("assets/normalisedCodeEmployee.csv"))) {
//            writer.writeAll(normalisedCodeList);
//        }




    }

    public static String normaliseCode(char[] javaCode) {
        String newCode = "";


        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(javaCode);
        // Remove Comments in the code
        parser.setCompilerOptions(new java.util.HashMap<String, String>() {{
            put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
        }});

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

//         Remove package [package_name] line in the code (package declaration)
        PackageRemover packageRemover = new PackageRemover();
        cu.accept(packageRemover);

//         Remove library imports
        ImportRemover importRemover = new ImportRemover();
        cu.accept(importRemover);

//         Variable related code
        VariableNameVisitor variableVisitor = new VariableNameVisitor();
        cu.accept(variableVisitor);

        List<String> variableNames = variableVisitor.getVariableNames();
        for (int i = 0; i < variableNames.size(); i++) {
            VariableNameChanger variableNameChanger = new VariableNameChanger(variableNames.get(i), "VAR" + i);
            cu.accept(variableNameChanger);
            newCode = cu.toString();
        }


        // Functions related code
        FunctionNameVisitor functionVisitor = new FunctionNameVisitor();
        cu.accept(functionVisitor);

        List<String> functionNames = functionVisitor.getFunctionNames();

        for (int j = 0; j < functionNames.size(); j++) {
//            if (functionNames.get(j) != "main") {
                System.out.println(functionNames.get(j));

                FunctionNameChanger functionNameChanger = new FunctionNameChanger(functionNames.get(j), "FUNC" + j);
                cu.accept(functionNameChanger);

                newCode = cu.toString();
//            }
        }

        // Remove Println lines
        PrintlnRemover printlnRemover = new PrintlnRemover();
        cu.accept(printlnRemover);
        newCode =  cu.toString();

//        Print string return mrthods
//        MethodVisitor visitor = new MethodVisitor();
//        cu.accept(visitor);

//        String[] stringMethods = visitor.getStringMethods();
//        for (String method : stringMethods) {
//            System.out.println(method);
//        }


        // Remove new lines
//        NewlineRemover visitor = new NewlineRemover();
//        cu.accept(visitor);
//        newCode = cu.toString();
        newCode = newCode.replaceAll("\n","");

        //Remove extra whitespaces
        newCode = newCode.trim().replaceAll(" +", " ");

//        System.out.println(newCode);
//        return cu.toString();
                return newCode;

    }


    public static List<String> loadCsvData(String fileName) {
        List<String> csvData = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(fileName));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
//                String values = line.split(",");
                csvData.add(line);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return csvData;
    }





}
