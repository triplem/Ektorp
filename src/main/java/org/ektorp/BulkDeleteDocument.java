package org.ektorp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.io.Serializable;
import org.ektorp.util.Documents;

/**
 * This class can be used to delete documents in bulk operations. Add an instance for each document
 * to be deleted to the objects collection.
 */
@JsonSerialize(using = BulkDeleteDocument.Serializer.class)
public class BulkDeleteDocument implements Serializable {

  private static final long serialVersionUID = 6517134960185042866L;
  private final String id;
  private final String revision;

  /**
   * Will create a bulk delete document based on the specified object.
   */
  public static BulkDeleteDocument of(Object o) {
    return new BulkDeleteDocument(Documents.getId(o), Documents.getRevision(o));
  }

  public BulkDeleteDocument(String id, String rev) {
    this.id = id;
    this.revision = rev;
  }

  public String getId() {
    return id;
  }

  public String getRevision() {
    return revision;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BulkDeleteDocument) {
      BulkDeleteDocument bd = (BulkDeleteDocument) o;
      return bd.id.equals(id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Dummy setter, only exists because the BulkOperationResponseHandler tries to set revision on all
   * objects, no matter what.
   */
  public void setRevision(String s) {
    // do nothing
  }

  public static class Serializer extends JsonSerializer<BulkDeleteDocument> {

    @Override
    public void serialize(BulkDeleteDocument value, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,
        JsonProcessingException {
      jgen.writeStartObject();
      jgen.writeStringField("_id", value.id);
      jgen.writeStringField("_rev", value.revision);
      jgen.writeBooleanField("_deleted", true);
      jgen.writeEndObject();
    }

  }
}