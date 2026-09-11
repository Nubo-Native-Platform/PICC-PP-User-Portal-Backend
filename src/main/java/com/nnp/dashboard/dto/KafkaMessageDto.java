package com.nnp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KafkaMessageDto {
    private String key;
    private Object value;
    private int partition;
    private long offset;
    private long timestamp;
}
