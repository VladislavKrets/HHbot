package ru.hh.findjob;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.swing.text.PlainDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lollipop on 14.06.2017.
 */
public class Model extends Task<Void> {
    private MainController controller;
    private static String baseURI = "https://api.hh.ru/";
    private String accessToken, experienceGot, message, resumeId, searchWord;

    public Model(MainController controller) {
        this.controller = controller;
    }

    public void setValuables(String accessToken, String experienceGot,
                             String message, String resumeId, String searchWord) {

        this.accessToken = accessToken;
        this.experienceGot = experienceGot;
        this.message = message;
        this.resumeId = resumeId;
        this.searchWord = searchWord;
    }

    public void searchVacancies() throws IOException, InterruptedException {
        String experience = "";
        System.out.println(experienceGot);
        switch (experienceGot) {
            case "Does not matter":
                experience = "doesNotMatter";
                break;
            case "No experience":
                experience = "noExperience";
                break;
            case "1-3 years":
                experience = "between1And3";
                break;
            case "3-6 years":
                experience = "between3And6";
                break;
            case "More than 6 years":
                experience = "moreThan6";
                break;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        Date date = new Date(new Date().getTime() - 86400000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        HttpGet httpGet = new HttpGet(String.format("%svacancies?text=%s&experience=%s&date_from=%s&per_page=500",
                baseURI, searchWord, experience, dateFormat.format(date)));

        httpGet.addHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String answer = reader.readLine();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new VacancyConverter());
        Gson gson = builder.create();
        List<Vacancy> vacancies = gson.fromJson(answer, List.class);
        System.out.println(vacancies.size());

        reader.close();

        HttpPost httpPost;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        StringBuilder s;
        for (Vacancy vacancy : vacancies) {
            if (vacancy.getName().contains(searchWord) || vacancy.getName().contains(searchWord.toLowerCase())) {
                httpPost = new HttpPost(baseURI + "negotiations");
                httpPost.addHeader("Authorization", "Bearer " + accessToken);
                nameValuePairs.add(new BasicNameValuePair("vacancy_id", vacancy.getId()));
                nameValuePairs.add(new BasicNameValuePair("resume_id", resumeId));
                nameValuePairs.add(new BasicNameValuePair("message", message));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                s = new StringBuilder();
                while (reader.ready()) {
                    s.append(reader.readLine());
                }

                reader.close();
                nameValuePairs.clear();
                httpPost.reset();
                String text = s.toString();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String line = String.format("Vacancy %s%nUrl %s%n" +
                                            (text.contains("errors") ? "Already applied%n" : ""),
                                    vacancy.getName(), vacancy.getUrl());
                            byte bytes[] = line.getBytes("UTF-8");
                            String value = new String(bytes, "UTF-8");
                            controller.addListViewItem(value);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Thread.sleep(2000);
            }
        }
        httpClient.close();
    }

    @Override
    protected Void call() throws Exception {
        try {
            searchVacancies();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            controller.getStartButton().setDisable(false);
        }
        return null;
    }
}
