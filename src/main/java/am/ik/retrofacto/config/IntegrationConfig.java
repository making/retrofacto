package am.ik.retrofacto.config;

import java.sql.DriverManager;

import javax.sql.DataSource;

import am.ik.retrofacto.retro.sse.SseEmitterManager;
import org.postgresql.jdbc.PgConnection;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.jdbc.channel.PostgresChannelMessageTableSubscriber;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.PostgresChannelMessageStoreQueryProvider;
import org.springframework.messaging.MessageChannel;

@Configuration(proxyBeanMethods = false)
public class IntegrationConfig {

	@Bean
	public JdbcChannelMessageStore messageStore(DataSource dataSource) {
		JdbcChannelMessageStore messageStore = new JdbcChannelMessageStore(dataSource);
		messageStore.setChannelMessageStoreQueryProvider(new PostgresChannelMessageStoreQueryProvider());
		return messageStore;
	}

	@Bean
	public PostgresChannelMessageTableSubscriber subscriber(DataSourceProperties properties) {
		return new PostgresChannelMessageTableSubscriber(() -> DriverManager
			.getConnection(properties.determineUrl(), properties.determineUsername(), properties.determinePassword())
			.unwrap(PgConnection.class));
	}

	@Bean
	public MessageChannel retrofactoChannel(JdbcChannelMessageStore messageStore) {
		return MessageChannels.queue(messageStore, "retrofacto").getObject();
	}

	@Bean
	public IntegrationFlow streamRetrofactoMessage(MessageChannel retrofactoChannel,
			SseEmitterManager sseEmitterManager) {
		return IntegrationFlow.from(retrofactoChannel).handle(sseEmitterManager).get();
	}

}
