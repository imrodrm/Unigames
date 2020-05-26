package com.example.unigames;

import android.os.StrictMode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Clase a la que recurriremos cada vez que necesitemos hacer una nueva petición a la API.
 */
public class UnigamesApi {

    /**
     * Este parámetro es necesario para crear nuestras peticiones a la API (se puede hacer de esta forma o de otra, pero esta nos pareció mejor).
     */
    private OkHttpClient client;

    /**
     * Constructor de la clase, inicializa la política de creación de peticiones e instancia el cliente.
     */
    public UnigamesApi(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.client = new OkHttpClient();
    }

    /**
     * Este método coge los juegos más populares desde el día 1 de el mes anterior al actual, hasta el primer día
     * 2 meses después del mes actual. Coge los 20 juegos más populares del momento.
     * @return Lista de juegos más populares del momento
     */
    public List<Game> getPopularGames(){

        //Esta será la lista de juegos sobre la que vayamos añadiendo los distintos juegos que traigamos
        List<Game> gamesList = null;
        List<Game> enums = null;
        try{
            //Todo este fragmento es para sacar las fechas para las cuales queremos los juegos más populares
            Calendar mesPasado = Calendar.getInstance();
            mesPasado.set(Calendar.DAY_OF_MONTH, 1);
            Calendar proximoMes = Calendar.getInstance();
            proximoMes.add(Calendar.MONTH, 3);
            int diasaniadir = this.cuantosDiasTieneElMes(proximoMes.get(Calendar.MONTH), proximoMes.get(Calendar.YEAR)) - proximoMes.get(Calendar.DAY_OF_MONTH);
            proximoMes.add(Calendar.DAY_OF_MONTH, diasaniadir);
            //PETICIÓN ------------------------------
            String url = "https://rawg-video-games-database.p.rapidapi.com/games?dates=" + Integer.toString(mesPasado.get(Calendar.YEAR)) + "-" + this.convertirFecha(mesPasado.get(Calendar.MONTH)) + "-" + this.convertirFecha(mesPasado.get(Calendar.DAY_OF_MONTH)) + "," + Integer.toString(proximoMes.get(Calendar.YEAR)) + "-" + this.convertirFecha(proximoMes.get(Calendar.MONTH)) + "-" + this.convertirFecha(proximoMes.get(Calendar.DAY_OF_MONTH));

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            // Aquí estamos cogiendo el cuerpo de la respuesta, que es dónde está el json con el resultado de la petición, pero que viene dado como una string
            // Para parsearlo, lo pasamos a un JSONObject
            Response response = client.newCall(request).execute();
            String informacionDeLosJuegosJson =  response.body().string().toString();//saca el cuerpo de la petición, que tiene los datos de los juegos.
            JSONObject jsonJuegos = new JSONObject(informacionDeLosJuegosJson); //almaceno el json de los juegos.

            //---------------------------------------------

            // results es un array dentro del propio JSON que es dónde está la información de cada juego metida, por lo que creamos una string que será ese vector, pero en cadena de caracteres.
            String infojuegosEnArrayJson = jsonJuegos.getString("results");
            // Aquí lo que hacemos es parsear cada array de la string (que, recordemos, es un JSON) como un juego, y añadirlo a la lista que devolveremos
            Gson gson = new GsonBuilder().create();
//            Game[] gamesArray = gson.fromJson(infojuegosEnArrayJson, Game[].class);
            Type collectionType = new TypeToken<Collection<Game>>(){}.getType();
            enums = gson.fromJson(infojuegosEnArrayJson, collectionType);
//            gamesList = new ArrayList<Game>();
//            for(int i = 0; i<gamesArray.length;i++){ // traslado lo del array a una lista para poder devolverlo en el método.
            //              gamesList.add(gamesArray[i]);
            //}
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
       return enums;
    }

    /**
     * Este método va a la API y coge toda la info de un juego
     * @param id del juego en cuestión en la API
     * @return un juego con toda la información para su página
     */
    public Game getInfoFromAGame(String id){
        try{
            //PETICIÓN ------------------------------
            String nulo = "null";
            String url = "https://rawg-video-games-database.p.rapidapi.com/games/" + id;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String infoDelJuego =  response.body().string(); //saca el cuerpo de la petición, que tiene los datos del juego.
            JSONObject jsonJuego = new JSONObject(infoDelJuego); //almaceno el json de los juegos.

            //---------------------------------------------
            //No hace falta hacer pasar por el GSON al JSONObject ya que podemos acceder a sus valores por sus claves
            Game jueguico;
            Gson gson = new GsonBuilder().create();
            jueguico = gson.fromJson(String.valueOf(jsonJuego), Game.class);

            JSONArray arrayPlataformas = jsonJuego.getJSONArray("platforms");
            ArrayList<JSONObject> plataformas= new ArrayList<>();
            for(int i=0;i<arrayPlataformas.length(); i++){
                plataformas.add(arrayPlataformas.getJSONObject(i));
            }
            jueguico.setPlatforms(plataformas);
            JSONArray arrayDevelopers = jsonJuego.getJSONArray("developers");
            ArrayList<JSONObject> developers= new ArrayList<>();
            for(int i=0;i<arrayDevelopers.length(); i++){
                developers.add(arrayDevelopers.getJSONObject(i));
            }
            jueguico.setDevelopers(developers);
            JSONArray arrayPublishers = jsonJuego.getJSONArray("publishers");
            ArrayList<JSONObject> publishers= new ArrayList<>();
            for(int i=0;i<arrayPublishers.length(); i++){
                publishers.add(arrayPublishers.getJSONObject(i));
            }
            if(jsonJuego.isNull("esrb_rating")){
                jueguico.setESRBRating(null);
            }
            else{
                jueguico.setESRBRating(jsonJuego.getJSONObject("esrb_rating"));
            }
            //if (jsonJuego.)
            jueguico.setPublishers(publishers);
            //Game juego = new Game(idJuego, name, released, rating, jsonJuego.getString("background_image"), jsonJuego.getString("description"), jsonJuego.getInt("playtime"), jsonJuego.getJSONArray("developers"), jsonJuego.getJSONArray("publishers"), plataformas, esrb, jsonJuego.getJSONObject("clip"));
            //Game juego = new Game(jsonJuego.getString("id"), jsonJuego.getString("name"), jsonJuego.getString("released"), jsonJuego.getString("rating"), jsonJuego.getString("background_image"), jsonJuego.getString("description"));
            return jueguico;
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        } /*catch (ParseException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    /**
     * Devuelve cuantos días tiene un mes dependiendo del mes y el año
     * @param mes
     * @param anio
     * @return número de días de un mes dependiendo de mes y año
     */
    private int cuantosDiasTieneElMes(int mes, int anio){
        if (mes==1 || mes==3 || mes==5 || mes==7 || mes==8 || mes==10 || mes==12){
            return 31;
        }
        else if(mes==4 || mes==6 || mes==9 || mes==11){
            return 30;
        }
        else if(anio%400==0){
            return 29;
        }
        else if(anio%100==0){
            return 28;
        }
        else if(anio%4==0){
            return 29;
        }
        return 0;
    }

    public List<Game> getVisuallySimmilarGames(String id){
        List<Game> enums = null;
        try{
            //PETICIÓN ------------------------------

            String url = "https://rawg-video-games-database.p.rapidapi.com/games/" + id + "/suggested"; //game-series

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String infoDelJuego =  response.body().string(); //saca el cuerpo de la petición, que tiene los datos del juego.
            JSONObject juegosParecidos = new JSONObject(infoDelJuego); //almaceno el json de los juegos.
            //---------------------------------------------
            String primerTrailer = juegosParecidos.getString("results");
            //No hace falta hacer pasar por el GSON al JSONObject ya que podemos acceder a sus valores por sus claves
            Gson gson = new GsonBuilder().create();
//            Game[] gamesArray = gson.fromJson(infojuegosEnArrayJson, Game[].class);
            Type collectionType = new TypeToken<List<Game>>(){}.getType();
            enums = gson.fromJson(primerTrailer, collectionType);
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return enums;
    }

    public void getTrailerFromAGame(Game juego){
        try{
            //PETICIÓN ------------------------------

            String url = "https://rawg-video-games-database.p.rapidapi.com/games/" + juego.getId() + "/youtube";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String infoDelJuego =  response.body().string(); //saca el cuerpo de la petición, que tiene los datos del juego.
            JSONObject jsonJuego = new JSONObject(infoDelJuego); //almaceno el json de los juegos.
            //---------------------------------------------
            JSONArray primerTrailer = jsonJuego.getJSONArray("results");
            //No hace falta hacer pasar por el GSON al JSONObject ya que podemos acceder a sus valores por sus claves
            if(primerTrailer.length()>0){
                juego.setTrailer(primerTrailer.getJSONObject(0).getString("external_id"));
            } else{
                juego.setTrailer("");
            }

        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getTwitchFromAGame(Game juego){
        try{
            //PETICIÓN ------------------------------

            String url = "https://rawg-video-games-database.p.rapidapi.com/games/" + juego.getId() + "/twitch";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String infoDelJuego =  response.body().string(); //saca el cuerpo de la petición, que tiene los datos del juego.
            JSONObject jsonJuego = new JSONObject(infoDelJuego); //almaceno el json de los juegos.
            //---------------------------------------------
            JSONArray primerTrailer = jsonJuego.getJSONArray("results");
            //No hace falta hacer pasar por el GSON al JSONObject ya que podemos acceder a sus valores por sus claves
            if(primerTrailer.length()>0){
                juego.setTrailer(primerTrailer.getJSONObject(0).getString("external_id"));
            } else{
                juego.setTrailer("");
            }

        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public List<Game> getSeriesGames(String id){
        List<Game> enums = null;
        try{
            //PETICIÓN ------------------------------

            String url = "https://rawg-video-games-database.p.rapidapi.com/games/" + id + "/game-series?page_size=2"; //game-series

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String infoDelJuego =  response.body().string(); //saca el cuerpo de la petición, que tiene los datos del juego.
            JSONObject juegosParecidos = new JSONObject(infoDelJuego); //almaceno el json de los juegos.
            //---------------------------------------------
            String primerTrailer = juegosParecidos.getString("results");
            //No hace falta hacer pasar por el GSON al JSONObject ya que podemos acceder a sus valores por sus claves
            Gson gson = new GsonBuilder().create();
//            Game[] gamesArray = gson.fromJson(infojuegosEnArrayJson, Game[].class);
            Type collectionType = new TypeToken<List<Game>>(){}.getType();
            enums = gson.fromJson(primerTrailer, collectionType);
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return enums;
    }

    /**
     * Método necesario para hacer peticiones por fecha a la API, ya que los números con un solo dígito han de ir precedidos por un 0
     * @param fech fecha que queremos parsear
     * @return fecha con un formato amigable para la API
     */
    private String convertirFecha(int fech){
        if (fech>=10){
            return Integer.toString(fech);
        }
        else{
            return "0" + Integer.toString(fech);
        }
    }

    public List<Game> searchGames(String busqueda){

        //Esta será la lista de juegos sobre la que vayamos añadiendo los distintos juegos que traigamos
        List<Game> gamesList = null;
        List<Game> enums = null;
        try{
            //Todo este fragmento es para sacar las fechas para las cuales queremos los juegos más populares
            String busquedaJuego = busqueda.replaceAll(" ", "-").toLowerCase();
            String url = "https://rawg-video-games-database.p.rapidapi.com/games?search=" + busquedaJuego;
            url.replace(" ", "-");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            // Aquí estamos cogiendo el cuerpo de la respuesta, que es dónde está el json con el resultado de la petición, pero que viene dado como una string
            // Para parsearlo, lo pasamos a un JSONObject
            Response response = client.newCall(request).execute();
            String informacionDeLosJuegosJson =  response.body().string().toString();//saca el cuerpo de la petición, que tiene los datos de los juegos.
            JSONObject jsonJuegos = new JSONObject(informacionDeLosJuegosJson); //almaceno el json de los juegos.

            //---------------------------------------------

            // results es un array dentro del propio JSON que es dónde está la información de cada juego metida, por lo que creamos una string que será ese vector, pero en cadena de caracteres.
            String infojuegosEnArrayJson = jsonJuegos.getString("results");
            // Aquí lo que hacemos es parsear cada array de la string (que, recordemos, es un JSON) como un juego, y añadirlo a la lista que devolveremos
            Gson gson = new GsonBuilder().create();
            Type collectionType = new TypeToken<List<Game>>(){}.getType();
            enums = gson.fromJson(infojuegosEnArrayJson, collectionType);
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return enums;
    }

    public List<Game> platforms(String busqueda){

        Calendar mesPasado = Calendar.getInstance();
        mesPasado.set(Calendar.DAY_OF_MONTH, 1);
        mesPasado.add(Calendar.MONTH, -6);
        Calendar proximoMes = Calendar.getInstance();
        proximoMes.add(Calendar.MONTH, 6);
        int diasaniadir = this.cuantosDiasTieneElMes(proximoMes.get(Calendar.MONTH), proximoMes.get(Calendar.YEAR)) - proximoMes.get(Calendar.DAY_OF_MONTH);
        proximoMes.add(Calendar.DAY_OF_MONTH, diasaniadir);


        //Esta será la lista de juegos sobre la que vayamos añadiendo los distintos juegos que traigamos
        List<Game> gamesList = null;
        List<Game> enums = null;
        try{
            //Todo este fragmento es para sacar las fechas para las cuales queremos los juegos más populares
            String busquedaJuego = busqueda.replaceAll(" ", "-").toLowerCase();
            String url = "https://rawg-video-games-database.p.rapidapi.com/games?platforms=" + busquedaJuego + "&platforms_count=2" + "&page_size=39" +  "&dates=" + Integer.toString(mesPasado.get(Calendar.YEAR)) + "-" + this.convertirFecha(mesPasado.get(Calendar.MONTH)) + "-" + this.convertirFecha(mesPasado.get(Calendar.DAY_OF_MONTH)) + "," + Integer.toString(proximoMes.get(Calendar.YEAR)) + "-" + this.convertirFecha(proximoMes.get(Calendar.MONTH)) + "-" + this.convertirFecha(proximoMes.get(Calendar.DAY_OF_MONTH));
            url.replace(" ", "-");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            // Aquí estamos cogiendo el cuerpo de la respuesta, que es dónde está el json con el resultado de la petición, pero que viene dado como una string
            // Para parsearlo, lo pasamos a un JSONObject
            Response response = client.newCall(request).execute();
            String informacionDeLosJuegosJson =  response.body().string().toString();//saca el cuerpo de la petición, que tiene los datos de los juegos.
            JSONObject jsonJuegos = new JSONObject(informacionDeLosJuegosJson); //almaceno el json de los juegos.
            //---------------------------------------------

            // results es un array dentro del propio JSON que es dónde está la información de cada juego metida, por lo que creamos una string que será ese vector, pero en cadena de caracteres.
            String infojuegosEnArrayJson = jsonJuegos.getString("results");
            // Aquí lo que hacemos es parsear cada array de la string (que, recordemos, es un JSON) como un juego, y añadirlo a la lista que devolveremos
            Gson gson = new GsonBuilder().create();
            Type collectionType = new TypeToken<List<Game>>(){}.getType();
            enums = gson.fromJson(infojuegosEnArrayJson, collectionType);
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return enums;
    }

    public List<Game> platformsUna(String busqueda){

        Calendar mesPasado = Calendar.getInstance();
        mesPasado.set(Calendar.DAY_OF_MONTH, 1);
        mesPasado.add(Calendar.MONTH, -6);
        Calendar proximoMes = Calendar.getInstance();
        proximoMes.add(Calendar.MONTH, 6);
        int diasaniadir = this.cuantosDiasTieneElMes(proximoMes.get(Calendar.MONTH), proximoMes.get(Calendar.YEAR)) - proximoMes.get(Calendar.DAY_OF_MONTH);
        proximoMes.add(Calendar.DAY_OF_MONTH, diasaniadir);


        //Esta será la lista de juegos sobre la que vayamos añadiendo los distintos juegos que traigamos
        List<Game> gamesList = null;
        List<Game> enums = null;
        try{
            //Todo este fragmento es para sacar las fechas para las cuales queremos los juegos más populares
            String busquedaJuego = busqueda.replaceAll(" ", "-").toLowerCase();
            String url = "https://rawg-video-games-database.p.rapidapi.com/games?platforms=" + busquedaJuego + "&platforms_count=1" + "&page_size=39" +  "&dates=" + Integer.toString(mesPasado.get(Calendar.YEAR)) + "-" + this.convertirFecha(mesPasado.get(Calendar.MONTH)) + "-" + this.convertirFecha(mesPasado.get(Calendar.DAY_OF_MONTH)) + "," + Integer.toString(proximoMes.get(Calendar.YEAR)) + "-" + this.convertirFecha(proximoMes.get(Calendar.MONTH)) + "-" + this.convertirFecha(proximoMes.get(Calendar.DAY_OF_MONTH));
            url.replace(" ", "-");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            // Aquí estamos cogiendo el cuerpo de la respuesta, que es dónde está el json con el resultado de la petición, pero que viene dado como una string
            // Para parsearlo, lo pasamos a un JSONObject
            Response response = client.newCall(request).execute();
            String informacionDeLosJuegosJson =  response.body().string().toString();//saca el cuerpo de la petición, que tiene los datos de los juegos.
            JSONObject jsonJuegos = new JSONObject(informacionDeLosJuegosJson); //almaceno el json de los juegos.
            //---------------------------------------------

            // results es un array dentro del propio JSON que es dónde está la información de cada juego metida, por lo que creamos una string que será ese vector, pero en cadena de caracteres.
            String infojuegosEnArrayJson = jsonJuegos.getString("results");
            // Aquí lo que hacemos es parsear cada array de la string (que, recordemos, es un JSON) como un juego, y añadirlo a la lista que devolveremos
            Gson gson = new GsonBuilder().create();
            Type collectionType = new TypeToken<List<Game>>(){}.getType();
            enums = gson.fromJson(infojuegosEnArrayJson, collectionType);
        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return enums;
    }

    public List<Game> getGames(){

        List<Game> gamesList = null;

        try{
            //PETICIÓN ------------------------------
            Request request = new Request.Builder()
                    .url("https://rawg-video-games-database.p.rapidapi.com/games")
                    .get()
                    .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "65cde4890dmsh1ede24bd5d76c14p13aeb1jsn2b519c2b8ef5")
                    .build();
            Response response = client.newCall(request).execute();
            String informacionDeLosJuegosJson =  response.body().string().toString();//saca el cuerpo de la petición, que tiene los datos de los juegos.
            JSONObject jsonJuegos = new JSONObject(informacionDeLosJuegosJson); //almaceno el json de los juegos.

            //---------------------------------------------

            //En el json viene info como la url de la siguiente página y la info de los juegos vienen en un array dentro del json así que saco otro json con el array de los juegos.
            String infojuegosEnArrayJson = jsonJuegos.getString("results").toString();
            Gson gson = new Gson();
            Game[] gamesArray = gson.fromJson(infojuegosEnArrayJson, Game[].class);
            gamesList = new ArrayList<Game>();
            for(int i = 0; i<gamesArray.length;i++){ // traslado lo del array a una lista para poder devolverlo en el método.
                gamesList.add(gamesArray[i]);
            }


        }catch(IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }


        return gamesList;

    }
}