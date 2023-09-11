package com.hevo;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response {
    private List<FileDetail> filesList;
}
