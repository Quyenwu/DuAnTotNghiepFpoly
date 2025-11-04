package com.example.the_autumn.entity;

public class ChatMessage {
    private String sender;
    private String content;
    private String role;  // "admin" hoặc "customer"
    private String roomId; // dùng để phân biệt cuộc trò chuyện

    // getters & setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
