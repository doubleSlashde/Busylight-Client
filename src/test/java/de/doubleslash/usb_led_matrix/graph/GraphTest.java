package de.doubleslash.usb_led_matrix.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.scene.paint.Color;

@ExtendWith(MockitoExtension.class)
public class GraphTest {
   Graph graph;

   @Mock
   HttpClient httpClient;

   @BeforeEach
   void setUp() {
      graph = new Graph(httpClient);
   }

   @Test
   void shouldBeGreenWhenTeamsStatusIsAvailable() throws IOException, InterruptedException {
      // Given
      final HttpResponse response = mock(HttpResponse.class);
      when(response.body()).thenReturn(
            "{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('xx')/presence/$entity\",\"id\":\"xx\",\"availability\":\"Available\",\"activity\":\"Available\"}");
      when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(response);
      // When
      final Color statusColorFromTeams = graph.getStatusColorFromTeams();
      // Then
      assertEquals(statusColorFromTeams, AvailabilityStatus.Available.getColor());
   }

   @Test
   void shouldBeBlackWhenTeamsReturnsUnauthorized() throws IOException, InterruptedException {
      // Given
      final HttpResponse response = mock(HttpResponse.class);
      when(response.body()).thenReturn(
            "{\"error\":{\"code\":\"Unauthorized\",\"message\":\"\",\"innerError\":{\"request-id\":\"xx\",\"date\":\"2023-01-23T09:29:58\",\"client-request-id\":\"xx\"}}}");
      when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(response);
      // When
      final Color statusColorFromTeams = graph.getStatusColorFromTeams();
      // Then
      assertEquals(statusColorFromTeams, AvailabilityStatus.Offline.getColor());
   }
}
