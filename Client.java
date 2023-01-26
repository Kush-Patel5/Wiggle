import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

// this needs to be ported to JS
// it does not need to use an object
public class Client {
	private Socket clientSocket;
	private PrintWriter out;
	private InputStream in;
	private ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
	private long lastKeepAlive = System.currentTimeMillis();
	
	// opens a connection
	public void startConnection(String ip, int port) throws IOException {
		clientSocket = new Socket(ip, port);
		clientSocket.setKeepAlive(true);
		clientSocket.setTcpNoDelay(true);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = clientSocket.getInputStream();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				sendMessage("close");
			} catch (Throwable ignored) {
			}
		}));
		
		Client client = this;
		Thread listenerThread = new Thread(() -> {
			Scanner sc = new Scanner(System.in);
			while (true) {
				String str = sc.nextLine();
				if (str.equals("q")) {
					try {
						client.sendMessage("close");
					} catch (Throwable ignored) {
					}
				}
				// if (str.contains(":")) {
				// String[] split = str.split(":", 2);
				try {
					int clientId = 0;
					client.sendChat(MessageInfo.sending(str));
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
				// }
			}
		});
		listenerThread.start();
	}
	
	// sends a packet to the server
	public void sendMessage(String msg) throws IOException {
		out.println(msg);
	}
	
	ArrayList<MessageInfo> newMessages = new ArrayList<>();
	ArrayList<MessageInfo> pendingMessages = new ArrayList<>();
	
	public int countPending() {
		return pendingMessages.size();
	}
	
	public ArrayList<MessageInfo> getNewMessages() {
		return newMessages;
	}
	
	// sends a chat message to the server
	public void sendChat(MessageInfo msg) throws IOException {
		out.println("send:" + msg.message);
		pendingMessages.add(msg);
		synchronized (newMessages) {
			newMessages.add(msg);
		}
	}
	
	// reads and handles the messages sent from the server
	public void processBuffer() throws IOException {
		if (in.available() > 1) {
			while (in.available() > 1) {
				byte[] bytes = new byte[2];
				in.read(bytes);
				
				messageBuffer.write(bytes);
				String bufferString = new String(messageBuffer.toByteArray(), StandardCharsets.UTF_16);
				if (bufferString.contains("\n")) {
					for (String s : bufferString.split("\n")) {
						if (s.startsWith("post:")) {
							System.out.println("Other: " + s.substring("post:".length()));
							// add a message to the list of recieved messages
							MessageInfo inf = MessageInfo.incoming(s.substring("post:".length()));
							synchronized (newMessages) {
								newMessages.add(inf);
							}
						} else if (s.startsWith("posted:")) {
							// get just the contents of the message
							String contents = s.substring("posted:".length());
							System.out.println("You: " + contents);
							// update a pending message and remove it from the list of pending messages
							for (int element = pendingMessages.size() - 1; element >= 0; element--) {
								if (pendingMessages.get(element).message.equals(contents)) {
									pendingMessages.get(element).messageStatus = 1;
									pendingMessages.remove(element);
									break;
								}
							}
						} else {
							// System.out.println(s);
						}
					}
					messageBuffer.flush();
					messageBuffer.close();
					messageBuffer = new ByteArrayOutputStream();
				}
			}
		}
	}
	
	// sends a chat message to the server
	public void sendChat(String text) throws IOException {
		sendChat(MessageInfo.sending(text));
	}
	
	public void tick() throws IOException {
		if (lastKeepAlive < System.currentTimeMillis() - 8000) {
			sendMessage("keep_alive");
			lastKeepAlive = System.currentTimeMillis();
		}
		
		processBuffer();
		
		try {
			Thread.sleep(10);
		} catch (Throwable ignored) {
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client();
		client.startConnection("", 4352); //Put server ip address in the quote
		
		try {
			Thread.sleep(2000);
		} catch (Throwable ignored) {
		}
		
		MessageInfo inf = MessageInfo.sending("hello");
		client.sendChat(inf);
		
		while (true) {
			client.tick();
			System.out.println(inf.messageStatus);
		}
	}
}
