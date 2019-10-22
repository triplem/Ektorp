package org.ektorp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Map;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.junit.Test;

public class StdCouchDbInstanceTest {

  HttpClient client = mock(HttpClient.class);
  CouchDbInstance instance = new StdCouchDbInstance(client);

  @Test
  public void testCreateDatabase() {
    when(client.head("/testdb/")).thenReturn(
        HttpResponseStub.valueOf(404, "{\"error\":\"not_found\",\"reason\":\"no_db_file\"}"));
    when(client.put(anyString())).thenReturn(HttpResponseStub.valueOf(201, "{\"ok\": true}"));
    instance.createDatabase("testdb/");
    verify(client).put("/testdb/");
  }

  @Test
  public void testDatabaseWithSlashInPath() {
    when(client.head("/test_inv%2Fqaz/")).thenReturn(
        HttpResponseStub.valueOf(404, "{\"error\":\"not_found\",\"reason\":\"no_db_file\"}"));
    when(client.put(anyString())).thenReturn(HttpResponseStub.valueOf(201, "{\"ok\": true}"));
    instance.createDatabase("test_inv/qaz/");
    verify(client).put("/test_inv%2Fqaz/");
  }

  @Test
  public void shouldFailWhenDbExists() {
    when(client.get("/_all_dbs"))
        .thenReturn(HttpResponseStub.valueOf(200, "[\"somedatabase\", \"anotherdatabase\"]"));
    try {
      instance.createDatabase("somedatabase/");
      fail("RuntimeException expected");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testDeleteDatabase() {
    instance.deleteDatabase("somedatabase");
    verify(client).delete("/somedatabase/");
  }

  @Test
  public void testGetAllDatabases() {
    when(client.get("/_all_dbs"))
        .thenReturn(HttpResponseStub.valueOf(200, "[\"somedatabase\", \"anotherdatabase\"]"));
    List<String> all = instance.getAllDatabases();
    assertEquals(2, all.size());
    assertEquals("somedatabase", all.get(0));
    assertEquals("anotherdatabase", all.get(1));
  }

  @Test
  @SuppressFBWarnings("unchecked")
  public void testGetFullConfiguration() {
    when(client.get("/_node/_local/_config")).thenReturn(HttpResponseStub.valueOf(200, "{\"httpd\": {" +
        "\"bind_address\": \"0.0.0.0\",\"port\": \"5984\"}, \"ssl\": {\"port\": \"6984\"}}"));
    Map<String, Object> config = instance.getConfiguration(Map.class, null);
    assertEquals(2, config.keySet().size());
  }

  @Test
  @SuppressFBWarnings("unchecked")
  public void testGetConfigurationSection() {
    when(client.get("/_node/_local/_config/httpd")).thenReturn(HttpResponseStub.valueOf(200, "{\"httpd\": {" +
        "\"bind_address\": \"0.0.0.0\",\"port\": \"5984\"}}"));
    Map<String, Object> config = instance.getConfiguration(Map.class, null, "httpd");
    assertEquals(1, config.keySet().size());
  }

  @Test
  public void testGetConfigurationValue() {
    when(client.get("/_node/_local/_config/httpd/port")).thenReturn(HttpResponseStub.valueOf(200, "\"5984\""));
    String config = instance.getConfiguration(String.class, null, "httpd", "port");
    assertEquals("5984", config);
  }

  @Test
  public void testSetConfigurationValue() {
    when(client.put(eq("/_node/_local/_config/httpd/port"), anyString()))
        .thenReturn(HttpResponseStub.valueOf(200, "\"5984\""));
    String oldConfig = instance.setConfiguration(null, "httpd", "port", "5985");
    assertEquals("5984", oldConfig);
  }

  @Test
  public void testDeleteConfigurationValue() {
    when(client.delete("/_node/_local/_config/httpd/port"))
        .thenReturn(HttpResponseStub.valueOf(200, "\"5984\""));
    String oldConfig = instance.deleteConfiguration(null, "httpd", "port");
    assertEquals("5984", oldConfig);
  }

  @Test
  public void testDescribeCluster() {
    when(client.get("/_membership"))
        .thenReturn(HttpResponseStub.valueOf(200, "{\"all_nodes\": [\"nonode@nohost\"]}"));
    MembershipInfo mInfo = instance.describeCluster();
    assertEquals(1, mInfo.getAllNodes().size());
  }

}