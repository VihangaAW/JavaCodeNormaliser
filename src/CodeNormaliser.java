import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;

public class CodeNormaliser extends ASTVisitor {
//    public boolean visit(VariableDeclarationFragment node) {
//        System.out.println(node.getName().getIdentifier());
//        return true;
//    }

    public static void main(String[] args) {
        String javaCode = "/* This is a comment */\npublic class MyClass {\n" +
                "    // This is also a comment\n" +
                "    public static void main(String[] args) {\n" +
                "        int x = 10; // This is a comment\n" +
                "x = x + 1; \n" +
                "        // This is another comment\n" +
                "        System.out.println(\"x = \" + x);\n" +
                "    }\n" +
                "  public void myMethod() {\n" +
                "    int x = 0;\n" +
                "    System.out.println(\"Hello, world!\");\n" +
                "  }\n" +
                "}";

        System.out.println(javaCode);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(javaCode.toCharArray());
        parser.setCompilerOptions(new java.util.HashMap<String, String>() {{
            put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
        }});

        CompilationUnit unit = (CompilationUnit) parser.createAST(null);

//        Remove Comments in the code
        String codeWithoutComments = unit.toString().replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//.*", "");
//        System.out.println(codeWithoutComments);

        getSourceCodeVariableNames(javaCode);
    }

    public static void getSourceCodeVariableNames(String javaCode) {
        String newCode = "";
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(javaCode.toCharArray());

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

//        Variable related code
        VariableNameVisitor variableVisitor = new VariableNameVisitor();
        cu.accept(variableVisitor);

        List<String> variableNames = variableVisitor.getVariableNames();
        for (String variableName : variableNames) {
//            System.out.println(variableName);
        }
        for (int i = 0; i < variableNames.size(); i++) {
            VariableNameChanger variableNameChanger = new VariableNameChanger(variableNames.get(i), "VAR" + i);
            cu.accept(variableNameChanger);

            newCode = cu.toString();
//              System.out.println(newCode);

        }


//Functions related code
        FunctionNameVisitor functionVisitor = new FunctionNameVisitor();
        cu.accept(functionVisitor);

        List<String> functionNames = functionVisitor.getFunctionNames();
//        System.out.println("Function names: " + functionNames);

        for (int i = 0; i < functionNames.size(); i++) {
            if (functionNames.get(i) != "main") {
                FunctionNameChanger functionNameChanger = new FunctionNameChanger(functionNames.get(i), "FUNC" + i);
                cu.accept(functionNameChanger);

                newCode = cu.toString();
            }
        }
        System.out.println(newCode);

    }

}
