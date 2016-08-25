package com.phoenixkahlo.networking.test;

import java.io.IOException;

import com.phoenixkahlo.networking.CallBroadcaster;
import com.phoenixkahlo.networking.CallReceiver;
import com.phoenixkahlo.networking.TypedConsumer;
import com.phoenixkahlo.utils.AttempterThread;
import com.phoenixkahlo.utils.StreamPair;

public class CallSerializerTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		StreamPair pair = new StreamPair();
		CallBroadcaster broadcaster = new CallBroadcaster(pair.out);
		CallReceiver receiver = new CallReceiver(pair.in);
		broadcaster.bind(42, "println");
		receiver.bind(42, new TypedConsumer() {

			@Override
			public void invoke(Object... args) throws IllegalArgumentException {
				checkTypes(args);
				System.out.println("> " + (String) args[0]);
			}

			@Override
			public Class<?>[] getArgTypes() {
				return new Class<?>[] { String.class };
			}

		});
		AttempterThread receiverThread = new AttempterThread(receiver::receive, Exception::printStackTrace);
		receiverThread.start();
		broadcaster.broadcast("println", "Hello world!");
		Thread.sleep(1000);
		broadcaster.broadcast("println", "Testing, testing, 1 2 3");
		Thread.sleep(1000);
		System.out.println("Killing " + receiverThread.getId());
		receiverThread.interrupt();
	}

}
