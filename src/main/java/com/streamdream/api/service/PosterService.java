package com.streamdream.api.service;

import com.streamdream.api.model.MediaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PosterService {

    private final String API_KEY = "72929168";
    private final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

    public MediaDTO fetchMovieFromApi(String title) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = API_URL + URLEncoder.encode(title, StandardCharsets.UTF_8);

            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && "True".equals(result.get("Response"))) {
                MediaDTO dto = new MediaDTO();
                dto.setTitle((String) result.get("Title"));
                dto.setGenre(((String) result.get("Genre")).split(",")[0]);
                dto.setPosterUrl((String) result.get("Poster"));
                dto.setDescription((String) result.get("Plot"));
                String yearRaw = (String) result.get("Year");
                dto.setReleaseYear(Integer.parseInt(yearRaw.substring(0, 4)));
                return dto;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}