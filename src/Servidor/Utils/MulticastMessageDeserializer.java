package Servidor.Utils;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Comum.ServerInfo;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MulticastMessageDeserializer implements JsonDeserializer<MulticastMessage> {
    @Override
    public MulticastMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new Gson();
        Gson gsonPedido = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();
        JsonObject multicastMessageObject = jsonElement.getAsJsonObject();

        //Trata do objeto ServerInfo sender que está dentro da classe MulticastMessage
        String senderJson = multicastMessageObject.get("sender").getAsString();
        ServerInfo sender = gson.fromJson(senderJson, ServerInfo.class);

        //Obtem o json dos objetos pedido e receiver
        JsonElement pedidoJsonElement = multicastMessageObject.get("pedido");
        JsonElement receiverJsonElement = multicastMessageObject.get("receiver");

        //Verifica se há algum pedido serializado em json, se não houve é porque é um servidor novo a pedir a info toda.
        //Se houver deserializa o pedido
        if(pedidoJsonElement == null)
            return new MulticastMessage(sender);
        Pedido pedido = gsonPedido.fromJson(pedidoJsonElement, Pedido.class);

        //Verifica se ha alguma receiver serializado no json. Se não houver é um pedido para todos. Se houver deserializa e retorna tudo
        if(receiverJsonElement == null)
            return new MulticastMessage(sender, pedido);
        ServerInfo receiver = gson.fromJson(receiverJsonElement, ServerInfo.class);

        return new MulticastMessage(sender, receiver, pedido);
    }
}
