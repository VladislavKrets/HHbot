package ru.hh.findjob;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lollipop on 13.06.2017.
 */
public class VacancyConverter implements JsonDeserializer<List<Vacancy>> {
    public List<Vacancy> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray lines = jsonElement.getAsJsonObject().get("items").getAsJsonArray();

        List<Vacancy> vacancies = new ArrayList<Vacancy>();
        JsonObject object;
        String name;
        String url;
        String id;
        for (JsonElement element : lines) {
            object = element.getAsJsonObject();
            name = object.get("name").getAsString();
            url = object.get("alternate_url").getAsString();
            id = object.get("id").getAsString();
            vacancies.add(new Vacancy(name, url, id));
        }
        return vacancies;
    }
}
