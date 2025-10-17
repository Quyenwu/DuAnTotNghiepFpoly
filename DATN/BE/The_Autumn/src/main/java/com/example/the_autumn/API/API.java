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
        String apiUrl = "https://provinces.open-api.vn/api/?depth=3";
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=TheAutumn;encrypt=false;user=sa;password=nguyen123";

        try (Connection conn = DriverManager.getConnection(connectionUrl)) {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();
            con.disconnect();

            JSONArray provinces = new JSONArray(content.toString());

            for (int i = 0; i < provinces.length(); i++) {
                JSONObject tinh = provinces.getJSONObject(i);
                String tenTinh = tinh.getString("name");

                PreparedStatement pstTinh = conn.prepareStatement(
                        "INSERT INTO tinh_thanh (ten_tinh) OUTPUT INSERTED.id VALUES (?)");
                pstTinh.setString(1, tenTinh);
                ResultSet rsTinh = pstTinh.executeQuery();
                rsTinh.next();
                int tinhId = rsTinh.getInt(1);
                rsTinh.close();
                pstTinh.close();

                JSONArray districts = tinh.getJSONArray("districts");
                for (int j = 0; j < districts.length(); j++) {
                    JSONObject quan = districts.getJSONObject(j);
                    String tenQuan = quan.getString("name");

                    PreparedStatement pstQuan = conn.prepareStatement(
                            "INSERT INTO quan_huyen (id_tinh, ten_quan) OUTPUT INSERTED.id VALUES (?, ?)");
                    pstQuan.setInt(1, tinhId);
                    pstQuan.setString(2, tenQuan);
                    ResultSet rsQuan = pstQuan.executeQuery();
                    rsQuan.next();
                    int quanId = rsQuan.getInt(1);
                    rsQuan.close();
                    pstQuan.close();

                    JSONArray wards = quan.getJSONArray("wards");
                    for (int k = 0; k < wards.length(); k++) {
                        JSONObject xa = wards.getJSONObject(k);
                        String tenXa = xa.getString("name");

                        PreparedStatement pstXa = conn.prepareStatement(
                                "INSERT INTO xa_phuong (id_quan, ten_xa) VALUES (?, ?)");
                        pstXa.setInt(1, quanId);
                        pstXa.setString(2, tenXa);
                        pstXa.executeUpdate();
                        pstXa.close();
                    }
                }
            }

            System.out.println("✅ Import dữ liệu địa chỉ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
