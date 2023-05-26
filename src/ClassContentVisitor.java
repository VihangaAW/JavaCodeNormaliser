import org.eclipse.jdt.core.dom.*;

public class ClassContentVisitor extends ASTVisitor {

        private String[] classContents;
        private int classCount;

        public ClassContentVisitor() {
            classContents = new String[100]; // Assuming there are no more than 100 classes
            classCount = 0;
        }

        public String[] getClassContents() {
            String[] trimmedClassContents = new String[classCount];
            System.arraycopy(classContents, 0, trimmedClassContents, 0, classCount);
            return trimmedClassContents;
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            String classContent = node.toString();
            classContents[classCount++] = classContent;
            return super.visit(node);
        }

}

