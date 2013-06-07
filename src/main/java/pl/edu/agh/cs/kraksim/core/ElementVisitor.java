package pl.edu.agh.cs.kraksim.core;

//CONCRETE_ELEMENT_TYPE_DEPENDENT
/*
 * Used to visit all core elements and perform specific
 * action based on element type.
 * 
 * You can use VisitingException in method implementation to
 * wrap real exception, if caught.
 *  
 * There are visit() methods only for concrete classes of elements.
 * It means, there is no visit() method for elements of Node class. 
 */
interface ElementVisitor
{

  void visit(City city) throws VisitingException;

  void visit(Gateway gateway) throws VisitingException;

  void visit(Intersection intersection) throws VisitingException;

  void visit(Link link) throws VisitingException;

  void visit(Lane lane) throws VisitingException;
}
