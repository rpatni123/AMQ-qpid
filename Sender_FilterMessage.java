package org.apache.activemq.artemis.jms.demo;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Sender_FilterMessage {

	
	  static String addressName = "FILTER.TESTING";
	  static String userId = "amq";
	  static String userPwd = "amq";
	  static String brokerEndPoint = "amqp://localhost:61616"; 

	public static void main(String[] args) throws Exception {
		
	      
	      Hashtable<Object, Object> envVal = new Hashtable<Object, Object>();
		  envVal.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
		  envVal.put("connectionfactory.myFactoryLookup", brokerEndPoint);
		  envVal.put("topic.myAddressLookup", addressName);
		  Context context = new InitialContext(envVal);
	      
	      Connection connection = null;
	      try {
	         // Step 1. Create an initial context to perform the JNDI lookup.
	         //initialContext = new InitialContext();

	         // Step 2. look-up the JMS queue object from JNDI, this is the queue that has filter configured with it.
	         Queue queue = (Queue) context.lookup("queue/exampleQueue");

	         // Step 3. look-up the JMS connection factory object from JNDI
	         ConnectionFactory cf = (ConnectionFactory) context.lookup("my_topicConnectionFactory");

	         // Step 4. Create a JMS Connection
	         connection = cf.createConnection();

	         // Step 5. Start the connection
	         connection.start();

	         // Step 6. Create a JMS Session
	         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	         
	         // Step 7. Create a JMS Message Producer
	         MessageProducer producer = session.createProducer(queue);

	         // Step 9. Create five messages with different 'News' properties. Only the messages which has string set to 'bbc' will be allowed.
	         TextMessage bbcMessage1 = session.createTextMessage("Hello world 1");
	         bbcMessage1.setStringProperty("NEWS", "bbc");
	         TextMessage foxMessage2 = session.createTextMessage("Hello world 2");
	         foxMessage2.setStringProperty("NEWS", "fox");
	         TextMessage nbcMessage = session.createTextMessage("Hello world 3");
	         nbcMessage.setStringProperty("NEWS", "nbc");
	         TextMessage abcMessage = session.createTextMessage("Hello world 4");
	         abcMessage.setStringProperty("NEWS", "abc");
	         
	         // Step 10. Send the Messages
	         producer.send(bbcMessage1);
	         System.out.println("Message sent: " + bbcMessage1.getText());
	         producer.send(foxMessage2);
	         System.out.println("Message sent: " + foxMessage2.getText());
	         producer.send(nbcMessage);
	         System.out.println("Message sent: " + nbcMessage.getText());
	         producer.send(abcMessage);
	         System.out.println("Message sent: " + abcMessage.getText());
	         
	         // Step 11. Waiting for the message listener to check the received messages.
	         Thread.sleep(5000);

	        
	      } finally {
	         
	    	 // Step 12. Be sure to close our JMS resources! 
	         if (context != null) {
	        	 context.close();
	         }
	         if (connection != null) {
	            connection.close();
	         }
	      }
	   }
}
