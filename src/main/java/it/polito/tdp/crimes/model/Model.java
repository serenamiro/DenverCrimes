package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<Integer> getMesi(){
		return dao.getMesi();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo(String categoria, Integer mese) {
		this.grafo = new SimpleWeightedGraph(DefaultWeightedEdge.class);
		
		List<Adiacenza> adiacenze = this.dao.getAdiacenze(categoria, mese);
		
		for(Adiacenza a : adiacenze) {
			if(!this.grafo.containsVertex(a.getV1())){
				this.grafo.addVertex(a.getV1());
			}
			if(!this.grafo.containsVertex(a.getV2())){
				this.grafo.addVertex(a.getV2());
			}
			
			if(this.grafo.getEdge(a.getV1(), a.getV2()) == null) {
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+this.grafo.edgeSet().size()+" archi.");
	}
	
	public List<Arco> getArchi(){
		double pesoMedio = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		pesoMedio = pesoMedio/this.grafo.edgeSet().size();
		
		List<Arco> archi = new ArrayList<Arco>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pesoMedio)
				archi.add(new Arco(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		Collections.sort(archi);
		return archi;
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		
		List<String> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		
		parziale.add(sorgente);
		
		trovaRicorsivo(destinazione, parziale, 0);
		
		return this.best;
	}

	private void trovaRicorsivo(String destinazione, List<String> parziale, int L) {
		// CASI TERMINALI
		// Quando l'ultimo vertice inserito in parziale è uguale alla destinazione
		// (in quel caso verifico il peso)
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>this.best.size()) {
				this.best = new ArrayList<>(parziale);
			}
			return;
		}
		
		// SE NON SONO NEL CASO TERMINALE
		// scorro i vicini dell'ultimo vertice inserito in parziale
		for(String vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			// cammino aciclico -> controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				// provo ad aggiungere
				parziale.add(vicino);
				this.trovaRicorsivo(destinazione, parziale, L+1);
				// faccio backtracking
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
	
}


