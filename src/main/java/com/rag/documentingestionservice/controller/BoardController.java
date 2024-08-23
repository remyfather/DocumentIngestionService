package com.rag.documentingestionservice.controller;

import com.rag.documentingestionservice.dto.UrlExtractRequestDto;
import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;
import com.rag.documentingestionservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board")
    public String list(Model model) {
        List<UrlOptions> urlOptions = boardService.getAllUrlOptions();
        model.addAttribute("urlOptions", urlOptions);
        return "board/board";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("urlOptionsDto", new UrlOptionsDto());
        return "board/register";
    }

    @PostMapping("/save-url-options")
    public String saveUrlOptions(@ModelAttribute UrlOptionsDto urlOptionsDto) {
        boardService.saveUrlOptions(urlOptionsDto);
        return "redirect:board";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        UrlOptions urlOptions = boardService.getUrlOptionsById(id);
        model.addAttribute("urlOptions", urlOptions);
        return "board/detail";
    }

    @PostMapping("/chunk")
    public String chunkData(@RequestParam Long id) {
        // 청킹 로직을 호출하는 서비스 메서드를 호출하세요
        // 예: ragService.chunkData(id);
        return "redirect:board/board";
    }

}
