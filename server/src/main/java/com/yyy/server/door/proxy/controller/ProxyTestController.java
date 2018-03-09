package com.yyy.server.door.proxy.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.proxy.common.Command;
import com.yyy.proxy.common.server.DoorRequestCommand;
import com.yyy.server.door.proxy.DoorCommandException;
import com.yyy.server.door.proxy.DoorCommandProxy;

@RestController
@RequestMapping({ "/doorproxy" })
public class ProxyTestController {
	@Autowired
	private DoorCommandProxy proxy;

	@PostMapping("/command")
	public Command execCommand(String ip, int port, String secret, String command)
			throws UnsupportedEncodingException, DoorCommandException {
		return proxy.sendCommand(secret, new DoorRequestCommand(ip, port, secret, command.getBytes("UTF-8")));
	}
}
