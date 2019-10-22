package org.ektorp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.ektorp.util.Assert;

@JsonInclude(Include.NON_NULL)
public class Attachment implements Serializable {

  private static final long serialVersionUID = 1L;
  private String id;
  private String contentType;
  private long length;
  private String dataBase64;
  private boolean stub;
  private int revpos;

  @SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD")
  private String digest;

  private Map<String, Object> anonymous;

  /**
   * Constructor that takes data as String. The data must be base64 encoded single line of
   * characters, so pre-process your data to remove any carriage returns and newlines
   * <p>
   * Useful if you want to save the attachment as an inline attachent.
   *
   * @param data base64-encoded
   */
  public Attachment(String id, String data, String contentType) {
    Assert.hasText(id, "attachmentId must have a value");
    Assert.hasText(contentType, "contentType must have a value");
    Assert.notNull(data, "data input stream cannot be null");
    this.id = id;
    this.contentType = contentType;
    this.dataBase64 = data;
    this.length = data.getBytes(StandardCharsets.UTF_8).length;
  }

  Attachment() {
  }

  @JsonProperty("content_type")
  public String getContentType() {
    return contentType;
  }

  @JsonProperty("content_type")
  void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @JsonIgnore
  public long getContentLength() {
    return length;
  }

  /**
   * Only populated if this attachment was created with data as String constructor.
   */
  @JsonProperty("data")
  public String getDataBase64() {
    return dataBase64;
  }

  @JsonIgnore
  public String getId() {
    return id;
  }

  @JsonIgnore
  void setId(String id) {
    this.id = id;
  }

  void setLength(long contentLength) {
    this.length = contentLength;
  }

  public boolean isStub() {
    return stub;
  }

  void setStub(boolean stub) {
    this.stub = stub;
  }

  public int getRevpos() {
    return revpos;
  }

  public void setRevpos(int revpos) {
    this.revpos = revpos;
  }

  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  public String getDigest() {
    return digest;
  }

  /**
   * @return a Map containing fields that did not map to any other field in the class during object
   * deserializarion from a JSON document.
   */
  @JsonAnyGetter
  public Map<String, Object> getAnonymous() {
    return anonymous();
  }

  @JsonAnySetter
  public void setAnonymous(String key, Object value) {
    anonymous().put(key, value);
  }

  /**
   * Provides lay init for the anonymous Map
   */
  private Map<String, Object> anonymous() {
    if (anonymous == null) {
      anonymous = new HashMap<>();
    }
    return anonymous;
  }
}