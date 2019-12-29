package Comum.Pedidos.Serializers;

import Comum.FilteredResult;
import Comum.Playlist;
import Comum.Song;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PlaylistDeserializer implements JsonDeserializer<Playlist> {
    @Override
    public Playlist deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement musicas = jsonObject.get("musicas");
        Gson gson = new Gson();
        Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type listType = new TypeToken<ArrayList<Song>>(){}.getType();

        Playlist playlist = gson1.fromJson(jsonElement, Playlist.class);
        ArrayList<Song> songs = gson.fromJson(musicas, listType);
        playlist.setMusicas(songs);

        return playlist;
    }
}
