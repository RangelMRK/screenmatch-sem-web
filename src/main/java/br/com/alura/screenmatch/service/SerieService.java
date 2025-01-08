package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterSeriesTop5(){
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());

    }

    public List<SerieDTO> obterSeriesRecentes() {
        return converteDados(repositorio.encontrarEpsMaisRecentes());

    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(),s.getTitulo(),s.getNumTemporadas(),s.getAvaliacao(),
                    s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse());
        } else {
            return null;
        }
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getNumTemporadas(),s.getAvaliacao(),
                        s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());
    }


    public List<EpisodioDTO> obterTodasTemp(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(),e.getNumeroEp(),e.getTitulo()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterEpsPorTemp(Long id, Long numero) {
        return repositorio.obterTemporadasPorNumero(id, numero).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEp(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterEpsPorCategoria(String genero) {
        Categoria categoria = Categoria.fromPortugues(genero);
        return converteDados(repositorio.findByGenero(categoria));
    }

    public List<EpisodioDTO> obterTop5Eps(Long id) {
        return repositorio.topEpisodiosPorSerieId(id).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEp(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}
