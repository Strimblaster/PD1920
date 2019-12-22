package Comum.Pedidos.Serializers;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.*;
import Comum.Pedidos.Enums.TipoExcecao;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RespostaDeserializer implements JsonDeserializer<Resposta> {
    @Override
    public Resposta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        //Deserializar a resposta por partes. Por isso é que é preciso varios gson.

        //Para deserializar apenas o Pedido dentro da resposta
        Gson gson = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();
        //Para deserializar todos os atrivutos que ão tenham @Expose na classe (basicamente todos menos o Pedido)
        Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //Verificar se há uma exceção na resposta, assim precisa de ser deserializada à parte também.
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement tipoExcecao = jsonObject.get("tipoExcecao");
        Exception exception = null;
        if(tipoExcecao != null){
            String tipo = tipoExcecao.getAsString();
            Exception temp = new Exception(jsonElement.getAsJsonObject().get("exception").getAsJsonObject().get("message").getAsString());

            if(tipo.equals(TipoExcecao.InvalidPassword.toString()))
                exception = new InvalidPasswordException(temp.getMessage());
            else if(tipo.equals(TipoExcecao.InvalidUsername.toString()))
                exception = new InvalidUsernameException(temp.getMessage());
            else if(tipo.equals(TipoExcecao.InvalidServer.toString()))
                exception = new InvalidServerException();
            else if(tipo.equals(TipoExcecao.InvalidSongDescription.toString()))
                exception = new InvalidSongDescriptionException(temp.getMessage());
            else
                exception = temp;

        }

        //Deserializa o Pedido
        Pedido pedido = gson.fromJson(jsonElement.getAsJsonObject().get("pedido"), Pedido.class);
        //Deserializa a Resposta menos o Pedido e a Exceção
        Resposta resposta = gson1.fromJson(jsonElement.getAsJsonObject(), Resposta.class);

        resposta.setPedido(pedido);
        resposta.setException(exception);

        return resposta;
    }
}
