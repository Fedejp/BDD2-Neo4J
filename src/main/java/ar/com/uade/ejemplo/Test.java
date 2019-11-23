package ar.com.uade.ejemplo;

public class Test {

	public static void main(String[] args) {
		Conector connect = new Conector();
		String shortname;
		System.out.println("#### Programa de demostración de consumo de base de datos NEO4J ####");
		System.out.println("## Respuestas a consultas planteadas en el trabajo práctico ##");
		System.out.println("# Consulta 2: todas las unidades alquiladas #");
		connect.getUnidadesAlquiladas();
		System.out.println("--------------------------------------------");
		System.out.println("# Consulta 6: Monto a pagar de expensas por un dueño dado #");
		System.out.println("Dueño dado: DNI 123");
		connect.getExpensasAPagar(123);
		System.out.println("## Funciones CRUD ##");
		System.out.println("# Creación de un edificio (edificio + unidades) #");
		shortname = connect.crearEdificio();
		System.out.println("# Demostración de creado / lectura de un edificio: #");
		connect.getEdificio(shortname);
		System.out.println();
		System.out.println("# Actualizacion de un edificio (cambio de nombre) #");
		connect.cambiarNombreEdificio(shortname);
		System.out.println("# Demostración de actualizado: #");
		connect.getEdificio(shortname);
		System.out.println();
		System.out.println("# Eliminación del edificio dado #");
		connect.eliminarEdificio(shortname);
		System.out.println("# Demostración de eliminad / lectura de un edificio inexistente: #");
		connect.getEdificio(shortname);
		System.out.println("#### Fin del programa de prueba ####");
		System.exit(0);

	}

}
