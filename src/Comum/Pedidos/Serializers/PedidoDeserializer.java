package Comum.Pedidos.Serializers;

import Comum.FilteredResult;
import Comum.Pedidos.*;
import Comum.Pedidos.Enums.TipoPedido;
import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PedidoDeserializer implements JsonDeserializer<Pedido> {
    @Override
    public Pedido deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement tipoJsonElement = jsonObject.get("tipo");
        if(tipoJsonElement == null)
            throw new JsonParseException("Não consegui deserializar o Pedido (tipo == null)");
        String tipo = tipoJsonElement.getAsString();
        Gson gson = new Gson();
        Utilizador u = gson.fromJson(jsonObject.get("utilizador"), Utilizador.class);
        Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        if(tipo.equals(TipoPedido.PedidoLogin.toString())){
            PedidoLogin p = new PedidoLogin();
            p.setUtilizador(u);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoSignUp.toString())){
            PedidoSignUp p = new PedidoSignUp();
            p.setUtilizador(u);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoUploadFile.toString())){
            PedidoUploadFile p = new PedidoUploadFile();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoDownloadFile.toString())){
            PedidoDownloadFile p = new PedidoDownloadFile();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoMusicas.toString())){
            PedidoMusicas p = new PedidoMusicas();
            p.setUtilizador(u);
            return p;
        }else if(tipo.equals(TipoPedido.PedidoSearch.toString())){

            Gson gson2 = new GsonBuilder().registerTypeAdapter(FilteredResult.class, new FilteredResultDeserializer()).create();

            PedidoSearch p = gsonExpose.fromJson(json, PedidoSearch.class);
            FilteredResult filteredResult = gson2.fromJson(jsonObject.get("filteredResult"), FilteredResult.class);
            p.setFilteredResult(filteredResult);
            p.setUtilizador(u);
            p.setTipo(TipoPedido.PedidoSearch);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoNewPlaylist.toString())){
            JsonElement nomeElement = jsonObject.get("nome");
            if(nomeElement == null) throw new JsonParseException("Deserializar PedidoNewPlaylist: nome == null");
            String nome = nomeElement.getAsString();

            PedidoNewPlaylist p = new PedidoNewPlaylist(nome);
            p.setUtilizador(u);
            return p;

        }
        else if(tipo.equals(TipoPedido.PedidoPlaylists.toString())){
            PedidoPlaylists p = new PedidoPlaylists();
            p.setUtilizador(u);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoEditSong.toString())){
            PedidoEditSong p = new PedidoEditSong();
            p.setUtilizador(u);

            Song musica = gson.fromJson(jsonObject.get("musica"), Song.class);
            p.setMusica(musica);
            return p;
        }
        else if(tipo.equals(TipoPedido.PedidoAddSong.toString())){
            JsonElement playlistElement = jsonObject.get("playlist");
            if(playlistElement == null) throw new JsonParseException("Deserializar PedidoAddSong: playlist == null");

            Playlist playlist = gsonExpose.fromJson(playlistElement, Playlist.class);
            PedidoAddSong p = gsonExpose.fromJson(json, PedidoAddSong.class);
            p.setPlaylist(playlist);
            p.setUtilizador(u);
            return p;
        }
        else{
            throw new IllegalArgumentException("Erro a deserializar (Tipo de pedido não especificado no Deserializer)");
        }

    }
}
