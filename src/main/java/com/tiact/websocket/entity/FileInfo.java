package com.tiact.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    String name;
    Long size;
    Integer type;
    Date editTime;
    List<FileInfo> children;

}
