package edu.brown.cs.suggest.ORM;

/**
 * An entity is an object that is defined by an ID.
 */
public interface Entity extends Comparable<Entity> {

  /**
   * This method returns the ids of the entity.
   *
   * @return id of entity
   */
  String getId();

  /**
   * Since entitys are defined by their ID, we use ID to compare them.
   *
   * @param e oEntity being compared
   * @return an integer in line with the comparable interface
   */
  @Override
  default int compareTo(Entity e) {
    return getId().compareTo(e.getId());
  }
}
