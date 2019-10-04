package org.ektorp.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.List;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageResponseHandler<T> extends StdResponseHandler<Page<T>> {

  private final QueryResultParser<T> parser;
  private final PageRequest pageRequest;
  private final static Logger LOG = LoggerFactory.getLogger(PageResponseHandler.class);

  public PageResponseHandler(PageRequest pr, Class<T> docType, ObjectMapper om) {
    Assert.notNull(om, "ObjectMapper may not be null");
    Assert.notNull(docType, "docType may not be null");
    parser = new QueryResultParser<T>(docType, om);
    this.pageRequest = pr;
  }

  public PageResponseHandler(PageRequest pr, Class<T> docType, ObjectMapper om,
      boolean ignoreNotFound) {
    Assert.notNull(om, "ObjectMapper may not be null");
    Assert.notNull(docType, "docType may not be null");
    parser = new QueryResultParser<T>(docType, om);
    parser.setIgnoreNotFound(ignoreNotFound);
    this.pageRequest = pr;
  }

  @Override
  @SuppressFBWarnings(value = "DB_DUPLICATE_BRANCHES")
  public Page<T> success(HttpResponse hr) throws Exception {
    parser.parseResult(hr.getContent());
    List<T> rows = parser.getRows();

    int rowsSize = rows.size();
    LOG.debug("got {} rows", rowsSize);

    String nextId = null;
    JsonNode nextKey = null;
    String firstId = null;
    JsonNode firstKey = null;

    if (pageRequest.isBack()) {
      Collections.reverse(rows);
      //since rows are now reversed
      nextId = parser.getFirstId();
      nextKey = parser.getFirstKey();
      firstId = parser.getLastId();
      firstKey = parser.getLastKey();
    } else {
      nextId = parser.getLastId();
      nextKey = parser.getLastKey();
      firstId = parser.getFirstId();
      firstKey = parser.getFirstKey();
    }
    PageRequest.Builder nextRequestBuilder = pageRequest.nextRequest(nextKey, nextId).back(false);
    PageRequest.Builder prevRequestBuilder = pageRequest.nextRequest(firstKey, firstId).back(true);
    int currentPage = pageRequest.getPageNo();

    PageRequest nextRequest = nextRequestBuilder.page(currentPage + 1).build();
    PageRequest previousRequest =
        currentPage == 1 ? PageRequest.firstPage(pageRequest.getPageSize()) :
            prevRequestBuilder.page(currentPage - 1).build();

    boolean hasMore = rowsSize == pageRequest.getPageSize() + 1;
    if (hasMore) {
      rows.remove(rows.size() - 1);
    } else if (!pageRequest.isBack()) {
      nextRequest = null;
    }
    if (currentPage == 0) {
      previousRequest = null;
    }
    return new Page<T>(rows, parser.getTotalRows(), pageRequest.getPageSize(), previousRequest,
        nextRequest);
  }
}