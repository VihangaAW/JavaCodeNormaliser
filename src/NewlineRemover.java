import org.eclipse.jdt.core.dom.*;

public class NewlineRemover extends ASTVisitor {

    public boolean visit(TextElement node) {
        String text = node.getText();
        node.setText(text.replaceAll("\\n", ""));
        return super.visit(node);
    }

}
