package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.config.FaceIdConfig;
import com.axelor.apps.pbproject.service.FaceIdService;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import wslite.json.JSONException;
import wslite.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

public class FaceIdServiceImpl implements FaceIdService {
    private final UserRepository userRepository;

    @Inject
    public FaceIdServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void uploadUserToFaceId(User user) {
        String uuid = generateUUID();
        user.setFaceUuid(uuid);

        String accessToken = getAccessToken();

        String response = createUserInFaceId(user, accessToken);

        if (response != null) {
            userRepository.save(user);
        }
    }

    private String createUserInFaceId(User user, String accessToken) {
        try {
            HttpURLConnection connection = setupHttpConnection(FaceIdConfig.FACE_ID_URL_USER_CREATE, "POST", accessToken);

            JSONObject userJson = createUserJson(user);

            sendRequest(connection, userJson);

            return getResponse(connection);

        } catch (IOException | JSONException e) {
            throw new RuntimeException("Error while creating user in FaceID: " + e.getMessage(), e);
        }
    }

    private HttpURLConnection setupHttpConnection(String urlString, String requestMethod, String accessToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject createUserJson(User user) throws JSONException, UnsupportedEncodingException {
        String fullName = user.getFullName();
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        JSONObject userJson = new JSONObject();
        userJson.put("username", user.getCode());
        userJson.put("email", user.getEmail());
        userJson.put("first_name", firstName);
        userJson.put("last_name", lastName);
        userJson.put("uuid", user.getFaceUuid());
        userJson.put("image", user.getImage() != null ? new String(user.getImage(), "UTF-8") : null);

        return userJson;
    }

    private void sendRequest(HttpURLConnection connection, JSONObject jsonBody) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.toString().getBytes("utf-8"));
        }
    }

    private String getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining());
        } finally {
            connection.disconnect();
        }
    }


    private String getAccessToken() {
        try {
            HttpURLConnection connection = setupHttpConnection(FaceIdConfig.FACE_ID_URL_TOKEN, "POST", null);

            JSONObject credentialsJson = new JSONObject();
            credentialsJson.put("username", FaceIdConfig.FACE_ID_LOGIN);
            credentialsJson.put("password", FaceIdConfig.FACE_ID_PASSWORD);

            sendRequest(connection, credentialsJson);
            String response = getResponse(connection);

            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("access");
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Error while getting access token: " + e.getMessage(), e);
        }
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }
}

