import org.eclipse.jdt.core.dom.*;

public class ImportRemover extends ASTVisitor {
    @Override
    public boolean visit(ImportDeclaration node) {
        // Remove import
        node.delete();
        return false;
    }
}
