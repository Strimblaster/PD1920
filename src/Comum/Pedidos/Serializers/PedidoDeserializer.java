package Comum.Pedidos.Serializers;

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
        String tipo = jsonObject.get("tipo").getAsString();
        Gson gson = new Gson();
        Utilizador u = gson.fromJson(jsonObject.get("utilizador"), Utilizador.class);

        if(tipo.equals(TipoPedido.PedidoLogin.toString())){
            PedidoLogin p = new PedidoLogin();
            p.setUtilizador(u);
            return p;
        }

        if(tipo.equals(TipoPedido.PedidoSignUp.toString())){
            PedidoSignUp p = new PedidoSignUp();
            p.setUtilizador(u);
            return p;
        }

        if(tipo.equals(TipoPedido.PedidoUploadFile.toString())){
            PedidoUploadFile p = new PedidoUploadFile();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }

        if(tipo.equals(TipoPedido.PedidoMusicas.toString())){
            PedidoMusicas p = new PedidoMusicas();
            p.setUtilizador(u);
            return p;
        }

        throw new IllegalArgumentException("Erro a deserializar");
    }
}
