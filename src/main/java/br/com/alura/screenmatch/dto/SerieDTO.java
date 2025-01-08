package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;


public record SerieDTO(Long id,
                       String titulo,
                       Integer numTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse) {

}
