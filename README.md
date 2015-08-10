##mockito

[Mockito]()

[Introducción]()

[Verificar el comportamiento]()

[Stubbing]()

[Argument matchers]()

[Verficiando el numero exacto de invocaciones, al menos X, o ninguna invocación]()

[Stubbing metodos void methods con excepciones]()

[Verificaciones en orden]()

[Asegurandonos que alguna(s) interaccion(es) nunca ocurren en un mock]()

[Buscando llamadas redundantes]()

[@Mock]()

[Conclusiones]()

###Introducción

Muchos de vosotros conoceréis TDD (Test Drive development), desarrollo conducido por pruebas, donde la fuerza de nuestros programas se basa justo en eso, en los test de las clases que vamos generando (De hecho TDD propone generar los test antes de crear el cï¿½digo de la clase).

Para poder crear un buen conjunto de pruebas unitarias, es necasario que nos centremos exclusivamente en la clase a testear, simulando el funcionamiento de las capas inferiores (pensad por ejemplo en olvidarnos de la capa de acceso a datos, DAO). De esta manera estaremos creando test unitarios potentes que os permitiría detectar y solucionar los errores que tengáis o que se cometan durante el futuro del desarrollo de vuestra aplicación.

Para esta tarea nos apoyaremos en el uso de mock objects, que no son más que objetos que simulan parte del comportamiento de una clase, y más especificamente vamos a ver una herramienta que permite generar mock objects dinámicos, mockito.

Mockito está basado en EasyMock, y el funcionamiento es prácticamente parecido, aunque mejora el api a nivel sintáctico, haciéndolo más entendible para nosotros (no existe el concepto de expected, para aquellos que sepáis algo de EasyMock), y además permite crear mocks de clases concretas (y no sólo de interfaces).

La idea de las pruebas al usar mockito es el concepto de stubbing – ejecutar – verificar (programar un comportamiento, ejecutar las llamadas y verificar las llamadas), donde centraremos nuestros esfuerzos, no en los resultados obtenidos por los métodos a probar (o al menos no solo en ello), si no en las interacciones de las clases a probar y las clases de apoyo, de las cuales generamos mocks.

Vamos a ver el funcionamiento de mockito con diferentes ejemplos para asi apreciar la funcionalidad y potencial de este tipo de herramientas.

Los siguientes ejemplos van a realizar mocks sobre listas (List), simplemente porque la interfaz List es muy conocida y asi se facilita la comprensión de dichos fragmentos de código. Quizás los ejemplos parezcan demasiado simples o incluso poco lógicos, pero sólo los usaremos para comprender e ir conociendo el api de mockito de una manera sencilla.


###Verificar el comportamiento

Una vez realizadas las llamadas necesarias al objecto que estamos probando mediante mocks, vamos a comprobar que las iteraciones se han realizado correctamente:


```java
//creacion de mock
List mockedList = mock(List.class);
 
//utilizando el mock object
mockedList.add("one");
mockedList.clear();
 
//verificacion
verify(mockedList).add("one");
verify(mockedList).clear();
Una vez creado, el mock recuerda todas las interacciones. Se puede elegir indiferentemente que interacción verificar
```

###Stubbing

También podemos programar el comportamiento de los mocks, indicando qué deben devolver ciertos métodos.


```java
//se pueden hacer mock de clases concretas, no solo interfaces
LinkedList mockedList = mock(LinkedList.class);
 
//stubbing
when(mockedList.get(0)).thenReturn("first");
when(mockedList.get(1)).thenThrow(new RuntimeException());
 
//imprime "first"
System.out.println(mockedList.get(0));
 
//lanza runtime exception
System.out.println(mockedList.get(1));
 
//imprime "null" porque no se ha hecho stubbing de get(999)
System.out.println(mockedList.get(999));
 
verify(mockedList).get(0);
```

Por defecto todos los métodos que devuelven valores de un mock devuelven null, una colección vacía o el tipo de dato primitivo apropiado.

Argument matchers

Los arguments matchers permiten realizar llamadas a métodos mediante ‘comodines’, de forma que los párametros a los mismos no se tengan que definir explícitamente:


```java
//stubbing usando anyInt() argument matcher
when(mockedList.get(anyInt())).thenReturn("element");
 
//stubbing usando hamcrest (libreria de matchers) (digamos que isValid() devuelve tu propio matcher):
when(mockedList.contains(argThat(isValid()))).thenReturn("element");
 
//imprime "element"
System.out.println(mockedList.get(999));
 
//tambien se puede verificar usando argument matchers
verify(mockedList).get(anyInt());
```

Argument matchers permiten realizar stubbing o verificaciones muy flexibles. podéis ver mas en http://mockito.googlecode.com/svn/branches/1.7/javadoc/org/mockito/Matchers.html

Verficiando el numero exacto de invocaciones, al menos X, o ninguna invocación

Vamos a ver ahora cómo verificar si se ha un cumplido un número mínimo o máximo de llamadas al mock:


```java
//usando mock 
mockedList.add("once");
 
mockedList.add("twice");
mockedList.add("twice");
 
mockedList.add("three times");
mockedList.add("three times");
mockedList.add("three times");
 
//las dos verificaciones siguientes trabajan de la misma manera (times(1) se usa por defecto)
verify(mockedList).add("once");
verify(mockedList, times(1)).add("once");
 
//verificacion de numero exacto de invaciones
verify(mockedList, times(2)).add("twice");
verify(mockedList, times(3)).add("three times");
 
//verificacion utilizando never. never() es un alias de times(0)
verify(mockedList, never()).add("never happened");
 
//verificacion utilizando atLeast()/atMost()
verify(mockedList, atLeastOnce()).add("three times");
verify(mockedList, atLeast(2)).add("five times");
verify(mockedList, atMost(5)).add("three times");
```

Stubbing metodos void methods con excepciones

Veamos ahora cómo realizar stubbing de métodos que no devuelven nada (por ejemplo para indicar que deben lanzar una excepción):


```java
doThrow(new RuntimeException()).when(mockedList).clear();
   
//la siguiente llamada lanza RuntimeException:
mockedList.clear();
Verificaciones en orden
```

Si necesitamos que varios mock necesiten llevar un orden específico en las llamadas lo podemos realizar de la siguiente manera:

```java
List firstMock = mock(List.class);
List secondMock = mock(List.class);
 
//usando mocks
firstMock.add("was called first");
secondMock.add("was called second");
 
//creamos un objeto inOrder, pasando los mocks que necesitan verificarse en orden
InOrder inOrder = inOrder(firstMock, secondMock);
 
//verficamos que firstMock ha sido invocado antes que secondMock
inOrder.verify(firstMock).add("was called first");
inOrder.verify(secondMock).add("was called second");
```

Realizar verificaciones en orden son muy flexibles. no es necesario verificar todas las interacciones, si no sólo aquellas que necesitamos.

Asegurandonos que alguna(s) interaccion(es) nunca ocurren en un mock


```java
//usando mocks - solo se interactua sobre mockOne
mockOne.add("one");
 
//verificacion ordinaria
verify(mockOne).add("one");
 
//verificamos que el metodo nunca ha sido llamado en el mock
verify(mockOne, never()).add("two");
 
//verificamos que otros mocks no obtienen interactuaciones
verifyZeroInteractions(mockTwo, mockThree);
```

Buscando llamadas redundantes


```java
//usando mocks
mockedList.add("one");
mockedList.add("two");
 
verify(mockedList).add("one");
 
//la siguiente verificacion fallara
verifyNoMoreInteractions(mockedList);
```
Ojo! : verifyNoMoreInteractions() debe ser llamada solo cuando necesario. Realizar llamadas a este método asiduamente (sobre todo en todas las pruebas) generará test muy poco mantenibles y ampliables. Es mejor usar never() para aquellos métodos que no deban ser interaccionados.

###@Mock

Nos permite realizar mocks anotando el código, y así el mismo queda más claro y limpio.

```java
public class ArticleManagerTest { 
     
       @Mock private ArticleCalculator calculator;
       @Mock private ArticleDatabase database;
       @Mock private UserProvider userProvider;
     
       private ArticleManager manager;
       ...
}
```

Importante! La siguiente llamada debe encontrarse en algun lugar de una clase base o del test runner:


```java
MockitoAnnotations.initMocks(testClass);
```

O se pueden usar como runners MockitoJUnitRunner, MockitoJUnit44Runner. (veremos en otro tutorial un ejemplo).

###Conclusiones

Como hemos podido ver en este tutorial, el uso de mock objects nos facilita mucho crear test unitarios que no dependen de las capas inferiores, y por tanto prueben las clases de cierta capamucho más exhaustivamente, permitiendo la detecci&aoacute;n de errores y asegurándonos el buen funcionamiento durante el futuro.

Algunos, tras ver los snippets de código anteriores pensaran… ¿y para qué me sirve mockito?, ¿por qué ‘perder’ el tiempo usando mock objects cuando puedo realizar las pruebas apoyandome en otras clases que ya han sido probadas, y funcionan bien?. Es un error. Lo primero, NUNCA se pierde tiempo en generar test ni en usar mock objects, puesto que ese código nos automatizará las tareas de pruebas nos sólo durante el desarrollo, si no tambien durante las fases de mantenimiento de la aplicación; y pensar que el código que hoy no falla no puede fallar mañana es err&aoacute;neo también.. y si el fallo esta en las clases en las que nos apoyamos.. nuestros tests fallaran cuando nuestras clases (puede que) funcionen bien.


