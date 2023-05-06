import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class FunctionNameChanger extends ASTVisitor {

    private String oldName;
    private String newName;

    public FunctionNameChanger(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public boolean visit(MethodDeclaration node) {
        if (node.getName().getIdentifier().equals(oldName)) {
            node.getName().setIdentifier(newName);
        }
        return super.visit(node);
    }
}
