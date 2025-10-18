package com.example.the_autumn.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class API {

    public static void main(String[] args) {
        String apiUrl = "https://provinces.open-api.vn/api/v2/?depth=2";
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=TheAutumn2;encrypt=false;user=sa;password=123456";

        try (Connection conn = DriverManager.getConnection(connectionUrl)) {

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();
            con.disconnect();

            JSONArray provinces = new JSONArray(content.toString());

            for (int i = 0; i < provinces.length(); i++) {
                JSONObject tinh = provinces.getJSONObject(i);
                String tenTinh = tinh.getString("name");
                PreparedStatement pstTinh = conn.prepareStatement(
                        "INSERT INTO tinh_thanh (ten_tinh) OUTPUT INSERTED.id VALUES (?)"
                );
                pstTinh.setString(1, tenTinh);
                ResultSet rsTinh = pstTinh.executeQuery();
                rsTinh.next();
                int tinhId = rsTinh.getInt(1);
                rsTinh.close();
                pstTinh.close();

                JSONArray wards = tinh.getJSONArray("wards");
                for (int j = 0; j < wards.length(); j++) {
                    JSONObject ward = wards.getJSONObject(j);
                    String tenPhuong = ward.getString("name");

                    PreparedStatement pstWard = conn.prepareStatement(
                            "INSERT INTO quan_huyen (id_tinh, ten_quan) OUTPUT INSERTED.id VALUES (?, ?)"
                    );
                    pstWard.setInt(1, tinhId);
                    pstWard.setString(2, tenPhuong);
                    ResultSet rsWard = pstWard.executeQuery();
                    rsWard.next();
                    rsWard.close();
                    pstWard.close();
                }
            }

            System.out.println("✅ Import dữ liệu địa chỉ thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
