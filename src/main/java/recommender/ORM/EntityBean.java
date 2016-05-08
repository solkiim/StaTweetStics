package edu.brown.cs.suggest.ORM;
import java.util.Objects;


/**
 * An EntityBean is the Object in memory for an entity.
 *
 * @param <E> an Entity type
 */
public class EntityBean<E extends Entity> implements Entity {
  /** The entities ID. */
  private String id;

  /**
   * Constructor.
   *
   * @param idIn Sets the id of entity bean
   */
  public EntityBean(String idIn) {
    this.id = idIn;
  }

  /**
   * Returns the id.
   *
   * @return the id
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Entity equality method.
   *
   * @return true if o equals this, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    try {
      Entity entity = (Entity) o;
      return getId().equals(entity.getId());
    } catch (ClassCastException cce) {
      return false;
    }
  }

  /**
   * Entity hashing method.
   *
   * @return Hashcode for an entity
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
