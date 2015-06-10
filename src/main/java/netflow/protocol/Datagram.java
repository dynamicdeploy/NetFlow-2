package netflow.protocol;

import java.util.ArrayList;

public class Datagram {
	private DatagramHeader head;
	private ArrayList<DatagramFlowRecord> records;

	/**
	 * Description of Datagram(byte[] data)
	 * 
	 * Constructor to build a datagram header and set of flow records from a
	 * byte array
	 * 
	 * @param data
	 *            data to be parsed
	 * 
	 *
	 */
	public Datagram(byte[] data) {
		head = new DatagramHeader(data);
		int offset = 24; // 24 bytes per header record
		records = new ArrayList<DatagramFlowRecord>();
		for (int i = 0; i < head.getFlowcount(); i++) {
			records.add(new DatagramFlowRecord(data, offset));
			offset += 48; // 48 bytes per flow record
		}
	}

	/**
	 * Description of writeCSV(string fname)
	 * 
	 * writes the comma delimited data based on the internal representation of
	 * the datagram
	 * 
	 * @param fname
	 *            full path to the file to be written to
	 *
	 */
	public void showData() {

		for (DatagramFlowRecord record : records) {

			System.out.println(record.getCSVData(head.getSequencenum(),
					head.getSysBaseTime()));

		}
	}

	public void showBaseInfos() {
		System.out.println(" 0 to 1 Version:\t " + head.getVersion());
		System.out.println(" 2 to 3 Count:\t " + head.getFlowcount());
		System.out.println(" 4 to 7 System Uptime:\t " + head.getSysuptime());
		System.out.println(" 8 to 11 UNIX Seconds:\t " + head.getUnixtime());
		// System.out.println(" 12 to 15 UNIX NanoSeconds:\t ");
		System.out.println(" 16 to 19 Flow Sequence Number:\t "
				+ head.getSequencenum());
		// System.out.println(" 20 Engine Type:\t ");
		// System.out.println(" 21 Engine ID:\t ");
		System.out.println(" 22 to 23 Reserved:\t " + head.getSampleinterval());
	}

}