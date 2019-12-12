package Comum.Pedidos.Serializers;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.TipoPedido;
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

        throw new IllegalArgumentException("Erro a deserializar");
    }
}
