package org.springframework.cloud.client.discovery.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientTestsConfig.CUSTOM_SERVICE_ID;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for behavior of Composite Discovery Client
 * 
 * @author Biju Kunjummen
 */

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"spring.application.name=service0",
		"spring.cloud.discovery.client.simple.instances.service1[0].uri=http://s1-1:8080",
		"spring.cloud.discovery.client.simple.instances.service1[1].uri=https://s1-2:8443",
		"spring.cloud.discovery.client.simple.instances.service2[0].uri=https://s2-1:8080",
		"spring.cloud.discovery.client.simple.instances.service2[1].uri=https://s2-2:443", }, classes = {
				CompositeDiscoveryClientTestsConfig.class })
public class CompositeDiscoveryClientTests {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Test
	public void getInstancesByServiceIdShouldDelegateCall() {
		assertThat(discoveryClient).isInstanceOf(CompositeDiscoveryClient.class);

		assertThat(discoveryClient.getInstances("service1")).hasSize(2);

		ServiceInstance s1 = discoveryClient.getInstances("service1").get(0);
		assertThat(s1.getHost()).isEqualTo("s1-1");
		assertThat(s1.getPort()).isEqualTo(8080);
		assertThat(s1.getUri()).isEqualTo(URI.create("http://s1-1:8080"));
		assertThat(s1.isSecure()).isEqualTo(false);
	}
	
	@Test
	public void getServicesShouldAggregateAllServiceNames() {
		assertThat(discoveryClient.getServices()).containsOnlyOnce("service1", "service2",
				CUSTOM_SERVICE_ID);
	}
	
	@Test
	public void getDescriptionShouldBeComposite() {
		assertThat(discoveryClient.description()).isEqualTo("Composite Discovery Client");
	}
	
	@Test
	public void getInstancesShouldRespectOrder() {
		assertThat(discoveryClient.getInstances(CUSTOM_SERVICE_ID)).hasSize(1);
		assertThat(discoveryClient.getInstances(CUSTOM_SERVICE_ID)).hasSize(1);
	}

	@Test
	public void getInstancesByUnknownServiceIdShouldReturnAnEmptyList() {
		assertThat(discoveryClient.getInstances("unknown")).hasSize(0);
	}
}
