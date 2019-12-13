package Comum.Pedidos.Serializers;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ExceptionSerializer implements JsonSerializer<Exception> {


    @Override
    public JsonElement serialize(Exception e, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cause", new JsonPrimitive(String.valueOf(e.getCause())));
        jsonObject.add("message", new JsonPrimitive(e.getMessage()));
        return jsonObject;
    }
}