package org.apache.activemq.artemis.jms.demo;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Sender_Anycast {

	private static final int DEFAULT_COUNT = 10;
	private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

	static String queueName = "FDXT.T1";
	static String userId = "amq";
	static String userPwd = "amq";
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
			envVal.put("queue.myQueueLookup", queueName);
			Context context = new InitialContext(envVal);

			ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
			Destination queue = (Destination) context.lookup("myQueueLookup");

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
