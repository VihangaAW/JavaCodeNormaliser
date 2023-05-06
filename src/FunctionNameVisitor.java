import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FunctionNameVisitor extends ASTVisitor {

    private List<String> functionNames = new ArrayList<String>();

    public boolean visit(MethodDeclaration node) {
        String functionName = node.getName().getIdentifier();
        functionNames.add(functionName);
        return super.visit(node);
    }

    public List<String> getFunctionNames() {
        return functionNames;
    }

}
