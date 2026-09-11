package com.nnp.dashboard.client;


import com.nnp.dashboard.dto.SignozResponseDto;

public interface SignozClient {
    SignozResponseDto queryRange(String query);
}
