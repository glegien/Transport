package pl.edu.agh.cs.kraksim.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

//ELEMENT_TYPE_DEPENDENT
/* 
 * Used internally to store interfaces offered by module extensions
 * or classes implementing modules extensions.
 */
@SuppressWarnings("unchecked")
class EntityClassSet
{  
  private final Class cityClass;
  private final Class nodeClass;
  private final Class gatewayClass;
  private final Class intersectionClass;
  private final Class linkClass;
  private final Class laneClass;

  /*
   * Extracts interface or class set from ModuleView direct subclass with
   * defined all ModuleView's type parameters. These type parameters must be
   * interfaces or classes.
   * 
   * Throws InvalidClassSetDefException if above requirements are not met.
   * 
   * See ModuleView constructor.
   */
  static EntityClassSet createFromViewClass(Class<? extends ModuleView> viewClass)
      throws InvalidClassSetDefException
  {
    return new EntityClassSet( viewClass, ModuleView.class );
  }

  /*
   * Extracts interface or class set from ModuleCreator direct subclass with
   * defined all ModuleCreator's type parameters. These type parameters must
   * be interfaces or classes.
   * 
   * Throws InvalidClassSetDefException if above requirements are not met.
   * 
   * See Core.newModule()
   */
  static EntityClassSet createFromCreatorClass(Class<? extends ModuleCreator> creatorClass)
      throws InvalidClassSetDefException
  {
    return new EntityClassSet( creatorClass, ModuleCreator.class );
  }

  /* constructor called only from the two above methods */
  private EntityClassSet(Class defClass, Class superClass) throws InvalidClassSetDefException {
    Type t = defClass.getGenericSuperclass();
    /*
     * t != null, because we are assured, that defClass extends ModuleView
     * or ModuleCreator
     */

    ParameterizedType pt;
    try {
      pt = (ParameterizedType) t;
    }
    catch (ClassCastException e) {
      throw new InvalidClassSetDefException( defClass.getCanonicalName()
                                             + " must directly extend "
                                             + superClass.getCanonicalName() );
    }

    if ( !pt.getRawType().equals( superClass ) ) throw new InvalidClassSetDefException(
        defClass.getCanonicalName() + " must directly extend " + superClass.getCanonicalName() );

    try {
      Type[] ptArgs = pt.getActualTypeArguments();
      cityClass = (Class) ptArgs[0];
      nodeClass = (Class) ptArgs[1];
      gatewayClass = (Class) ptArgs[2];
      intersectionClass = (Class) ptArgs[3];
      linkClass = (Class) ptArgs[4];
      laneClass = (Class) ptArgs[5];
    }
    catch (ClassCastException e) {
      throw new InvalidClassSetDefException(
          "all type parameters to " + defClass.getCanonicalName()
              + " must be classes (in essence, not parametrized types)" );
    }
  }

  Class getCityClass() {
    return cityClass;
  }

  Class getNodeClass() {
    return nodeClass;
  }

  Class getGatewayClass() {
    return gatewayClass;
  }

  Class getIntersectionClass() {
    return intersectionClass;
  }

  Class getLinkClass() {
    return linkClass;
  }

  Class getLaneClass() {
    return laneClass;
  }

  // ELEMENT_TYPE_DEPENDENT
  /*
   * Checks whether this class set matches contract of
   * class set given by set argument.
   * 
   * Class set A matches contract of class set B if every
   * class in set A matches contract (Class.isAssignableFrom())
   * of related class in set B.
   * 
   * This applies also to interfaces. (Runtime information
   * about interface is a java.lang.Class object)
   */
  boolean matchesContractOf(EntityClassSet set) {
    return matches( set.cityClass, cityClass ) && matches( set.nodeClass, nodeClass )
           && matches( set.gatewayClass, gatewayClass )
           && matches( set.intersectionClass, intersectionClass )
           && matches( set.linkClass, linkClass ) && matches( set.laneClass, laneClass );
  }

  @SuppressWarnings("unchecked")
  private boolean matches(Class contract, Class cls)
  {
    return contract == NULL.class || contract.isAssignableFrom( cls );
  }
}
