package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

/* 
 * Module
 * 
 * Module groups set of extensions working together to offer
 * some functionality.
 * 
 * In our architecture this grouping is only logical. In general
 * extensions of the same module do not reference each other.
 * They only reference to and are referenced by core elements,
 * which they extend.
 * 
 * To get an extension for a given element, some kind of a handle
 * is needed to specify which module to get an extension from.
 * A module object represents only this *handle*.
 */
@SuppressWarnings("unchecked")
public class Module
{

  /* name, for informative purposes */
  private final String name;

  private final Core   core;

  /* 
   * Extensions to every element are stored in an array. See Element.extensions
   * Extensions of this module are stored in key-th cell of this array.
   */
  final int            key;

  /* Set of extension classes */
  final EntityClassSet extClassSet;

  /* See Core.newModule() */
  Module(String name, Core core, int key, Class<? extends ModuleCreator> creatorClass)
      throws InvalidClassSetDefException
  {
    this( name, core, key, EntityClassSet.createFromCreatorClass( creatorClass ) );
  }

  /* See Core.newModule() */
  Module(String name, Core core, int key, final ModuleCreator creator)
      throws InvalidClassSetDefException,
      ModuleCreationException
  {
    this( name, core, key, EntityClassSet.createFromCreatorClass( creator.getClass() ) );

    creator.setModule( this );
    try {
      City city = core.getCity();
      city.applyElementVisitor( new CreatingVisitor( creator ) );
      city.applyElementVisitor( new PostCreatingVisitor() );
    }
    catch (VisitingException e) {
      throw new ModuleCreationException( "cannot create module " + name, e );
    }
  }

  private Module(String name, Core core, int key, EntityClassSet extClassSet)
      throws InvalidClassSetDefException
  {
    this.name = name;
    this.core = core;
    this.extClassSet = extClassSet;
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public Core getCore() {
    return core;
  }

  //CONCRETE_ELEMENT_TYPE_DEPENDENT
  private class CreatingVisitor implements ElementVisitor
  {

    private final ModuleCreator creator;

    private CreatingVisitor(ModuleCreator creator) {
      this.creator = creator;
    }

    public void visit(City city) throws VisitingException {
      try {
        city.setExtension( Module.this, creator.createCityExtension( city ) );
      }
      catch (KraksimException e) {
        throw new VisitingException( e );
      }
    }

    public void visit(Gateway gateway) throws VisitingException {
      try {
        gateway.setExtension( Module.this, creator.createGatewayExtension( gateway ) );
      }
      catch (KraksimException e) {
        throw new VisitingException( e );
      }
    }

    public void visit(Intersection intersection) throws VisitingException {
      try {
        intersection.setExtension( Module.this, creator
            .createIntersectionExtension( intersection ) );
      }
      catch (KraksimException e) {
        throw new VisitingException( e );
      }
    }

    public void visit(Link link) throws VisitingException {
      try {
        link.setExtension( Module.this, creator.createLinkExtension( link ) );
      }
      catch (KraksimException e) {
        throw new VisitingException( e );
      }
    }

    public void visit(Lane lane) throws VisitingException {
      try {
        lane.setExtension( Module.this, creator.createLaneExtension( lane ) );
      }
      catch (KraksimException e) {
        throw new VisitingException( e );
      }
    }
  }

  private class PostCreatingVisitor extends UniformElementVisitor
  {

    @Override
    void visitUniformly(Element element) throws VisitingException
    {
      try {
        Object ext = element.getExtension( Module.this );
        if ( ext instanceof PostCreateOp ) ((PostCreateOp) ext).postCreate();
      }
      catch (ExtensionCreationException e) {
        throw new VisitingException( e );
      }
    }
  }
}
