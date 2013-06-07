package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimRuntimeException;

/**
 * Core consist of a city and modules extending it.
 */
@SuppressWarnings("unchecked")
public class Core
{

  int          moduleCount;
  private City city;
  private int  i = 0;

  public Core() {
    moduleCount = 0;

    city = new City( this );
  }

  public int getModuleCount() {
    return moduleCount;
  }

  public City getCity() {
    return city;
  }

  /**
   * Creates new Module.
   * This method differs from the newModule(String name, ModuleCreator creator) in that it does not fills a new
   * module with extensions.
   * 
   * @see pl.edu.agh.cs.kraksim.core.Core.newModule(String name, ModuleCreator creator)
   * 
   * Extensions have to be manually assigned (satisfying the contract for
   * extension classes given by creatorClass).
   */
  public Module newModule(String name, Class<? extends ModuleCreator> creatorClass)
      throws InvalidClassSetDefException
  {
    nextModule();

    try {
      return new Module( name, this, moduleCount - 1, creatorClass );
    }
    catch (InvalidClassSetDefException e) {
      popModule();
      throw e;
    }
  }

  /**
   * Creates a new module and fills it with extensions.
   * 
   * creator must be an instance of a class derived (directly) from
   * ModuleCreator defining all ModuleCreator's type parameters. These type
   * parameters define what extension classes are used in the module (for
   * details see ModuleCreator class). For example: class MyCreator extends
   * ModuleCreator<MyCityExt, MyNodeExt, MyGatewayExt, MyIntersectionExt,
   * MyLinkExt, MyLaneExt>
   * 
   * defines that in this module city extensions will be objects of class
   * MyCityExt, node extensions will be objects of class MyNodeExt, ...
   * 
   * ModuleCreator create*Extension() methods will be called to assign an
   * apprioprate extension to every element in the core.
   * 
   * After creating all extensions, postCreate() method is called on every
   * extension which class implements PostCreateOp interface.
   * 
   * All created extensions are instances of apprioprate classes - it is
   * guaranteed by create*Extension() methods' return types.
   * 
   * @throws InvalidClassSetDefException if creator class does not meet the
   * mentioned requirements.
   * 
   * @throws ModuleCreationException if an error occured, probably some
   * extension in the module cannot be created (ExtensionCreationException) or
   * an extension was returned, where it was not expected
   * (ExtensionUnsupportedException). You can use the inner exception to get
   * the detailed cause.
   * 
   * @see ModuleCreator class.
   */
  public Module newModule(String name, ModuleCreator creator)
      throws InvalidClassSetDefException,
      ModuleCreationException
  {
    nextModule();

    try {
      return new Module( name, this, moduleCount - 1, creator );
    }
    catch (InvalidClassSetDefException e) {
      popModule();
      throw e;
    }
    catch (ModuleCreationException e) {
      popModule();
      throw e;
    }
  }

  private void nextModule() {
    moduleCount++;

    try {
      city.applyElementVisitor( new UniformElementVisitor() {

        @Override
        public void visitUniformly(Element element) {
          element.fireNewExtension();
        }
      } );
    }
    catch (VisitingException e) {
      // Should never happen.
      throw new KraksimRuntimeException( e );
    }
  }

  private void popModule() {
    moduleCount--;

    try {
      city.applyElementVisitor( new UniformElementVisitor() {

        @Override
        public void visitUniformly(Element element) {
          element.firePopExtension();
        }
      } );
    }
    catch (VisitingException e) {
      // Should never happen.
      throw new KraksimRuntimeException( e );
    }
  }

  /**
   * May be called after creation of all modules. Can save some space by
   * shrinking arrays containing extensions for elements to the minimum
   * possible length.
   */
  public void packModules() {
    try {
      city.applyElementVisitor( new UniformElementVisitor() {

        @Override
        public void visitUniformly(Element element) {
          element.firePackExtensions();
        }
      } );
    }
    catch (VisitingException e) {
      // Should never happen.
      throw new KraksimRuntimeException( e );
    }
  }

  public int getNextNumber() {
    return i++;
  }
}
