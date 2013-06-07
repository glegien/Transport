package pl.edu.agh.cs.kraksim.core;

/** Base class for extendable core elements */
public abstract class Element
{

  protected final Core     core;
  /**
   * An array of extensions for this element. First
   * core.moduleCount cells are filled, the rest are
   * nulls.
   */
  private Object[]         extensions;
  private static final int INITIAL_EXTENSIONS_LENGTH = 4;

  protected Element(Core core) {
    this.core = core;
    extensions = new Object[INITIAL_EXTENSIONS_LENGTH];
  }

  /**
   * Returns extension class for this type of element in module.
   * NULL.class means there is no extension for this element.
   */
  @SuppressWarnings("unchecked")
  protected abstract Class getExtensionClass(Module module);

  /** Returns extension object for this element in the given module. */
  public final Object getExtension(final Module module) {
    return extensions[module.key];
  }

  /**
   * Sets extension for this element in module to ext.
   * 
   * The class of ext must satisfy the contract defined
   * in Core.newModule()
   * 
   * Throws ExtensionUnsupportedException if the contract says
   * NULL is the class for extensions of this element. 
   * 
   * Throws InvalidExtensionClassException if ext is not an
   * instance of class (nor its subclasses) for extensions
   * of this type of element specified in the contract.
   */
  @SuppressWarnings("unchecked")
  final void setExtension(Module module, Object ext)
      throws ExtensionUnsupportedException,
      InvalidExtensionClassException
  {
    if ( ext == null ) {
      extensions[module.key] = null;
      return;
    }

    Class extClass = getExtensionClass( module );
    if ( extClass == NULL.class ) throw new ExtensionUnsupportedException( String.format(
        "Element: %s, module: %s", this, module.getName() ) );
    if ( !extClass.isInstance( ext ) ) throw new InvalidExtensionClassException( String
        .format( "Expected: %s, got: %s", extClass, ext.getClass() ) );
    extensions[module.key] = ext;
  }

  /** Fired when new module is created. */
  final void fireNewExtension() {
    int c = core.moduleCount;
    if ( c > extensions.length ) {
      int m = Math.max( extensions.length, 1 );
      while ( m < c )
        m *= 2;
      resizeExtensions( m );
    }
  }

  /* Fired when module is popped. */
  final void firePopExtension() {
    extensions[core.moduleCount] = null;
  }

  /* Fired when modules are packed. */
  final void firePackExtensions() {
    resizeExtensions( core.moduleCount );
  }

  private void resizeExtensions(int m) {
    Object[] newExtensions = new Object[m];
    for (int i = 0; i < Math.min( extensions.length, m ); i++) {
      newExtensions[i] = extensions[i];
    }
    extensions = newExtensions;
  }
}
