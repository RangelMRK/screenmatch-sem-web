package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService servico;

    @GetMapping()
    public List<SerieDTO> obterSeries() {
        return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5(){
        return servico.obterSeriesTop5();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterRecentes(){
        return servico.obterSeriesRecentes();
    }
    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return servico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasAsTemp(@PathVariable Long id){
        return servico.obterTodasTemp(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterEpisodiosPorTemp(@PathVariable Long id, @PathVariable Long numero){
        return servico.obterEpsPorTemp(id, numero);
    }

    @GetMapping("/categoria/{genero}")
    public List<SerieDTO> obterEpsPorGenero(@PathVariable String genero) {
        return servico.obterEpsPorCategoria(genero);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> top5Eps(@PathVariable Long id){
        return servico.obterTop5Eps(id);
    }
}
