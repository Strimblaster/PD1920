package Comum.Pedidos.Serializers;

import Comum.Pedidos.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RespostaDeserializer implements JsonDeserializer<Resposta> {
    @Override
    public Resposta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();
        Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Pedido pedido = gson.fromJson(jsonElement.getAsJsonObject().get("pedido"), Pedido.class);
        Resposta resposta = gson1.fromJson(jsonElement.getAsJsonObject(), Resposta.class);
        resposta.setPedido(pedido);

        return resposta;
    }
}
