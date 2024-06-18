package com.ecommerce.common.util;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto <T>{
    private List<T> data;
    private Integer totalPage;
    private Integer pageNumber;
    private Integer size;
    private String sort;
}
