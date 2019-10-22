package org.ektorp.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.ektorp.support.CouchDbDocument;
import org.junit.Ignore;
import org.junit.Test;


public class StreamingJsonSerializerTest {

  @Test
  @Ignore
  public void testToJson() throws IOException {
    JsonSerializer js = new StreamingJsonSerializer(new ObjectMapper());
    BulkOperation op = js.createBulkOperation(createTestData(10000), false);
    IOUtils.copy(op.getData(), System.out);
  }

  private List<?> createTestData(int size) {
    List<TestDoc> objects = new ArrayList<TestDoc>(size);
    for (int i = 0; i < size; i++) {
      objects.add(new TestDoc("id_" + i, "rev", "name_" + i));
    }
    return objects;
  }

  @SuppressFBWarnings("serial")
  public static class TestDoc extends CouchDbDocument {

    String name;

    public TestDoc(String id, String rev, String name) {
      setId(id);
      setRevision(rev);
      setName(name);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

  }

}