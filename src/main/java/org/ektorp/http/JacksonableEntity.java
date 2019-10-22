package org.ektorp.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.entity.AbstractHttpEntity;
import org.ektorp.util.Assert;
import org.ektorp.util.Exceptions;

/**
 * A streamed entity that obtains its content from an {@link Object}. The content obtained from the
 * {@link Object} instance can optionally be buffered in a byte array in order to make the entity
 * self-contained and repeatable.
 *
 * @see org.apache.http.entity.SerializableEntity
 */
@NotThreadSafe
@SuppressFBWarnings(value = "HE_EQUALS_USE_HASHCODE")
public class JacksonableEntity extends AbstractHttpEntity {

  private byte[] objSer;

  private final Object objRef;

  private final ObjectMapper objectMapper;

  /**
   * Creates new instance of this class.
   *
   * @param ser       input
   * @param bufferize tells whether the content should be stored in an internal buffer
   */
  public JacksonableEntity(final Object ser, final boolean bufferize,
      final ObjectMapper objectMapper) {
    super();
    Assert.notNull(ser, "Source object");
    this.objectMapper = objectMapper;
    this.objRef = ser;
    if (bufferize) {
      try {
        createBytes(ser);
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
    }
    setContentType("application/json");
  }

  public JacksonableEntity(final Object ser, final ObjectMapper objectMapper) {
    this(ser, false, objectMapper);
  }


  private void createBytes(final Object ser) throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    objectMapper.writeValue(baos, ser);
    this.objSer = baos.toByteArray();
  }

  public InputStream getContent() throws IOException, IllegalStateException {
    if (this.objSer == null) {
      createBytes(this.objRef);
    }
    return new ByteArrayInputStream(this.objSer);
  }

  public long getContentLength() {
    if (this.objSer == null) {
      return -1;
    } else {
      return this.objSer.length;
    }
  }

  public boolean isRepeatable() {
    return true;
  }

  public boolean isStreaming() {
    return this.objSer == null;
  }

  public void writeTo(final OutputStream outstream) throws IOException {
    Assert.notNull(outstream, "Output stream");
    if (this.objSer == null) {
      objectMapper.writeValue(outstream, this.objRef);
      outstream.flush();
    } else {
      outstream.write(this.objSer);
      outstream.flush();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof JacksonableEntity)) {
      return false;
    }
    JacksonableEntity other = (JacksonableEntity) o;
    return this.objRef == other.objRef;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(objRef, objectMapper);
    result = 31 * result + Arrays.hashCode(objSer);
    return result;
  }

  public Object getTarget() {
    return objRef;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

}