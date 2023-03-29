import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) throws Exception {

        // fazer uma conexão HTTP e buscar os top 250 filmes
        // String url = "https://raw.githubusercontent.com/alura-cursos/imersao-java-2-api/main/TopMovies.json";
        // String url = "https://raw.githubusercontent.com/alura-cursos/imersao-java-2-api/main/TopTVs.json";
        String url = "https://raw.githubusercontent.com/alura-cursos/imersao-java-2-api/main/MostPopularMovies.json";
        // String url = "https://raw.githubusercontent.com/alura-cursos/imersao-java-2-api/main/MostPopularTVs.json";

        URI endereco = URI.create(url);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(endereco).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String body = response.body();

        // extrair só os dados que interessam (título, poster, classificação)
        var parser = new JsonParser();
        List<Map<String, String>> listaDeFilmes = parser.parse(body); 

        // exibir e manipular os dados
        var diretorio = new File("figurinhas/");
        diretorio.mkdir();

        for (int i = 0; i < listaDeFilmes.size(); i++) {
            Map<String, String> filme = listaDeFilmes.get(i);

            String urlImagem = filme.get("image");
            String titulo = filme.get("title");
            double classificacao = Double.parseDouble(filme.get("imDbRating"));

            String textoFigurinha;
            InputStream rankingFilmes;
            if (classificacao >= 9.0) {
                textoFigurinha = "TOPZERA";
                rankingFilmes = new FileInputStream(new File("sobreposicao/oscar.png"));
            } 
            else if (classificacao < 9 && classificacao >= 7) {
                textoFigurinha = "OK";
                rankingFilmes = new FileInputStream(new File("sobreposicao/aprovado.png"));
            }
            else {
                textoFigurinha = "DECEPCIONANTE...";
                rankingFilmes = new FileInputStream(new File("sobreposicao/rejeitado.png"));
            }

            InputStream inputStream = new URL(urlImagem).openStream();
            String nomeArquivo = "figurinhas/" + titulo.replace(":", " -") + ".png";

            var geradora = new GeradoraDeFigurinhas();
            geradora.cria(inputStream, nomeArquivo, textoFigurinha, rankingFilmes);
                
            if (i == 0){
                System.out.print("\u001b[1m\u001b[33mPrimeiro lugar 🥇");
            }
            if (i == 1){
                System.out.print("\u001b[1m\u001b[33mSegundo lugar 🥈");
            }
            if (i == 2){
                System.out.print("\u001b[1m\u001b[33mTerceiro lugar 🥉");
            }
            System.out.println("\u001b[m\n\u001b[1m\u001b[41m Title:\u001b[m\u001b[1m\u001b[31m " + filme.get("title"));
            System.out.println("\u001b[m\n\u001b[1m\u001b[44m Poster:\u001b[m\u001b[1m\u001b[34m " + filme.get("image"));
            System.out.print("\u001b[m\n\u001b[1m\u001b[42m Rating:\u001b[m\u001b[1m\u001b[33m " + filme.get("imDbRating") + " ");
            int numeroEstrelinhas = (int) classificacao;
            for (int n = 1; n <= numeroEstrelinhas; n++) {
                System.out.print("⭐");
            }

            System.out.println("\n\n\u001b[m");
        }
    }
}
