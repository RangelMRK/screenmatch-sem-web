package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = System.getenv("OMDB_API_KEY");
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private Optional<Serie> buscaSerie;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    List<Serie> series = new ArrayList<>();

    public void exibeMenu(){

        var opcao = -1;
        while(opcao !=0) {
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar Séries Buscadas
                4 - Buscar Série por Titulo
                5 - Buscar Séries por Ator/Atriz
                6 - Top 5 Séries
                7 - Buscar Séries por Categoria
                8 - Buscar Séries por número de Temporadas
                9 - Buscar Episódio por trecho
                10 - Top 5 Episódios por Série
                11 - Buscar Episódios a partir de uma Data
                0 - Sair                                 
                """;

            System.out.println(menu);
            opcao = input.nextInt();
            input.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTopSeries();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarPorNumTemporada();
                    break;
                case 9:
                    buscaEpisodioPorTrecho();
                    break;
                case 10:
                    top5Eps();
                    break;
                case 11:
                    buscarEpAposData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }




    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = input.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome:");
        var nomeSerie = input.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getNumTemporadas(); i++) {
                var json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.temporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }



    }
    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = input.nextLine();
        buscaSerie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(buscaSerie.isPresent()){
            System.out.println("Dados da série: " + buscaSerie.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator para busca: ");
        var nomeAtor = input.nextLine();
        System.out.println("Avaliação minima de Série para busca: ");
        var avaliacao = input.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTopSeries() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Qual gênero de série deseja buscar? ");
        var nomeGenero = input.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries do Gênero: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarPorNumTemporada() {
        System.out.println("Digite o número máximo de temporadas da série que deseja buscar: ");
        var numTemp = input.nextInt();
        input.nextLine();
        System.out.println("Avaliação minima de Série para busca: ");
        var avaliacao = input.nextDouble();
        input.nextLine();

        List<Serie> seriesPorTemp = repositorio.seriePorTemporadaEAvaliacao(numTemp, avaliacao);

//        List<Serie> seriesPorTemp = repositorio.findByNumTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(numTemp,avaliacao);
        if(seriesPorTemp.isEmpty()){
            System.out.println("Nenhuma série encontrada!");
        } else {
            System.out.println("Séries com no máximo " + numTemp + " Temporadas:");
            seriesPorTemp.forEach(s ->
                    System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
        }

    }

    private void buscaEpisodioPorTrecho() {
        System.out.println("Digite um Trecho do Titulo do episódio");
        var trechoEp = input.nextLine();
        List<Episodio> epBuscados = repositorio.episodiosPorTrecho(trechoEp);
        epBuscados.forEach(e ->
                System.out.printf("Série: %s | Temporada %s | Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEp(), e.getTitulo()));
    }

    private void top5Eps() {
        buscarSeriePorTitulo();
        if(buscaSerie.isPresent()){
            buscaSerie.get();
            Serie serie = buscaSerie.get();
            List<Episodio> top5 = repositorio.topEpisodiosPorSerie(serie);
            top5.forEach(e ->
                    System.out.printf("Série: %s | Temporada %s | Episódio %s - %s | Avaliação: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEp(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpAposData() {
        buscarSeriePorTitulo();
        if(buscaSerie.isPresent()){
            buscaSerie.get();
            Serie serie = buscaSerie.get();
            System.out.println("Digite o ano Limite do lançamento");
            var anoLancamento = input.nextInt();
            input.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}
