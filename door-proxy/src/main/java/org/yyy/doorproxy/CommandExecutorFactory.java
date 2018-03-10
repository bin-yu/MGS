package org.yyy.doorproxy;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yyy.proxy.common.server.DoorRequestCommand;

@Configuration
public class CommandExecutorFactory {
	@Bean
	public Function<String, CommandExecutor> executorFac() {
		return protocol -> getInstance(protocol);
	}

	public CommandExecutor getInstance(String protocol) throws RecoverableException {
		switch (protocol) {
		case DoorRequestCommand.PROTOCOL_TCP:
			return tcpCommandExecutor();
		default:
			throw new RecoverableException("Invalid protocol:" + protocol);
		}
	}

	@Bean
	public CommandExecutor tcpCommandExecutor() {
		return new TcpDoorCommandExecutor();
	}
}
