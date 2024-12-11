package com.hs.service;


import com.hs.entity.LogicResponse;
import com.hs.entity.bo.UserBO;
import com.hs.entity.dto.UserDTO;

import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<LogicResponse<UserBO>> searchUser(UserDTO userDTO);

}
