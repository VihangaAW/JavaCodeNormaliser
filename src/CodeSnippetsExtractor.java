import com.opencsv.CSVWriter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CodeSnippetsExtractor extends ASTVisitor {

    public static void main(String[] args) throws IOException {

        List<String[]> normalisedCodeList = new ArrayList<>();
//        List<String[]> normalisedCodeList = new ArrayList<>();

        // Java source code CSV file path

        // Loop thoirugh Java files
        File dir = new File("assets/javaSubmissions");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File javaFile : directoryListing) {
                System.out.println(javaFile.getName());
                Integer fileName = Integer.parseInt(javaFile.getName().replace(".java",""));
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



                ASTParser parser = ASTParser.newParser(AST.JLS13);
                parser.setSource(javaCodeFile);
                // Remove Comments in the code
                parser.setCompilerOptions(new HashMap<String, String>() {{
                    put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
                }});

                parser.setKind(ASTParser.K_COMPILATION_UNIT);

                CompilationUnit cu = (CompilationUnit) parser.createAST(null);

                String[] actionTypes = new String[]{"ExtractClass", "ExtractMethod", "NormaliseCode"};
                int actionType = 1;
                if(actionType == 0){
                    // Code break into Classes
                    ClassContentVisitor visitor = new ClassContentVisitor();
                    cu.accept(visitor);

                    String[] classContents = visitor.getClassContents();
                    for (String classContent : classContents) {
                        classContent = normaliseCode(classContent.toCharArray());
                    }

                    if(classContents.length == 2){
                        normalisedCodeList.add(new String[]{classContents[0], classContents[1], Integer.toString(fileName)});
                    }
                    else{
                        // Some students only provided Payroll class
                        normalisedCodeList.add(new String[]{classContents[0], "", Integer.toString(fileName)});
                    }

                }
                else if(actionType == 1){
                    String methodCode = normaliseCode(javaCodeFile, "add");
                    normalisedCodeList.add(new String[]{methodCode, Integer.toString(fileName)});

                }
//                else {
//
//                }






            }


        }

        // Write the normalised code list to a CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter("assets/normalisedCode_MethodAddEmployeeOld.csv"))) {
            writer.writeAll(normalisedCodeList);
        }




    }

    public static String normaliseCode(char[] javaCode, String functionName) {
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
//            newCode = cu.toString();
        }


        // Functions related code
        FunctionNameVisitor functionVisitor = new FunctionNameVisitor();
        cu.accept(functionVisitor);

        List<String> functionNames = functionVisitor.getFunctionNames();
        String convertedFunctionName = "";
        for (int j = 0; j < functionNames.size(); j++) {
//            if (functionNames.get(j) != "main") {
            System.out.println(functionNames.get(j));
            if(((functionNames.get(j)).toLowerCase()).contains(functionName)){
                convertedFunctionName = "FUNC" + j;
                System.out.println(convertedFunctionName);
            }
            FunctionNameChanger functionNameChanger = new FunctionNameChanger(functionNames.get(j), "FUNC" + j);
            cu.accept(functionNameChanger);

//            newCode = cu.toString();
//            }
        }

        // Remove Println lines
        PrintlnRemover printlnRemover = new PrintlnRemover();
        cu.accept(printlnRemover);

//        System.out.println(cu.toString());
        if(convertedFunctionName != ""){
            MethodVisitor methodVisitor = new MethodVisitor();
            cu.accept(methodVisitor);

            newCode = methodVisitor.getMethodCode(convertedFunctionName.toLowerCase());
        }
        System.out.println(newCode);


//        newCode =  cu.toString();


//        String[] stringMethods = visitor.getStringMethods();
//        for (String method : stringMethods) {
//            System.out.println(method);
//        }


        // Remove new lines
//        NewlineRemover visitor = new NewlineRemover();
//        cu.accept(visitor);
//        newCode = cu.toString();
        if(newCode != null){
            newCode = newCode.replaceAll("\n","");

            //Remove extra whitespaces
            newCode = newCode.trim().replaceAll(" +", " ");
        }

//        System.out.println(newCode);
//        return cu.toString();




        return newCode;

    }




//Used for classes
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





}
