package ar.com.uade.ejemplo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Statement;
import org.neo4j.driver.StatementResult;
public class Conector {

	
	private Driver driver;
	
	public Conector() {
		driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic( "neo4j", "123456" ));
	}
	
	public Session openSesion() {
		return driver.session();
	}
	
	public void closeSesion(Session sesion) {
		sesion.close();
	}
	
	public void getOwners() {
		
		Session sesion = this.openSesion();
		Statement query = new Statement("match(n:Owner) return n");
		StatementResult record = sesion.run(query);
		List<Record> registros = record.list();
		for(Record registro : registros) {
			/*
			 * registros es una lista de nodos. 
			 * 
			 * get(0) me devuelve el primer valor del nodo
			 * 
			 * El segundo get me permite seguir por las propiedades.
			 * 
			 * */
			System.out.println(registro.get(0).get("name") + " -- " + registro.get(0).get("surname"));
		}
		
		this.closeSesion(sesion);
	}
	
	public void getUnidadesAlquiladas() { //Consulta 2
		Session sesion = this.openSesion();
		Statement query = new Statement("match (b:Building)-[:HAS]-(u:Unit{status:'rented'})-[:RENTS]-(t:Tennant) "
				+ "return b.name,u.code,t.name, t.surname");
		StatementResult record = sesion.run(query);
		List<Record> registros = record.list();
		for(Record r : registros) {
			System.out.println("Edificio: "+r.get(0) +" Unidad: "+r.get(1));
			System.out.println("Inquilino: "+ r.get(3)+ ", "+r.get(2));
		}
		this.closeSesion(sesion);
	}
	
	public void getExpensasAPagar(int dniduenio) {
		Session sesion = this.openSesion();
		Statement query = new Statement("match (o:Owner{id:"+dniduenio+"})-[:OWNS]-(u:Unit)"
				+ " return o, sum(u.expenses)");
		StatementResult record = sesion.run(query);
		Record res = record.single();
		System.out.println("Dueño: "+res.get(0).get("surname")+", "+res.get(0).get("name"));
		System.out.println("Total a pagar: $"+res.get(1));
		
		
		this.closeSesion(sesion);
	}
	
	public String crearEdificio() {
		String shortname, name, address;
		int pisos,unidadesxpiso;
		List<Integer> expensasxtipounidad = new ArrayList<Integer>();
		Scanner entrada= new Scanner(System.in);
		System.out.print("Ingrese nombre del edificio: ");
		name = entrada.nextLine();
		System.out.print("Ingrese un nombre corto para el  edificio (Abreviatura de no más de 3 alfanuméricos): ");
		shortname = entrada.nextLine();
		System.out.print("Ingrese la dirección del edificio: ");
		address= entrada.nextLine();
		System.out.print("Ingrese la cantidad de pisos del edificio (Máximo 20): ");
		pisos= entrada.nextInt();
		int max = 20/pisos;
		System.out.print("Ingrese la cantidad de unidades por piso del edificio (Máximo "+max+"): ");
		unidadesxpiso = entrada.nextInt();
		for(int i=0;i<unidadesxpiso;i++) {
			System.out.print("Ingrese valor de expensas para unidades '"+(char)(i+'A')+"': $");
			expensasxtipounidad.add(entrada.nextInt());
		}
		System.out.println("AVISO: todas las unidades se crearán con estado 'En venta' de forma predeterminada. Cualquier modificación deberá realizarse a posterirori");
		String query = "CREATE (edif:Building{shortname:'"+shortname+"', name:'"+name+"', address:'"+address+"'}),";
		int count = 0;
		for(int i=1;i<=pisos;i++) {
			for(int j=0;j<unidadesxpiso;j++) {
				String queryuni = "("+(char)(count+'a')+":Unit{code:'"+i+""+(char)(j+'A')+"', status: 'for sale', expenses:"+expensasxtipounidad.get(j)+"})";
				count++;
				if(i==pisos && j==unidadesxpiso-1) {
					queryuni= queryuni.concat(" ");
				} else {
					queryuni= queryuni.concat(", ");
				}
				query = query.concat(queryuni);
			}
		}
		query = query.concat("WITH edif, ");
		for (int i=0; i<count; i++) {
			if(i==count-1) {
			query= query.concat((char)(i+'a')+" ");
			}else {
			query= query.concat((char)(i+'a')+", ");
			}
		}
		query = query.concat("CREATE ");
		for (int i=0; i<count; i++) {
			if(i==count-1) {
				query= query.concat("(edif)-[:HAS]->("+(char)(i+'a')+") ");
				}else {
				query= query.concat("(edif)-[:HAS]->("+(char)(i+'a')+"), ");
				}
		}
		System.out.println(query);
		Session sesion = this.openSesion();
		Statement stat = new Statement(query);
		sesion.run(stat);
		System.out.println("Edificio '"+name+"' (con "+pisos * unidadesxpiso+" unidades) Creado!");
		this.closeSesion(sesion);
		return shortname;
		
	}
	public void eliminarEdificio(String shortname) {
		Session sesion = this.openSesion();
		Statement query = new Statement("match (b:Building{shortname:'"+shortname+"'})-[:HAS]-(u:Unit) "
				+ "detach delete b,u");
		sesion.run(query);
		System.out.println("El edificio "+shortname+" ha sido eliminado.");
		this.closeSesion(sesion);
	}
	
	public void getEdificio(String shortname) {
		Session sesion = this.openSesion();
		Statement query = new Statement("match (b:Building{shortname:'"+shortname+"'})-[:HAS]-(u:Unit) "
				+ "return (b),(u)");
		StatementResult record = sesion.run(query);
		List<Record> registros = record.list();
		if (!registros.isEmpty()) {
			System.out.println("Edificio: "+registros.get(0).get("b").get("name"));
			System.out.print("Lista de Unidades: ");
			for (Record r: registros) {
				System.out.print(r.get("u").get("code")+ ", ");
			}
		} else {
			System.out.println("No existe el edificio '"+shortname+"'.");
		}
		this.closeSesion(sesion);
		
	}

	public void cambiarNombreEdificio(String shortname) {
		Scanner entrada= new Scanner(System.in);
		System.out.print("Ingrese un nuevo nombre para el edificio de código '"+shortname+"': ");
		String newname = entrada.nextLine();
		Session sesion = this.openSesion();
		Statement query = new Statement("match (b:Building{shortname:'"+shortname+"'}) "
				+ "SET b.name='"+newname+"'");
		sesion.run(query);
		this.closeSesion(sesion);
	}
	
	
	
}
