package netflow.realtime;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import netflow.protocol.Datagram;

public class ReceiveUDPData {

	private static DatagramSocket serverSocket;
	private static DatagramPacket receivePacket;

	private static byte[] receiveData;

	private static void init(int port) {

		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		receiveData = new byte[2048];

		try {
			System.out.printf("Listening on udp:%s:%d%n", InetAddress
					.getLocalHost().getHostAddress(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		receivePacket = new DatagramPacket(receiveData, receiveData.length);

	}

	public static void main(String[] args) {
		int port = args.length == 0 ? 9999 : Integer.parseInt(args[0]);
		run(port);
	}

	public static void run(int port) {

		init(port);

		Datagram datagram = null;
		boolean isBase = false;
		try {
			while (true) {
				serverSocket.receive(receivePacket);

				datagram = new Datagram(receiveData);

				if (!isBase) {
					datagram.showBaseInfos();
					isBase = true;
				}

				datagram.showData();

				// String sentence = new String(receivePacket.getData(), 0,
				// receivePacket.getLength());

				// System.out.println("RECEIVED: " + sentence);

				// send();
			}

		} catch (IOException e) {

			e.printStackTrace();
			throw new RuntimeException(e);

		} finally {

			if (serverSocket != null)
				serverSocket.close();

		}
	}

	public static void send() {

		// now send acknowledgement packet back to sender
		InetAddress IPAddress = receivePacket.getAddress();
		String sendString = "asdf";
		byte[] sendData;
		try {
			sendData = sendString.getBytes("UTF-8");
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, receivePacket.getPort());
			serverSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
