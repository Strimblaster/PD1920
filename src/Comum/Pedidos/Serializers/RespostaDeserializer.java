package Comum.Pedidos.Serializers;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RespostaDeserializer implements JsonDeserializer<Resposta> {
    @Override
    public Resposta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();
        Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String tipo = jsonObject.get("tipoExcecao").getAsString();
        Exception exception = null;

        if(tipo.equals(TipoExcecao.InvalidPassword.toString()))
            exception = gson.fromJson(jsonElement.getAsJsonObject().get("exception"), InvalidPasswordException.class);
        else if(tipo.equals(TipoExcecao.InvalidUsername.toString()))
            exception = gson.fromJson(jsonElement.getAsJsonObject().get("exception"), InvalidUsernameException.class);


        Pedido pedido = gson.fromJson(jsonElement.getAsJsonObject().get("pedido"), Pedido.class);
        Resposta resposta = gson1.fromJson(jsonElement.getAsJsonObject(), Resposta.class);
        resposta.setPedido(pedido);
        resposta.setException(exception);

        return resposta;
    }
}
