package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {

	private Graph<Country, DefaultEdge> graph;
	private Map<Integer, Country> countriesMap;
	
	private Map<Country, Integer> personeStanziali; // Mappa che ha in chiave la nazione e come valore il numero di stanziali per quella nazione

	public Model() {
		this.countriesMap = new HashMap<>();
	}

	public void creaGrafo(int anno) {

		this.graph = new SimpleGraph<>(DefaultEdge.class);

		BordersDAO dao = new BordersDAO();

		// vertici
		dao.getCountriesFromYear(anno, this.countriesMap);
		Graphs.addAllVertices(graph, this.countriesMap.values());

		// archi
		List<Adiacenza> archi = dao.getCoppieAdiacenti(anno);
		for (Adiacenza c : archi) {
			graph.addEdge(this.countriesMap.get(c.getState1no()), this.countriesMap.get(c.getState2no()));

		}
	}

	public List<CountryAndNumber> getCountryAndNumbers() {
		List<CountryAndNumber> result = new LinkedList<>();

		for (Country c : this.graph.vertexSet()) {
			result.add(new CountryAndNumber(c, this.graph.degreeOf(c)));
		}

		Collections.sort(result);
		return result;
	}

	public Set<Country> getCountries() {
		if (this.graph != null) {
			return this.graph.vertexSet();
		}
		return null;
	}
	
	public int simula(Country partenza) {
		Simulatore sim = new Simulatore(graph);
		sim.init(partenza, 1000);
		sim.run();
		personeStanziali = sim.getPersone();
		return sim.getnPassi();
		//System.out.println("Passi: " + sim.getnPassi());
		//System.out.println(sim.getPersone());
	}
	
	public List<CountryAndNumber> getPersoneStanziali() {
		List<CountryAndNumber> lista = new ArrayList<>();
		for (Country c : personeStanziali.keySet()) {
			int persone = personeStanziali.get(c);
			if(persone != 0) {
				lista.add(new CountryAndNumber(c, persone));
			}			
		}
		
		Collections.sort(lista);
		return lista;
	}
}
