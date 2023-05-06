import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import java.util.ArrayList;
import java.util.List;

public class VariableNameVisitor extends ASTVisitor {

    private List<String> variableNames = new ArrayList<>();

    public boolean visit(VariableDeclarationFragment node) {
        SimpleName name = node.getName();
        variableNames.add(name.getIdentifier());
        return false;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }
}
