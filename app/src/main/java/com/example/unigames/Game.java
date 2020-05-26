package com.example.unigames;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un juego de la API que estamos usando
 */
public class Game { //clase que contiene información de un videojuego. el json recibido de la api se convierte en este objeto.
    @SerializedName("id")
    private String id; // identificador del juego en la API
    @SerializedName("name")
    private String name;               // nombre del juego en la API
    @SerializedName("released")
    private String released;            // fecha en la que el juego fue, o va a ser, lanzado al público
    @SerializedName("background_image")
    private String background_image;    // Imagen de fondo del juego, suele ser bastante representativa del juego
    @SerializedName("rating")
    private String rating;              // puntuación del juego (sobre 5).
    @SerializedName("playtime")
    private int playtime;            // Tiempo esperado de juego
    @SerializedName("description")
    private String description;         // Pequeña descripción del juego, viene con etiquetas HTML, que TENEMOS que quitar
    @SerializedName("platforms")
    private List<JSONObject> platforms;
    @SerializedName("developers")
    private List<JSONObject> developers;
    @SerializedName("publishers")
    private List<JSONObject> publishers;
    @SerializedName("esrb_rating")
    private JSONObject ESRBRating;
    @SerializedName("trailer")
    private String trailer;

    public Game() {
    }

    /**
     * Constructor de la clase que iniciará cada uno de los parámetros
     * @param id
     * @param name
     * @param released
     * @param rating
     * @param background_image
     * @param description
     */
    public Game(String id, String name, String released, String rating, String background_image, String description, int tiempoDeJuego) throws ParseException {
        this.id = id;
        this.name = name;
        this.released = released;
        this.rating = Double.toString(Double.parseDouble(rating)*2);
        this.background_image = background_image;
        this.description = description;
        this.playtime = tiempoDeJuego;
    }

    // SETTERS Y GETTERS DE TODOS LOS PARÁMETROS (MENOS SET DE ID, QUE AL TOMARSE COMO CLAVE NO NOS PARECE CORRECTO QUE TENGA SET
    public String getName(){
        return this.name;
    }

    public void setName(String name) { this.name = name; }

    public String getReleased() { return released; }

    public String getRating() {
        double rat = Double.parseDouble(this.rating);
        rat = rat*2;
        return String.valueOf(rat);
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUrlBackground_image() {
        return background_image;
    }

    public void setUrlBackground_image(String urlBackground_image) {
        this.background_image = urlBackground_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public int getPlaytime(){ return this.playtime;}

    public String getDeveloper() throws JSONException {
        if(developers.size()==0){
            return "unknown";
        }
        return this.developers.get(this.developers.size()-1).getString("name");
    }

    public void setPlatforms(List<JSONObject> plataformas){
        this.platforms = plataformas;
    }

    public List<String> getPlatforms() throws JSONException {
        List<String> plat = new ArrayList<>();
        for(int i=0; i<platforms.size(); i++){
            plat.add(platforms.get(i).getJSONObject("platform").getString("name"));
        }
        return plat;
    }

    public void setPublishers(ArrayList<JSONObject> pub){
        this.publishers=pub;
    }

    public String getPublisher() throws JSONException {
        if(publishers.size()==0){
            return "unknown";
        }
        return this.publishers.get(0).getString("name");
    }



    public String getESRBRating() throws JSONException {
        if(this.ESRBRating==null){
            return "unknown";
        }
        return this.ESRBRating.getString("name");
    }

    public void setTrailer(String tr){
        this.trailer = tr;
    }

    public String getTrailer(){
        return this.trailer;
    }

    public void setDevelopers(ArrayList<JSONObject> developers) {
        this.developers = developers;
    }

    public void setESRBRating(JSONObject esrb) {
        this.ESRBRating = esrb;
    }
}
