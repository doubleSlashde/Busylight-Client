package de.doubleslash.usb_led_matrix.usb_adapter;

import de.doubleslash.usb_led_matrix.Settings;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsbAdapterTest {
   @Mock
   SerialPort serialPort;

   @Captor
   ArgumentCaptor<byte[]> byteCaptor;

   private UsbAdapter usbAdapter;

   @BeforeEach
   void setup() {
      usbAdapter = new UsbAdapter(serialPort);
   }

   private static Stream<Arguments> provideLedsAndBytes() {
      return Stream.of(
            // (number of LEDs, expected number of bytes)
            Arguments.of(1, 5), //
            Arguments.of(8, 26) //
      );
   }

   @ParameterizedTest(name = "[Number of LEDS] {index}: Should return {1} bytes for {0} LEDs")
   @MethodSource("provideLedsAndBytes")
   void shouldReturnCorrectNumberOfBytesForGivenNumberOfLeds(final int numberOfLeds, final int expectedNumberOfBytes)
         throws SerialPortException {
      // Given
      usbAdapter.numberLEDS = (byte) numberOfLeds;
      final byte[] expectedSentBytes = new byte[expectedNumberOfBytes];
      expectedSentBytes[1] = (byte) numberOfLeds;
      final byte[] readControlCode = { ControlCodes.READY_FOR_IMAGE_CHANGE };
      when(serialPort.readBytes(1)).thenReturn(readControlCode);

      // When
      usbAdapter.serialEvent(new SerialPortEvent(serialPort, SerialPort.MASK_RXCHAR, 0));

      // Then
      verify(serialPort).writeBytes(byteCaptor.capture());
      final byte[] actuallySentBytes = byteCaptor.getValue();
      assertArrayEquals(expectedSentBytes, actuallySentBytes);
   }

   @Test
   void shouldNumberLEDSWhenCommandLineOptionIsSet() {
      // Given
      String[] args = { "-nr", "5" };

      // When
      Settings.handleParameters(args);
      usbAdapter = new UsbAdapter();

      // Then
      assertEquals(usbAdapter.numberLEDS, 5);
   }
}
