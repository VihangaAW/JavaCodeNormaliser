import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

class MethodVisitor extends ASTVisitor {

    private String[] methods;
    private int methodCount;

    public MethodVisitor() {
        methods = new String[100]; // Assuming there are no more than 100 methods
        methodCount = 0;
    }

    public String[] getMethods() {
        String[] trimmedMethods = new String[methodCount];
        System.arraycopy(methods, 0, trimmedMethods, 0, methodCount);
        return trimmedMethods;
    }

    public String getMethodCode(String methodName) {
        for (int i = 0; i < methodCount; i++) {
            int firstLineEndIndex = methods[i].indexOf("{");
            // If method exists
            if(firstLineEndIndex>0){
//                System.out.println(methods[i].substring(0, firstLineEndIndex));
                String methodFirstLine = (methods[i].substring(0, firstLineEndIndex)).toLowerCase();
//                System.out.println(methodFirstLine+" = "+methodName);
                if (methodFirstLine.contains(methodName)) {
//                    System.out.println(methods[i]);
                    return methods[i];
                }
            }
        }
        return null;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        String methodCode = node.toString();
        methods[methodCount++] = methodCode;
        return super.visit(node);
    }

}
