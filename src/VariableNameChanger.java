import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;

public class VariableNameChanger extends ASTVisitor {

    private String oldName;
    private String newName;

    public VariableNameChanger(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public boolean visit(SimpleName node) {
        if (node.getIdentifier().equals(oldName)) {
            node.setIdentifier(newName);
        }
        return super.visit(node);
    }
}
