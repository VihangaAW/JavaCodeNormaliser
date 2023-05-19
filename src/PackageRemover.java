import org.eclipse.jdt.core.dom.*;

public class PackageRemover extends ASTVisitor {

    @Override
    public boolean visit(CompilationUnit node) {
        PackageDeclaration packageDeclaration = node.getPackage();
        if (packageDeclaration != null) {
            // Remove package declaration
            node.setPackage(null);
        }
        return super.visit(node);
    }
}
