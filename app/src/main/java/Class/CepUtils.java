package Class;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

public class CepUtils {

    public static Endereco buscarCep(String cep) throws Exception {
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP inválido. Use 8 dígitos numéricos.");
        }

        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5 segundos
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Erro ao conectar: HTTP " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder json = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            json.append(linha);
        }
        reader.close();

        Gson gson = new Gson();
        Endereco endereco = gson.fromJson(json.toString(), Endereco.class);

        // Verificação correta do erro do ViaCEP
        if (endereco.getErro() != null && endereco.getErro()) {
            throw new IllegalArgumentException("CEP não encontrado.");
        }

        return endereco;
    }
}
