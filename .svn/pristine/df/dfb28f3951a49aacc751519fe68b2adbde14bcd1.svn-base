package pl.edu.agh.cs.kraksim.core;

//ELEMENT_TYPE_DEPENDENT (all element types, not only concrete types, must be mentioned in type parameters)
//CONCRETE_ELEMENT_TYPE_DEPENDENT (there is no need for createNodeExtension, as class Node is abstract)
/*
 * Subclasses of this class play two roles:
 * 1) give information what extension classes are in a module:
 *    Subclass must pass only classes or interfaces as ModuleCreator type parameters
 *    while extending it.
 *    The value of CITY parameter is an extension class for city elements in a module.
 *    The value of NODE parameter is an extension class for node elements in a module.
 *    ...
 *    
 *    NULL interface can be used to tell that no extension is expected for elements
 *    of some type.
 *    
 *    If a module is to be created but not yet filled with extensions,
 *    passing subclass (not its instance as a parameter to Core.newModule() suffices. 
 *    
 * 2) provide methods to create extensions of apprioprate types.
 *    
 *    If a module is to be created and filled automatically with extensions,
 *    passing subclass instance is required to Core.newModule().
 *    For every element in the core create*Extension() method will be called
 *    and its result will be assigned as an extension to the element.
 *    
 *    create*Extension() can return null, if no extension is needed for an element
 *    (default implementation). It must return null, if NULL was specified
 *    (in type parameters to ModuleCreator) as an extension class.
 * 
 * See Core.newModule()
 * 
 * Only direct subclassing of ModuleCreator is allowed! 
 */
public abstract class ModuleCreator<CITY, NODE, GATEWAY, INTERSECTION, LINK, LANE> implements
  EntityClassSetDefinition<CITY, NODE, GATEWAY, INTERSECTION, LINK, LANE>
{

  /* called before any of create* methods */
  public abstract void setModule(Module module);

  public CITY createCityExtension(City city) throws ExtensionCreationException {
    return null;
  }

  public GATEWAY createGatewayExtension(Gateway gateway) throws ExtensionCreationException {
    return null;
  }

  public INTERSECTION createIntersectionExtension(Intersection intersection)
      throws ExtensionCreationException
  {
    return null;
  }

  public LINK createLinkExtension(Link link) throws ExtensionCreationException {
    return null;
  }

  public LANE createLaneExtension(Lane lane) throws ExtensionCreationException {
    return null;
  }
}
