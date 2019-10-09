package org.ektorp.impl;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.ektorp.util.Exceptions;

public class BulkDocumentWriter {

  private final ObjectMapper objectMapper;

  public BulkDocumentWriter(ObjectMapper om) {
    objectMapper = om;
  }

  /**
   * Writes the objects collection as a bulk operation document. The output stream is flushed and
   * closed by this method.
   */
  public void write(Collection<?> objects, boolean allOrNothing, OutputStream out) {
    try(JsonGenerator jg = objectMapper.getFactory().createGenerator(out, JsonEncoding.UTF8)) {
      jg.writeStartObject();
      if (allOrNothing) {
        jg.writeBooleanField("all_or_nothing", true);
      }
      jg.writeArrayFieldStart("docs");
      for (Object o : objects) {
        jg.writeObject(o);
      }
      jg.writeEndArray();
      jg.writeEndObject();
      jg.flush();
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }

  public InputStream createInputStreamWrapper(boolean allOrNothing, InputStream in) {
    List<InputStream> seq = new ArrayList<>(3);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (JsonGenerator jg = objectMapper.getFactory()
        .createGenerator(byteArrayOutputStream, JsonEncoding.UTF8)) {
      jg.writeStartObject();

      if (allOrNothing) {
        jg.writeBooleanField("all_or_nothing", true);
      }

      jg.writeFieldName("docs");
      jg.writeRaw(':');
      jg.flush();
      seq.add(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
      seq.add(in);
      byteArrayOutputStream.reset();
      jg.writeEndObject();
      jg.flush();
      seq.add(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }

    return new SequenceInputStream(Collections.enumeration(seq));
  }

}
