package com.yyy.proxy.common.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.yyy.proxy.common.Command;

public class RegisterCommandTest {
	@Test
	public void serializeAndDeserialize() throws IOException{
		RegisterCommand cmd=new RegisterCommand("AAA");
		byte[] content=cmd.serialize();
		assertNotNull(content);
		assertTrue(content.length>0);
		System.out.println(new String(content,"UTF-8"));
		Command resultCmd=Command.deserialize(content);
		assertEquals(cmd,resultCmd);
	}
}
