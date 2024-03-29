package netflow.protocol;

import java.util.Date;

public class DatagramFlowRecord {
	int[] srcaddr;
	int[] destaddr;
	long npkts;
	long noctets;
	int protocol;
	long firstTime;
	long lastTime;

	/**
	 * Description of DatagramFlowRecord(byte[] data, int offset)
	 * 
	 * Converts the byte array into an internal datagram format information is
	 * available at http://netflow.caligare.com/netflow_v5.htm
	 * 
	 * @param data
	 *            byte array of data for conversion
	 * @param offset
	 *            initial offset in the datagram
	 * 
	 *
	 */
	public DatagramFlowRecord(byte[] data, int offset) {

		setSrcAddress(data, offset);
		setDestAddress(data, offset + 4);
		setNumPackets(data, offset + 16);
		setNumOctets(data, offset + 20);
		setfirstTime(data, offset + 24);
		setLastTime(data, offset + 28);
		setProtocol(data, offset + 38);
	}

	private void setLastTime(byte[] data, int i) {
		lastTime = quadTolong(data, i);
	}

	private void setfirstTime(byte[] data, int i) {
		firstTime = quadTolong(data, i);
	}

	/**
	 * Description of getCSVData(int seq)
	 * 
	 * creates a writable string of prepended with a sequence number
	 * 
	 * @param seq
	 *            sequence number to be prepended to the comma delimited string
	 * @param basetime
	 *            base time in seconds since 1970 since the server started
	 *
	 */
	public String getCSVData(int seq, long basetime) {
		Date d1 = new Date();
		d1.setTime((basetime * 1000) + firstTime);
		String s = new String(seq + "," + makeIPAddr(srcaddr) + ","
				+ makeIPAddr(destaddr) + ",");
		s = s + npkts + "," + protocol + "," + d1.toString();
		d1.setTime((basetime * 1000) + lastTime);
		s = s + "," + d1.toString();
		return s;
	}

	private String makeIPAddr(int[] data) {
		return new String(data[0] + "." + data[1] + "." + data[2] + "."
				+ data[3]);
	}

	private void setProtocol(byte[] data, int i) {
		protocol = ConvertByte.byteToInt(data[i]);
	}

	private void setNumOctets(byte[] data, int i) {
		noctets = quadTolong(data, i);
	}

	private void setNumPackets(byte[] data, int i) {
		npkts = quadTolong(data, i);
	}

	private void setDestAddress(byte[] data, int offset) {
		destaddr = new int[4];
		destaddr[0] = (ConvertByte.byteToInt(data[offset]));
		destaddr[1] = (ConvertByte.byteToInt(data[offset + 1]));
		destaddr[2] = (ConvertByte.byteToInt(data[offset + 2]));
		destaddr[3] = (ConvertByte.byteToInt(data[offset + 3]));

	}

	private void setSrcAddress(byte[] data, int offset) {
		srcaddr = new int[4];
		srcaddr[0] = (ConvertByte.byteToInt(data[offset]));
		srcaddr[1] = (ConvertByte.byteToInt(data[offset + 1]));
		srcaddr[2] = (ConvertByte.byteToInt(data[offset + 2]));
		srcaddr[3] = (ConvertByte.byteToInt(data[offset + 3]));
	}

	private long quadTolong(byte[] data, int i) {
		byte vals[] = new byte[4];
		long val;
		vals[0] = data[i];
		vals[1] = data[i + 1];
		vals[2] = data[i + 2];
		vals[3] = data[i + 3];
		val = ConvertByte.byteToULong(vals);
		return val;
	}
}
