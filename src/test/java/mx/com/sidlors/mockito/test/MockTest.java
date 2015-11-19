package mx.com.sidlors.mockito.test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MockTest {

	private static final Logger logger = Logger.getLogger(MockTest.class);

	@Mock
	List<String> mockedList;

	@Mock
	LinkedList<String> mockedLinkedList;

	@BeforeClass
	public static void befoteClass() {

		MockitoAnnotations.initMocks(MockTest.class);

	}

	@Before
	public void setUp() throws Exception {
		// Creation
		mockedList = mock(List.class);
		mockedLinkedList = mock(LinkedList.class);

	}

	@Test
	public void mockListTest() {

		// verification
		mockedLinkedList.add("one");
		mockedLinkedList.clear();
		verify(mockedLinkedList).add("one");
		verify(mockedLinkedList).clear();

	}

	@Test(expected = RuntimeException.class)
	public void verifyTest() {

		// stubbing
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		// imprime "first"
		logger.info(mockedList.get(0));

		verify(mockedList).get(0);

		// imprime "null" porque no se ha hecho stubbing de get(999)
		// Por defecto todos los métodos que devuelven valores de un mock
		// devuelven null, una colección vacía o el tipo de dato primitivo
		// apropiado.
		logger.info(mockedList.get(999));

		// lanza runtime exception
		logger.info(mockedList.get(1));

	}

	@Test
	public void verifyAnyTest() {

		// // stubbing usando anyInt() argument matcher
		// when(mockedList2.get(anyInt())).thenReturn("element");
		// tambien se puede verificar usando argument matchers
		when(mockedList.get(1)).thenReturn("element");
		logger.info(mockedList.get(1));
		verify(mockedList).get(anyInt());
	}

	@Test
	public void verfyNtimesTest() {

		// usando mock
		mockedList.add("once");

		mockedList.add("twice");
		mockedList.add("twice");

		mockedList.add("three times");
		mockedList.add("three times");
		mockedList.add("three times");

		// times(1) se usa por defecto, por lo tanto las dos verificaciones
		// siguientes son analogas
		verify(mockedList).add("once");
		verify(mockedList, times(1)).add("once");

		// verificacion de numero exacto de invaciones
		verify(mockedList, times(2)).add("twice");
		verify(mockedList, times(3)).add("three times");

		// verificacion utilizando never. never() es un alias de times(0)
		verify(mockedList, never()).add("never happened");

		// verificacion utilizando atLeast()/atMost()
		verify(mockedList, atLeastOnce()).add("three times");
		// aqui falla pues no existe ningun llamado con el parametro:
		// "five times"
		// verify(mockedList, atLeast(2)).add("five times");
		verify(mockedList, atMost(5)).add("three times");

	}

	@Test(expected = RuntimeException.class)
	public void ExceptionThrowTest() {
		doThrow(new RuntimeException()).when(mockedList).clear();

		// la siguiente llamada lanza RuntimeException:
		mockedList.clear();
	}

}
