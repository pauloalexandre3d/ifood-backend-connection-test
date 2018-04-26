import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.Test;

public class MqttClientTest {

	@Test
	public void test() throws MqttSecurityException, MqttException {
//		iot.eclipse.org
		MqttClient client = new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setKeepAliveInterval(10);
		client.connect(options );
		
		MqttMessage message = new MqttMessage();
		message.setPayload("{\"restaurantId\":1, \"status\":\"online\"}".getBytes());
		
		client.publish("ifood/heartbeat", message);
		client.disconnect();
	}

}
