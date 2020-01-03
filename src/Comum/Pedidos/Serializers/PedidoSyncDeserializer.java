package Comum.Pedidos.Serializers;

import Comum.Pedidos.Pedido;
import Servidor.Utils.PedidoSync;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PedidoSyncDeserializer implements JsonDeserializer<PedidoSync> {
    @Override
    public PedidoSync deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonElement pedido = object.get("pedido");
        if(pedido == null)
            return new PedidoSync();

        JsonElement fileElement = object.get("file");
        String file = null;
        if(fileElement != null)
            file = new Gson().fromJson(fileElement, String.class);

        Gson gsonPedido = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();
        Pedido p = gsonPedido.fromJson(pedido, Pedido.class);
        return new PedidoSync(p, file);
    }
}
