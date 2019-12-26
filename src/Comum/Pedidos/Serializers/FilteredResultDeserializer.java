package Comum.Pedidos.Serializers;

import Comum.FilteredResult;
import Comum.Playlist;
import Comum.Song;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FilteredResultDeserializer implements JsonDeserializer<FilteredResult> {
    @Override
    public FilteredResult deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new Gson();
        Type s = new TypeToken<ArrayList<Song>>(){}.getType();
        ArrayList<Song> songs = gson.fromJson(jsonObject.get("songs"), s);

        Type p = new TypeToken<ArrayList<Playlist>>(){}.getType();
        ArrayList<Playlist> playlists = gson.fromJson(jsonObject.get("playlists"), p);

        return new FilteredResult(songs,playlists);

    }
}
