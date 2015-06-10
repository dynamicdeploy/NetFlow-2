package netflow.protocol;

/**
 * DatagramHeader - Parses and stores a single datagram header
 *
 * @author David Turvey
 * @version 1.0 6-10-12
 */

public class DatagramHeader {
	private int version;
	private int flowcount;
	private long sysuptime;
	private long unixtime;
	private int sampleinterval;
	private int sequencenum;

	/**
	 * Description of DatagramFlowRecord(byte[] data)
	 * 
	 * Converts the byte array into an internal datagram header
	 * 
	 * @param data
	 *            byte array of data for conversion
	 *
	 */
	public DatagramHeader(byte[] data) {
		setVersion(ConvertByte.byteToInt(data[0]));
		setFlowCount(ConvertByte.byteToInt(data[3]));
		setSysupTime(data);
		setUnixTime(data);
		setSequenceNum(data);
		setSampleInterval(data[22], data[23]);
	}

	private void setSequenceNum(byte[] data) {
		sequencenum = 0;
		sequencenum = sequencenum | (ConvertByte.byteToInt(data[16]));
		sequencenum = sequencenum << 8;
		sequencenum = sequencenum | (ConvertByte.byteToInt(data[17]));
		sequencenum = sequencenum << 8;
		sequencenum = sequencenum | (ConvertByte.byteToInt(data[18]));
		sequencenum = sequencenum << 8;
		sequencenum = sequencenum | (ConvertByte.byteToInt(data[19]));
		sequencenum = sequencenum << 8;
	}

	private void setSampleInterval(byte b, byte c) {
		sampleinterval = 0;
		sampleinterval = sampleinterval | (ConvertByte.byteToInt(b));
		sampleinterval = sampleinterval << 8;
		sampleinterval = sampleinterval | (ConvertByte.byteToInt(c));

	}

	private void setSysupTime(byte[] data) {
		byte sutime[] = new byte[4];
		for (int i = 0; i < 4; i++) {
			sutime[i] = data[4 + i];
		}
		sysuptime = ConvertByte.byteToULong(sutime);
	}

	/**
	 * Description of setUnixTime()
	 * 
	 * Parser for Unix time in seconds since 1970 Will need to subtract sys
	 * uptime as a base time for all time measurements
	 * 
	 *
	 */
	private void setUnixTime(byte[] data) {
		byte utime[] = new byte[4];
		for (int i = 0; i < 4; i++) {
			utime[i] = data[8 + i];
		}
		unixtime = ConvertByte.byteToULong(utime);
		// Date d = new Date(unixtime * 1000);
	}

	private void setFlowCount(int i) {
		this.flowcount = i;
	}

	private void setVersion(int i) {
		this.version = i;
	}

	/**
	 * Description of getSysBaseTime()
	 * 
	 * Note returns the base time to the nearest second All other readings are
	 * in milliseconds
	 * 
	 * @return long System start time in seconds since 1970
	 *
	 */
	public long getSysBaseTime() {
		return unixtime - (sysuptime / 1000);
	}

	public int getFlowcount() {
		return flowcount;
	}

	public long getSysuptime() {
		return unixtime - (sysuptime / 1000);
	}

	public long getUnixtime() {
		return unixtime;
	}

	public int getSampleinterval() {
		return sampleinterval;
	}

	public int getSequencenum() {
		return sequencenum;
	}

	public int getVersion() {
		return version;
	}

}