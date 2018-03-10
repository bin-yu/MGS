package com.yyy.proxy.common.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import com.yyy.proxy.common.Command;

public class DoorRequestCommandTest {
	@Test
	public void serializeAndDeserialize() throws IOException {
		String hexCmd = "0FF0";
		DoorRequestCommand cmd = new DoorRequestCommand("AAA", "192.168.1.1", 8000, "TCP",
				DatatypeConverter.parseHexBinary(hexCmd));
		byte[] content = cmd.serialize();
		assertNotNull(content);
		assertTrue(content.length > 0);
		System.out.println(new String(content, "UTF-8"));
		Command resultCmd = Command.deserialize(content);
		assertEquals(cmd, resultCmd);
	}
}
