package org.apache.activemq.artemis.jms.demo;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Receiver_Anycast {
	
	private static final int DEFAULT_COUNT = 5;
	static String addressName = "FDXT.T1";
	static String userId = "amq";
	static String userPwd = "amq";
	static String brokerEndpoint = "amqp://localhost:61616";

	public static void main(String[] args) throws Exception {

		int count = DEFAULT_COUNT;
		try {
			// The configuration for the Qpid InitialContextFactory has been supplied below
			// using HashTable.
			// we can also supply using jndi.properties file in the classpath, which results
			// in it being picked
			// up automatically by the InitialContext constructor.
			Hashtable<Object, Object> envVal = new Hashtable<Object, Object>();
			envVal.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
			envVal.put("connectionfactory.myFactoryLookup", brokerEndpoint);
			envVal.put("queue.myAddressLookup", addressName);
			Context context = new InitialContext(envVal);

			ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
			Destination queue = (Destination) context.lookup("myAddressLookup");

			Connection connection = factory.createConnection(System.getProperty("userId"),
					System.getProperty("userPwd"));
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageConsumer messageConsumer = session.createConsumer(queue);

			long start = System.currentTimeMillis();

			int actualCount = 0;
			boolean deductTimeout = false;
			int timeout = 1000;
			for (int i = 1; i <= count; i++, actualCount++) {
				Message message = messageConsumer.receive(timeout);
				if (message == null) {
					System.out.println("Message " + i + " not received within timeout, stopping.");
					deductTimeout = true;
					break;
				}
			System.out.println("Got message " + i);
				
			}

			long finish = System.currentTimeMillis();
			long taken = finish - start;
			if (deductTimeout) {
				taken -= timeout;
			}
			System.out.println("Received " + actualCount + " messages in " + taken + "ms");

			connection.close();
		} catch (Exception exp) {
			System.out.println("Caught exception, exiting.");
			exp.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
