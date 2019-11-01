package org.apache.activemq.artemis.jms.demo;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Sender_Wildcard {
	
	private static final int DEFAULT_COUNT = 5;
	private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

	// provide Multicast Address
	//when you send the Message to below address, same message ill be broadcast to FEDEX.SOURCE.REQ.CONSUMER.2 and FEDEX.SOURCE.HASH
	static String addressName = "FEDEX.SOURCE.REQ.CONSUMER2";
	
	//when you send the Message to below address, same message ill be broadcast to FEDEX.SOURCE.CONSUMER.3, FEDEX.SOURCE.HASH and FEDEX.SOURCE.STAR
	//static String addressName = "FEDEX.SOURCE.CONSUMER3";

	// provide AMQ user userName
	static String userId = "amq";

	// provide AMQ user password
	static String userPwd = "amq";

	// provide AMQ endpoint
	static String brokerEndpoint = "amqp://localhost:61616";

	public static void main(String[] args) throws Exception {

		int count = DEFAULT_COUNT;

		try {

			// The configuration for the Qpid InitialContextFactory has been supplied in
			// a jndi.properties file in the classpath, which results in it being picked
			// up automatically by the InitialContext constructor.
			// Context context = new InitialContext();

			Hashtable<Object, Object> envVal = new Hashtable<Object, Object>();
			envVal.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
			envVal.put("connectionfactory.myFactoryLookup", brokerEndpoint);
			envVal.put("topic.myAddressLookup", addressName);
			Context context = new InitialContext(envVal);

			ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
			Topic queue = (Topic) context.lookup("myAddressLookup");

			Connection connection = factory.createConnection(System.getProperty("userId"),
					System.getProperty("userPwd"));
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer messageProducer = session.createProducer(queue);

			long start = System.currentTimeMillis();
			for (int i = 1; i <= count; i++) {
				TextMessage message = session.createTextMessage("Hello World " + i);
				messageProducer.send(message, DELIVERY_MODE, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

				System.out.println("Sent message " + i);

			}

			long finish = System.currentTimeMillis();
			long taken = finish - start;
			System.out.println("Sent " + count + " messages in " + taken + "ms");

			connection.close();
			
		} catch (Exception exp) {
			System.out.println("Caught exception, exiting.");
			exp.printStackTrace(System.out);
			System.exit(1);
		}
	}



}
