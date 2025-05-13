package usecases;

import adapters.ActiveMQTweetConsumer;
import entities.TweetResult;
import org.junit.jupiter.api.*;
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
        // Redirigir System.out
        System.setOut(new PrintStream(outputStreamCaptor));

        // Mocks
        mockSender = mock(TweetSender.class);
        mockProvider = mock(TweetProvider.class);

        // Mock de consumer para capturar el listener
        ActiveMQTweetConsumer mockConsumer = mock(ActiveMQTweetConsumer.class);
        doAnswer(invocation -> {
            listener = invocation.getArgument(0);
            return null;
        }).when(mockConsumer).registerListener(any());

        // Instanciar Controller y registrar listener
        controller = new Controller(mockSender, mockConsumer, mockProvider);
        controller.run();
        assertNotNull(listener, "El listener debe registrarse correctamente");
    }

    @AfterEach
    public void tearDown() {
        // Restaurar System.out
        System.setOut(originalSystemOut);
    }

    @Test
    public void testValidJsonMessageTriggersSend() throws Exception {
        // Datos de prueba
        String player = "Messi";
        String type = "goal";
        TweetResult fakeResult = new TweetResult("¡Gol de Messi!", 100, 10, 50, 6);

        when(mockProvider.generate(type, player)).thenReturn(fakeResult);

        String json = String.format("{\"player\":\"%s\",\"type\":\"%s\"}", player, type);

        // Simular llegada de mensaje
        listener.accept(json);

        // Verificar llamadas
        verify(mockProvider, times(1)).generate(type, player);
        verify(mockSender, times(1)).send(fakeResult);

        // Opcional: verificar que el controller imprime algo sobre el evento
        String out = outputStreamCaptor.toString();
        assertTrue(out.contains("📥 Mensaje recibido"), "Debe imprimir log de recepción");
    }

    @Test
    public void testInvalidJsonMessageDoesNotTriggerSend() {
        String badJson = "{ \"foo\": \"bar\" }";

        listener.accept(badJson);

        verifyNoInteractions(mockProvider);
        verifyNoInteractions(mockSender);

        String out = outputStreamCaptor.toString();
        assertFalse(out.contains("Evento leído"), "No debe procesar JSON inválido");
    }

    @Test
    public void testMalformedJsonDoesNotThrow() {
        String malformed = "not a json";
        assertDoesNotThrow(() -> listener.accept(malformed));

        verifyNoInteractions(mockProvider);
        verifyNoInteractions(mockSender);

        String out = outputStreamCaptor.toString();
        assertFalse(out.contains("Evento leído"), "No debe procesar mensaje mal formado");
    }

    @Test
    public void testPrintsReceivedJsonMessage() throws Exception {
        // Preparamos un JSON válido
        String player = "Mbappé";
        String type = "goal";
        String json = String.format("{\"player\":\"%s\",\"type\":\"%s\"}", player, type);

        TweetResult fakeResult = new TweetResult("¡Gol de Mbappé!", 120, 15, 40, 6);
        when(mockProvider.generate(type, player)).thenReturn(fakeResult);

        // Ejecutamos
        listener.accept(json);

        // Verificamos que el JSON fue impreso
        String out = outputStreamCaptor.toString();
        assertTrue(out.contains(json), "El JSON recibido debe imprimirse en la consola");
    }
}
