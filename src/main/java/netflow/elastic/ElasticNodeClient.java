package netflow.elastic;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Before;
import org.junit.Test;

public class ElasticNodeClient {

	private volatile Client client;

	@Before
	public void init() {

		if (client == null) {
			synchronized (ElasticNodeClient.class) {
				if (client == null) {

					try {
						client = new TransportClient()
								.addTransportAddress(new InetSocketTransportAddress(
										"192.168.1.202", 9300));
					} catch (ElasticsearchException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}

	}

	@Test
	public void testIndex() {

		IndexResponse response = null;

		try {
			response = client
					.prepareIndex("asdf2014", "asdf", "1")
					.setSource(
							jsonBuilder().startObject().field("age", 22)
									.field("sex", "male").endObject())
					.execute().actionGet();
		} catch (ElasticsearchException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		System.out.println("Index: " + response.getIndex());
		System.out.println("Type: " + response.getType());
		System.out.println("Id: " + response.getId());
		System.err.println("Version: " + response.getVersion());
		System.out.println("IsCreated: " + response.isCreated());

	}

	@Test
	public void testGet() {

		GetResponse response = client.prepareGet("asdf2014", "asdf", "1")
				.setOperationThreaded(false).execute().actionGet();
		if (response != null)
			System.out.println("Id: " + response.getId());

	}

	@Test
	public void testDelete() {

		DeleteResponse response = client.prepareDelete("asdf2014", "asdf", "1")
				.setOperationThreaded(false).execute().actionGet();
		if (response != null)
			System.out.println("Id: " + response.getId());

	}

	@Test
	public void testUpdate() {

		UpdateRequest updateRequest = new UpdateRequest("asdf2014", "asdf", "1")
				.script("ctx._source.sex=\"man\"");
		try {
			client.update(updateRequest).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Test
	public void testBulkProcess() {

		BulkProcessor bulkProcessor = BulkProcessor
				.builder(client, new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long executionId, BulkRequest request) {
						/**
						 * Why did those methods not be run?<br>
						 * Because we did not close the bulkProcessor to make it
						 * flush cache?<br>
						 * No, the reason is we do not wait those little seconds
						 * to close the bulk-processor when it was set
						 * FlushInterval after five second.
						 */
						System.out.println("BulkProcessor's beforeBulk.");
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, BulkResponse response) {
						// TODO:
						System.out.println("BulkProcessor's afterBulk.");
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, Throwable failure) {
						// TODO:
						System.out
								.println("BulkProcessor's afterBulk when it failed.");
					}
				}).setBulkActions(10000)
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
				.setFlushInterval(TimeValue.timeValueSeconds(5))
				.setConcurrentRequests(1).build();

		/**
		 * " ' " --> " \" "
		 */
		bulkProcessor.add(new IndexRequest("asdf2014", "asdf", "1")
				.source("{\"name\":\"asdf\"}"));
		bulkProcessor.add(new DeleteRequest("asdf2014", "asdf", "2"));

		try {
			bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		bulkProcessor.close();
	}
}
