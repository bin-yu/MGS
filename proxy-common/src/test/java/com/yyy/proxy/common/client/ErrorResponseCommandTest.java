package com.yyy.proxy.common.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import com.yyy.proxy.common.Command;

public class ErrorResponseCommandTest {
	@Test
	public void serializeAndDeserialize() throws IOException{
		ErrorResponseCommand cmd=new ErrorResponseCommand("AAA","exception happended");
		byte[] content=cmd.serialize();
		assertNotNull(content);
		assertTrue(content.length>0);
		System.out.println(new String(content,"UTF-8"));
		Command resultCmd=Command.deserialize(content);
		assertEquals(cmd,resultCmd);
	}
}
