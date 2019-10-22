package org.ektorp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Options {

  private Map<String, String> options = new LinkedHashMap<String, String>();

  /**
   * The loaded doc will include the special field '_conflicts' that contains all the conflicting
   * revisions of the document.
   */
  public Options includeConflicts() {
    options.put("conflicts", "true");
    return this;
  }

  /**
   * The loaded doc will include the special field '_revisions' that describes all document
   * revisions that exists in the database.
   */
  public Options includeRevisions() {
    options.put("revs", "true");
    return this;
  }

  /**
   * Retrieve a specific revision of the document.
   */
  public Options revision(String rev) {
    options.put("rev", rev);
    return this;
  }

  /**
   * Adds a parameter to the GET request sent to the database.
   */
  public Options param(String name, String value) {
    options.put(name, value);
    return this;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public boolean isEmpty() {
    return options.isEmpty();
  }

}