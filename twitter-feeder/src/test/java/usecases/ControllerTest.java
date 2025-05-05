package usecases;

import adapters.ActiveMQTweetConsumer;
import entities.TweetResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ports.TweetProvider;
import ports.TweetSender;

import javax.jms.JMSException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerTest {
    private TweetSender mockSender;
    private TweetProvider mockProvider;
    private Controller controller;
    private Consumer<String> listener;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalSystemOut = System.out;

    @BeforeEach
    public void setUp() throws JMSException {
        // Redirigir la salida estándar a un ByteArrayOutputStream
        System.setOut(new PrintStream(outputStreamCaptor));

        // Crear mocks
        mockSender = mock(TweetSender.class);
        mockProvider = mock(TweetProvider.class);

        // Mock de ActiveMQTweetConsumer solo para capturar el listener
        ActiveMQTweetConsumer mockConsumer = mock(ActiveMQTweetConsumer.class);
        doAnswer(invocation -> {
            listener = invocation.getArgument(0);
            return null;
        }).when(mockConsumer).registerListener(any());

        // Instanciar controller con mocks
        controller = new Controller(mockSender, mockConsumer, mockProvider);
        controller.run();
        assertNotNull(listener, "El listener debe registrarse correctamente");
    }

    @Test
    public void testValidJsonMessageTriggersSend() throws Exception {
        // Datos de prueba
        String player = "Messi";
        String type = "goal";
        TweetResult fakeResult = new TweetResult("¡Gol de Messi!", 100, 10, 50);

        // Configurar comportamiento del proveedor
        when(mockProvider.generate(type, player)).thenReturn(fakeResult);

        // Construir JSON válido
        String json = String.format("{\"player\": \"%s\", \"type\": \"%s\"}", player, type);

        // Invocar listener
        listener.accept(json);

        // Verificar que se llame a provider.generate y sender.send
        verify(mockProvider, times(1)).generate(type, player);
        ArgumentCaptor<TweetResult> captor = ArgumentCaptor.forClass(TweetResult.class);
        verify(mockSender, times(1)).send(captor.capture());
        TweetResult sent = captor.getValue();

        // Verificar que el mensaje se haya impreso
        assertTrue(outputStreamCaptor.toString().contains("Evento leído número 0"));

        assertEquals(fakeResult.getText(), sent.getText());
        assertEquals(fakeResult.getLikes(), sent.getLikes());
        assertEquals(fakeResult.getComments(), sent.getComments());
        assertEquals(fakeResult.getRetweets(), sent.getRetweets());
    }

    @Test
    public void testInvalidJsonMessageDoesNotTriggerSend() throws Exception {
        // JSON sin claves necesarias
        String badJson = "{ \"foo\": \"bar\" }";

        // Invocar listener
        listener.accept(badJson);

        // Verificar que no se llame ni al proveedor ni al sender
        verifyNoInteractions(mockProvider);
        verifyNoInteractions(mockSender);

        // Verificar que no se imprima mensaje de evento leído
        assertFalse(outputStreamCaptor.toString().contains("Evento leído"));
    }

    @Test
    public void testMalformedJsonDoesNotThrow() {
        String malformed = "not a json";
        // Asegurarse que no lanza excepción
        assertDoesNotThrow(() -> listener.accept(malformed));
        verifyNoInteractions(mockProvider);
        verifyNoInteractions(mockSender);

        // Verificar que no se imprima mensaje de evento leído
        assertFalse(outputStreamCaptor.toString().contains("Evento leído"));
    }
}
