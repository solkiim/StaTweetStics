package edu.brown.cs.suggest.ORM;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An EntityProxy is a shell representation of an entity. It's internal
 * data is only filled when required.
 *
 * @param <E> an Entity type
 */
public abstract class EntityProxy<E extends Entity> implements Entity {
  /** The id of the entity. */
  protected String id;
  /** The internal representation of the Entity. */
  protected E internal;
  /** A cache of entities. */
  private static Map<String, Entity> cache = new ConcurrentHashMap<>();

  /**
   * Constructor.
   *
   * @param idIn The object's id
   * @param dbIn A reference to the database
   */
  public EntityProxy(String idIn) {
    this.id = idIn;
    //this.db = dbIn;
    fillFromCache();
  }

  /**
   * Returns this ID.
   *
   * @return id of object
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * this is a method that allows for easy interation with the data
   * base.
   *
   * @param db the db to be used
   * @param op the dboperation that is to be preformed
   * @param <T> Any type
   * @return returns an object of type T
   */
  protected static <T> T withConnection(Db.Operation<T> op) {
    try {
      Connection conn = Db.getConnection();
      return op.executeWith(conn);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This method tries to fill from cache before moving to the database.
   */
  public void fill() {
    fillFromCache();
    if (internal != null) {
      return;
    }
    try {
      Connection conn = Db.getConnection();
      fill(conn);
      cacheAdd(id, internal);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * fills the entity from cache if it is there.
   */
  protected abstract void fillFromCache();

  /**
   * Fills the entity from the database.
   *
   * @param conn A connection to the sql database
   * @throws SQLException when connection fails
   */
  protected abstract void fill(Connection conn) throws SQLException;

  /**
   * ToString for EntityProxies.
   *
   * @return A String of this entity proxy
   */
  @Override
  public String toString() {
    String name = getClass().getSimpleName();
    if (internal == null) {
      return String.format("(%s %s)", name, getId());
    }
    return String.format("(%s %s %s)", name, getId(), internal);
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
      return id.equals(entity.getId());
    } catch (ClassCastException cce) {
      return false;
    }
  }

  /**
   * Returns the the entity in the cache if it is there,
   * null otherwise.
   *
   * @param idIn The ID of an entity
   * @return The entity from the cache with input id, if it exists
   */
  public Entity checkCache(String idIn) {
    return cache.get(idIn);
  }

  /**
   * Adds an entity to the cache.
   *
   * @param idIn The ID of an entity
   * @param element The entity
   */
  public void cacheAdd(String idIn, Entity element) {
    assert idIn != null : "idIn == null and should not";
    assert element != null : "element should not equal null";
    cache.put(idIn, element);
  }

  /**
   * Entity hashing method.
   *
   * @return Hashcode for an entity
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
