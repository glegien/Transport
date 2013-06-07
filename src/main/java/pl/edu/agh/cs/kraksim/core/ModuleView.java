package pl.edu.agh.cs.kraksim.core;

//ELEMENT_TYPE_DEPENDENT
/*
 * Subclasses of this class play two roles:
 * 1) give information what interfaces extensions in some module offer 
 *    
 *    Subclass pass interfaces as ModuleView type parameters
 *    while extending it.
 *    The value of CITY parameter is an interface which city extensions will offer.
 *    The value of NODE parameter is an extension which node extensions will offer.
 *    ...
 *    
 *    NULL interface can be used to tell that no interface is expected for extensions
 *    of elements of some type.
 *    
 *    Concrete classes can be also used in place of interfaces, in rare cases
 *    where view through concrete extension classes is required (not restricted
 *    to some interface).
 *    
 * 2) allow to 'view a module through extension interfaces' or seldom
 *    'view a module through extension implementation' 
 *    
 *    View must be provided with a module in which these extensions are.
 *    The view will check that it satisfies contract defined in point 1)
 *    
 *    Then, ext() methods can be used to 'view at an element extension'
 *    automatically returning extension belonging to the module as a
 *    value of apprioprate type (specified by contract in point 1) ).
 *    
 * Only direct subclassing of ModuleView is allowed!
 */
public abstract class ModuleView<CITY, NODE, GATEWAY, INTERSECTION, LINK, LANE> implements
  EntityClassSetDefinition<CITY, NODE, GATEWAY, INTERSECTION, LINK, LANE>
{

  private final Module module;

  /* 
   * Throws InvalidClassSetDefException if information about interfaces
   * does not meet requirements of point 1) or class is not direct subclass of ModuleView.
   * 
   * Throws UnsatisfiedContractException if extension classes in module does
   * not match contract of interfaces specified in point 1) above. 
   */
  protected ModuleView(Module module)
      throws InvalidClassSetDefException,
      UnsatisfiedContractException
  {
    this.module = module;

    EntityClassSet viewClassSet = EntityClassSet.createFromViewClass( this.getClass() );
    if ( !module.extClassSet.matchesContractOf( viewClassSet ) ) throw new UnsatisfiedContractException(
        "some of " + module.getName() + " extensions classes does not match contract of view "
            + this.getClass().getCanonicalName() );
  }

  @SuppressWarnings("unchecked")
  public CITY ext(City city)
  {
    return (CITY) city.getExtension( module );
  }

  @SuppressWarnings("unchecked")
  public NODE ext(Node node)
  {
    return (NODE) node.getExtension( module );
  }

  @SuppressWarnings("unchecked")
  public GATEWAY ext(Gateway gateway)
  {
    return (GATEWAY) gateway.getExtension( module );
  }

  @SuppressWarnings("unchecked")
  public INTERSECTION ext(Intersection intersection)
  {
    return (INTERSECTION) intersection.getExtension( module );
  }

  @SuppressWarnings("unchecked")
  public LINK ext(Link link)
  {
    return (LINK) link.getExtension( module );
  }

  @SuppressWarnings("unchecked") 
  public LANE ext(Lane lane)
  {
    return (LANE) lane.getExtension( module );
  }
}
