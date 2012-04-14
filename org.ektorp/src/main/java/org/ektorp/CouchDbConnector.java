package org.ektorp;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;
import org.ektorp.http.HttpClient;

/**
 * Primary interface for working with Objects mapped as documents in CouchDb.
 * 
 * The Id and revision of mapped Objects must be accessible by org.ektorp.util.Documents class.
 * 
 * @author henrik lundgren
 * 
 */
public interface CouchDbConnector {
    /**
     * 
     * @param id
     * @param the
     *            object to store in the database
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    void create(String id, Object o);

    /**
     * Creates the Object as a document in the database. If the id is not set it will be generated by the database.
     * 
     * The Object's revision field will be updated through the setRevision(String s) method.
     * 
     * @param o
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    void create(Object o);

    /**
     * Updates the document.
     * 
     * The Object's revision field will be updated through the setRevision(String s) method.
     * 
     * @param o
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    void update(Object o);

    /**
     * Deletes the Object in the database.
     * 
     * @param o
     * @return the revision of the deleted document
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    String delete(Object o);

    /**
     * Deletes the document in the database.
     * 
     * @param id
     * @param revision
     * @return the revision of the deleted document.
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    String delete(String id, String revision);

    /**
     * Permanently removes the references to deleted documents from the database.
     * 
     * @param revisionsToPurge
     *            document IDs &amp; revisions to be purged
     * @return contains the purge sequence number, and a list of the document IDs and revisions successfully purged.
     */
    PurgeResult purge(Map<String, List<String>> revisionsToPurge);

    /**
     * 
     * @param <T>
     * @param c
     *            the target class to map to.
     * @param id
     *            the id of the document in the database.
     * @return the document mapped as the specified class.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    <T> T get(Class<T> c, String id);

    /**
     * 
     * @param c
     *            the target class to map to.
     * @param id
     *            the id of the document in the database.
     * @param options
     * @return the document mapped as the specified class.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    <T> T get(Class<T> c, String id, Options options);

    /**
     * Same as get(Class<T> c, String id) with the difference that null is return if the document was not found.
     * 
     * @param c
     * @param id
     * @return null if the document was not found.
     */
    <T> T find(Class<T> c, String id);

    /**
     * Same as get(Class<T> c, String id, Options options) with the difference that null is return if the document was
     * not found.
     * 
     * @param c
     * @param id
     * @param options
     * @return null if the document was not found.
     */
    <T> T find(Class<T> c, String id, Options options);

    /**
     * 
     * @param <T>
     * @param c
     *            the target class to map to.
     * @param id
     *            the id of the document in the database.
     * @param rev
     *            of the object.
     * @return the document mapped as the specified class.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     * @deprecated use get(Class<T> c, String id, Options options)
     */
    @Deprecated
    <T> T get(Class<T> c, String id, String rev);

    /**
     * Will load the document with any conflicts included.
     * 
     * @param <T>
     * @param c
     *            the target class to map to.
     * @param id
     *            the id of the document in the database.
     * @return the document mapped as the specified class.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     * @deprecated use get(Class<T> c, String id, Options options)
     */
    @Deprecated
    <T> T getWithConflicts(Class<T> c, String id);

    /**
     * Check if the database contains a document.
     * 
     * @param id
     * @return true if a document with the id exists in the database
     */
    boolean contains(String id);

    /**
     * Please note that the stream has to be closed after usage, otherwise http connection leaks will occur and the
     * system will eventually hang due to connection starvation.
     * 
     * @param id
     * @return the document as raw json in an InputStream, don't forget to close the stream when finished.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    InputStream getAsStream(String id);

    /**
     * Please note that the stream has to be closed after usage, otherwise http connection leaks will occur and the
     * system will eventually hang due to connection starvation.
     * 
     * @param id
     * @param rev
     * @return the document as raw json in an InputStream, don't forget to close the stream when finished.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     * @deprecated use getAsStream(String id, Options options)
     */
    @Deprecated
    InputStream getAsStream(String id, String rev);

    /**
     * Please note that the stream has to be closed after usage, otherwise http connection leaks will occur and the
     * system will eventually hang due to connection starvation.
     * 
     * @param id
     * @param options
     * @return the document as raw json in an InputStream, don't forget to close the stream when finished.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    InputStream getAsStream(String id, Options options);

    /**
     * 
     * @param id
     * @return
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    List<Revision> getRevisions(String id);

    /**
     * Reads an attachment from the database.
     * 
     * Please note that the stream has to be closed after usage, otherwise http connection leaks will occur and the
     * system will eventually hang due to connection starvation.
     * 
     * @param id
     * @param attachmentId
     * @return the attachment in the form of an AttachmentInputStream.
     * @throws DocumentNotFoundException
     *             if the document was not found.
     */
    AttachmentInputStream getAttachment(String id, String attachmentId);

    AttachmentInputStream getAttachment(String id, String attachmentId, String revision);
    /**
     * Creates both the document and the attachment
     * 
     * @param docId
     * @param a
     *            - the data to be saved as an attachment
     * @return revision of the created attachment document
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    String createAttachment(String docId, AttachmentInputStream data);

    /**
     * Adds an attachment to the specified document id.
     * 
     * @param docId
     * @param revision
     * @param a
     *            - the data to be saved as an attachment
     * @return the new revision of the document
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    String createAttachment(String docId, String revision, AttachmentInputStream data);

    /**
     * 
     * @param docId
     * @param revision
     * @param attachmentId
     * @return the new revision of the document
     * @throws UpdateConflictException
     *             if there was an update conflict.
     */
    String deleteAttachment(String docId, String revision, String attachmentId);

    /**
     * @return all document ids in the database including design document ids.
     */
    List<String> getAllDocIds();

    /**
     * This method requires the view result values to be document ids or documents :
     * <ul>
     * <li>If the value is a document id, then the document is fetched from couchDB.</li>
     * <li>If the value is a document, then it is used directly for unmarshalling.</li>
     * </ul>
     * 
     * {"_id":"_design/ExampleDoc", "views":{ "all": {"map": "function(doc) { emit(null, doc._id);}"}, "by_name":
     * {"map": "function(doc) { emit(doc.name, doc._id);}"} // emit doc id "by_author": {"map":
     * "function(doc) { emit(doc.author, doc);}"} // emit doc } }
     * 
     * @param <T>
     * @param query
     * @param type
     *            the type to map the result to
     * @return the view result mapped as the specified class.
     */
    <T> List<T> queryView(ViewQuery query, Class<T> type);

    /**
     * Provides paged view results. Implementation based on the recipe described in the book
     * "CouchDB The Definitive Guide" http://guide.couchdb.org/editions/1/en/recipes.html#pagination
     * 
     * This method has the same requirements for the view as the method queryView(ViewQuery query, Class<T> type).
     * 
     * @param query
     * @param pr
     * @param type
     * @return
     */
    <T> Page<T> queryForPage(ViewQuery query, PageRequest pr, Class<T> type);

    /**
     * 
     * @param query
     * @return
     */
    ViewResult queryView(ViewQuery query);

    /**
     * Please note that the StreamingViewResult need to be closed after usage.
     * 
     * @param query
     * @return the view result as a iterable stream.
     */
    StreamingViewResult queryForStreamingView(ViewQuery query);

    /**
     * 
     * @param query
     * @return the view result as a raw InputStream.
     */
    InputStream queryForStream(ViewQuery query);

    /**
     * Creates a database on the configured path if it does not exists.
     */
    void createDatabaseIfNotExists();

    /**
     * @return name
     */
    String getDatabaseName();

    /**
     * 
     * @return
     */
    String path();

    /**
     * Convenience method for accessing the underlying HttpClient. Preferably used wrapped in a
     * org.ektorp.http.RestTemplate.
     * 
     * @return
     */
    HttpClient getConnection();

    /**
     * Provides meta information about this database.
     * 
     * @return
     */
    DbInfo getDbInfo();

    /**
     * Obtains information about a given design document, including the index, index size and current status of the
     * design document and associated index information.
     * 
     * @param designDocId
     * @return
     */
    DesignDocInfo getDesignDocInfo(String designDocId);

    /**
     * Compaction compresses the database file by removing unused sections created during updates. This call is
     * non-blocking, a compaction background task will be created on the CouchDB instance.
     */
    void compact();

    /**
     * This compacts the view index from the current version of the design document. This call is non-blocking, a
     * compaction background task will be created on the CouchDB instance.
     * 
     * @param designDocumentId
     */
    void compactViews(String designDocumentId);

    /**
     * View indexes on disk are named after their MD5 hash of the view definition. When you change a view, old indexes
     * remain on disk. To clean up all outdated view indexes (files named after the MD5 representation of views, that
     * does not exist anymore) you can trigger a view cleanup
     */
    void cleanupViews();

    /**
     * Revision limit defines a upper bound of document revisions which CouchDB keeps track of
     * 
     * @return
     */
    int getRevisionLimit();

    void setRevisionLimit(int limit);

    /**
     * Replicate the content in the source database into this database.
     * 
     * @param source
     *            database
     * @return ReplicationStatus
     */
    ReplicationStatus replicateFrom(String source);

    /**
     * Replicate the content in the source database into this database. Replication is restricted to the specified
     * document ids.
     * 
     * @param source
     *            database
     * @param docIds
     * @return ReplicationStatus
     */
    ReplicationStatus replicateFrom(String source, Collection<String> docIds);

    /**
     * Replicate the content in this database into the specified target database. The target must exist.
     * 
     * @param target
     *            database
     * @return ReplicationStatus
     */
    ReplicationStatus replicateTo(String target);

    /**
     * Replicate the content in this database into the specified target database. Replication is restricted to the
     * specified document ids. The target must exist.
     * 
     * @param target
     *            database
     * @param docIds
     * @return ReplicationStatus
     */
    ReplicationStatus replicateTo(String target, Collection<String> docIds);

    /**
     * Add the object to the bulk buffer attached to the executing thread. A subsequent call to either flushBulkBuffer
     * or clearBulkBuffer is expected.
     * 
     * @param o
     */
    void addToBulkBuffer(Object o);

    /**
     * Sends the bulk buffer attached the the executing thread to the database (through a executeBulk call). The bulk
     * buffer will be cleared when this method is finished.
     */
    List<DocumentOperationResult> flushBulkBuffer();

    /**
     * Clears the bulk buffer attached the the executing thread.
     */
    void clearBulkBuffer();
     /**
      * Creates, updates or deletes all objects in the supplied collection.
      *
      * If the json has no revision set, it will be created, otherwise it will be updated.
      * If the json document contains a "_deleted"=true field it will be deleted.
      *
      * Some documents may successfully be saved and some may not.
      * The response will tell the application which documents were saved or not. In the case of a power failure, when the database restarts some may have been saved and some not.
      * 
      * @param an json array with documents  ex [{"_id":"1", "name": "hello world" }, "_id":"2", "name": "hello world 2"}]
      * @return The list will only contain entries for documents that has any kind of error code returned from CouchDB. i.e. the list will be empty if everything was completed successfully.
      */
     List<DocumentOperationResult> executeBulk(InputStream inputStream);

     /**
      * Creates, updates or deletes all objects in the supplied collection.
      * In the case of a power failure, when the database restarts either all the changes will have been saved or none of them.
      * However, it does not do conflict checking, so the documents will be committed even if this creates conflicts.
      * 
      * @param an json array with documents
      * @return The list will only contain entries for documents that has any kind of error code returned from CouchDB. i.e. the list will be empty if everything was completed successfully.
      */
     List<DocumentOperationResult> executeAllOrNothing(InputStream inputStream);

    /**
     * Creates, updates or deletes all objects in the supplied collection.
     * 
     * If the object has no revision set, it will be created, otherwise it will be updated. If the object's serialized
     * json document contains a "_deleted"=true field it will be deleted.
     * 
     * org.ektorp.BulkDeleteDocument.of(someObject) is the easiest way to create a delete doc for an instance.
     * 
     * Some documents may successfully be saved and some may not. The response will tell the application which documents
     * were saved or not. In the case of a power failure, when the database restarts some may have been saved and some
     * not.
     * 
     * @param objects
     *            , all objects will have their id and revision set.
     * @return The list will only contain entries for documents that has any kind of error code returned from CouchDB.
     *         i.e. the list will be empty if everything was completed successfully.
     */
    List<DocumentOperationResult> executeBulk(Collection<?> objects);

    /**
     * Creates, updates or deletes all objects in the supplied collection. In the case of a power failure, when the
     * database restarts either all the changes will have been saved or none of them. However, it does not do conflict
     * checking, so the documents will be committed even if this creates conflicts.
     * 
     * @param objects
     *            , all objects will have their id and revision set.
     * @return The list will only contain entries for documents that has any kind of error code returned from CouchDB.
     *         i.e. the list will be empty if everything was completed successfully.
     */
    List<DocumentOperationResult> executeAllOrNothing(Collection<?> objects);

    /**
     * Queries the database for changes. This is a one-off operation. To listen to changes continuously @see
     * changesFeed(ChangesCommand cmd).
     * 
     * @param cmd
     * @return
     */
    List<DocumentChange> changes(ChangesCommand cmd);
    
    /**
     * Queries the database for changes. this operation gives you the result as a iterable stream of documentchange objects, 
     * the stream should be closed when finished
     * 
     * @param cmd
     * @return
     */
    StreamingChangesResult changesAsStream(ChangesCommand cmd);

    /**
     * Sets up a continuous changes feed. The current update sequence in the DB will be used if ChangesCommand does not
     * specify the since parameter. A heartbeat interval of 10 seconds will be used if ChangesCommand does not specify
     * the heartbeat parameter.
     * 
     * @param cmd
     * @return a running changes feed that buffers incoming changes in a unbounded queue (will grow until
     *         OutOfMemoryException if not polled).
     */
    ChangesFeed changesFeed(ChangesCommand cmd);

    /**
     * 
     * @param designDoc
     * @param function
     * @param docId
     * @return
     */
    String callUpdateHandler(String designDocID, String function, String docId);

    /**
     * 
     * @param designDoc
     * @param function
     * @param docId
     * @param params
     * @return
     */
    String callUpdateHandler(String designDocID, String function, String docId, Map<String, String> params);

    <T> T callUpdateHandler(final UpdateHandlerRequest req, final Class<T> c);

    String callUpdateHandler(final UpdateHandlerRequest req);

    /**
     * Commits any recent changes to the specified database to disk.
     */
    void ensureFullCommit();

	/**
	 * Sends a document to the Couch server as a MIME multipart/related message.
	 * @param id the document ID
	 * @param stream an InputStream of the multipart message containing
	 *                  the document and any attachments
	 * @param boundary the boundary of the multipart/related message parts
	 * @param length the length of the MIME multipart message stream
	 * @param options options to pass to the Couch request
	 */
	void updateMultipart(String id, InputStream stream, String boundary, long length, Options options);

	/**
	 * Sends a document to the Couch server as a MIME multipart/related message without
	 * a boundary.
	 * @param id the document ID
	 * @param stream an InputStream of the multipart message containing
	 *                  the document and any attachments
	 * @param length the length of the MIME multipart message stream
	 * @param options options to pass to the Couch request
	 */
	void updateMultipart(String id, InputStream stream, long length, Options options);

}