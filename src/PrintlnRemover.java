import org.eclipse.jdt.core.dom.*;

public class PrintlnRemover extends ASTVisitor {
    public boolean visit(MethodInvocation node) {
        if (node.getName().getFullyQualifiedName().equals("println")
                && node.getExpression() instanceof Name
                && ((Name) node.getExpression()).getFullyQualifiedName().equals("System.out")) {

            ASTNode parent = node.getParent();

            if (parent instanceof ExpressionStatement) {
                ASTNode grandparent = parent.getParent();

                if (grandparent instanceof Block) {
                    Block block = (Block) grandparent;
                    int index = block.statements().indexOf(parent);
                    block.statements().remove(index);
                }
            }
        }

        return super.visit(node);
    }
}
