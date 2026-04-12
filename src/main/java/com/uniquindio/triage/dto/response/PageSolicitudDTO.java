package com.uniquindio.triage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageSolicitudDTO {

    private List<SolicitudDTO> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}