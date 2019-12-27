package Comum.Pedidos.Serializers;

import Comum.FilteredResult;
import Comum.Pedidos.*;
import Comum.Pedidos.Enums.TipoPedido;
import Comum.Song;
import Comum.Utilizador;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PedidoDeserializer implements JsonDeserializer<Pedido> {
    @Override
    public Pedido deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement tipoJsonElement = jsonObject.get("tipo");
        if(tipoJsonElement == null)
            throw new JsonParseException("Não consegui deserializar o Pedido (tipo == null)");
        String tipo = tipoJsonElement.getAsString();
        Gson gson = new Gson();
        Utilizador u = gson.fromJson(jsonObject.get("utilizador"), Utilizador.class);

        if(tipo.equals(TipoPedido.PedidoLogin.toString())){
            PedidoLogin p = new PedidoLogin();
            p.setUtilizador(u);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoSignUp.toString())){
            PedidoSignUp p = new PedidoSignUp();
            p.setUtilizador(u);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoUploadFile.toString())){
            PedidoUploadFile p = new PedidoUploadFile();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoDownloadFile.toString())){
            PedidoDownloadFile p = new PedidoDownloadFile();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoMusicas.toString())){
            PedidoMusicas p = new PedidoMusicas();
            p.setUtilizador(u);
            return p;
        }else if(tipo.equals(TipoPedido.PedidoSearch.toString())){
            Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson gson2 = new GsonBuilder().registerTypeAdapter(FilteredResult.class, new FilteredResultDeserializer()).create();

            PedidoSearch p = gson1.fromJson(json, PedidoSearch.class);
            FilteredResult filteredResult = gson2.fromJson(jsonObject.get("filteredResult"), FilteredResult.class);
            p.setFilteredResult(filteredResult);
            p.setUtilizador(u);
            p.setTipo(TipoPedido.PedidoSearch);
            return p;
        }else{
            throw new IllegalArgumentException("Erro a deserializar (Tipo de pedido não especificado no Deserializer)");
        }

    }
}
