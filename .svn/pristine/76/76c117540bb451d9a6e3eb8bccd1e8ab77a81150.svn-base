package pl.edu.agh.cs.kraksim.core;

//ELEMENT_TYPE_DEPENDENT
/* 
 * A visitor which visits all elements in the same way.
 * 
 * Only visitUniformly() method is to be subclassed.
 * 
 * See ElementVisitor. 
 */
abstract class UniformElementVisitor implements ElementVisitor
{

  abstract void visitUniformly(Element element) throws VisitingException;

  public final void visit(City city) throws VisitingException {
    visitUniformly( city );
  }

  public final void visit(Gateway gateway) throws VisitingException {
    visitUniformly( gateway );
  }

  public final void visit(Intersection intersection) throws VisitingException {
    visitUniformly( intersection );
  }

  public final void visit(Link link) throws VisitingException {
    visitUniformly( link );
  }

  public final void visit(Lane lane) throws VisitingException {
    visitUniformly( lane );
  }
}
