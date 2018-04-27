package br.com.ifood.application;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {Application.class})
@DirtiesContext
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class ConfigurationTest {

	@Autowired
	private Configuration configuration;
	
	@Test
	public void testShouldAssertPropertieMqttHost() {
		assertThat(configuration.getMqttHost(), is("localhost"));
	}
	
	@Test
	public void testShouldAssertPropertieMqttPort() {
		assertThat(configuration.getMqttPort(), is("1883"));
	}
	
	@Test
	public void testShouldAssertPropertieMqttTopic() {
		assertThat(configuration.getTopic(), is("ifood/restaurants/+/status"));
	}

}
