package am.ik.retrofacto.retro.sse;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class PostgresCardEventSubscriber {

	private final DataSourceProperties properties;

	private final EventHandler eventHandler;

	private final TaskExecutor taskExecutor;

	private final AtomicBoolean terminated = new AtomicBoolean(false);

	private final ApplicationEventPublisher eventPublisher;

	private final Logger log = LoggerFactory.getLogger(PostgresCardEventSubscriber.class);

	public PostgresCardEventSubscriber(DataSourceProperties properties, EventHandler eventHandler,
			TaskExecutor taskExecutor, ApplicationEventPublisher eventPublisher) {
		this.properties = properties;
		this.eventHandler = eventHandler;
		this.taskExecutor = taskExecutor;
		this.eventPublisher = eventPublisher;
	}

	@PostConstruct
	public void init() {
		this.taskExecutor.execute(() -> {
			try {
				log.info("Start listening");
				try (PgConnection pgConnection = DriverManager
					.getConnection(properties.determineUrl(), properties.determineUsername(),
							properties.determinePassword())
					.unwrap(PgConnection.class);
						PreparedStatement statement = pgConnection.prepareStatement("LISTEN retrofacto_event")) {
					statement.execute();
					while (true) {
						try {
							PGNotification[] notifications = pgConnection.getNotifications(10_000);
							if (terminated.get()) {
								log.debug("terminated");
								break;
							}
							if (notifications == null) {
								continue;
							}
							Arrays.stream(notifications)
								.map(PGNotification::getParameter)
								.map(NotifiedEvent::valueOf)
								.sorted()
								.forEach(event -> {
									log.debug("Received: {}", event);
									this.eventHandler.onEvent(event.slug(), event.payload());
								});
						}
						catch (SQLException e) {
							log.warn("SQL Exception occurred.", e);
							try {
								Thread.sleep(1_000);
							}
							catch (InterruptedException ex) {
								Thread.currentThread().interrupt();
							}
						}
					}

				}
			}
			catch (SQLException | RuntimeException e) {
				log.error("Unexpected exception", e);
				AvailabilityChangeEvent.publish(this.eventPublisher, e, LivenessState.BROKEN);
				throw new IllegalStateException("Unexpected exception", e);
			}
		});
	}

	@PreDestroy
	public void destroy() {
		log.info("Stop listening");
		this.terminated.set(true);
	}

}
