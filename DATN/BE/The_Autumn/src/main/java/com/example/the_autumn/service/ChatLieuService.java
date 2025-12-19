package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChatLieu;
import com.example.the_autumn.entity.MauSac;
import com.example.the_autumn.model.request.ChatLieuRequest;
import com.example.the_autumn.model.response.ChatLieuResponse;
import com.example.the_autumn.model.response.MauSacResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.ChatLieuRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepo;

    public List<ChatLieuResponse> findAll() {
        return chatLieuRepo.findAll()
                .stream()
                .map(ChatLieuResponse::new)
                .toList();
    }

    public ChatLieuResponse detail(Integer id){
        ChatLieu chatLieu = chatLieuRepo.findById(id).get();
        return new ChatLieuResponse(chatLieu);
    }

    public void add(ChatLieuRequest request) {
        ChatLieu chatLieu = MapperUtils.map(request, ChatLieu.class);
        chatLieu.setTrangThai(true);
        chatLieuRepo.save(chatLieu);
    }

    public void update(Integer id, ChatLieuRequest request){
        ChatLieu chatLieu = chatLieuRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu"));
        MapperUtils.mapToExisting(request, chatLieu);
        chatLieuRepo.save(chatLieu);
    }


    public List<ChatLieuResponse> findByName(String name) {
        return chatLieuRepo.findByTenChatLieuContainingIgnoreCase(name)
                .stream()
                .map(ChatLieuResponse::new)
                .collect(Collectors.toList());
    }

    public List<ChatLieuResponse> findByName2(String name) {
        return chatLieuRepo.findByNameContaining(name)
                .stream()
                .map(ChatLieuResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<ChatLieuResponse> filterChatLieuWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maChatLieu,
            String tenChatLieu,
            Date ngayTao,
            Boolean trangThai) {

        List<ChatLieuResponse> filteredList = filterChatLieu(
                searchText, maChatLieu, tenChatLieu, ngayTao, trangThai
        );


        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<ChatLieuResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();


        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ChatLieuResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<ChatLieuResponse> filterChatLieu(
            String searchText,
            String maChatLieu,
            String tenChatLieu,
            Date ngayTao,
            Boolean trangThai) {

        List<ChatLieu> list = chatLieuRepo.findAll();

        return list.stream()
                .filter(cl -> searchText == null || searchText.isEmpty() ||
                        (cl.getMaChatLieu() != null && cl.getMaChatLieu().toLowerCase().contains(searchText.toLowerCase())) ||
                        (cl.getTenChatLieu() != null && cl.getTenChatLieu().toLowerCase().contains(searchText.toLowerCase())))

                .filter(cl -> maChatLieu == null || maChatLieu.isEmpty() ||
                        (cl.getMaChatLieu() != null && cl.getMaChatLieu().toLowerCase().contains(maChatLieu.toLowerCase())))

                .filter(cl -> tenChatLieu == null || tenChatLieu.isEmpty() ||
                        (cl.getTenChatLieu() != null && cl.getTenChatLieu().toLowerCase().contains(tenChatLieu.toLowerCase())))

                .filter(cl -> ngayTao == null ||
                        (cl.getNgayTao() != null && !cl.getNgayTao().before(ngayTao)))

                .filter(cl -> trangThai == null ||
                        (cl.getTrangThai() != null && cl.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(ChatLieuResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        ChatLieu cl = chatLieuRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + id));

        cl.setTrangThai(trangThai);
        cl.setNgaySua(new Date());

        chatLieuRepo.save(cl);
    }
}
